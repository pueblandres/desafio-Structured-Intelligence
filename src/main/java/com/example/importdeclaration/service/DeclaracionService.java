package com.example.importdeclaration.service;

import com.example.importdeclaration.dto.CreateDeclaracionResponse;
import com.example.importdeclaration.dto.DeclaracionInternaDto;
import com.example.importdeclaration.dto.DeclaracionResponse;
import com.example.importdeclaration.dto.ItemDeclaracionInternaDto;
import com.example.importdeclaration.entity.DeclarationStatus;
import com.example.importdeclaration.entity.Declaracion;
import com.example.importdeclaration.exception.DeclarationNotFoundException;
import com.example.importdeclaration.exception.DuplicateDeclarationException;
import com.example.importdeclaration.mapper.DeclaracionMapper;
import com.example.importdeclaration.repository.DeclaracionRepository;
import com.example.importdeclaration.repository.DeclaracionSpecifications;
import com.example.importdeclaration.service.xml.DeclaracionXmlParserService;
import com.example.importdeclaration.service.xml.XmlValidationService;
import com.example.importdeclaration.service.xml.XsltTransformationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeclaracionService {

    private final XmlValidationService xmlValidationService;
    private final XsltTransformationService xsltTransformationService;
    private final DeclaracionXmlParserService declaracionXmlParserService;
    private final DeclaracionRepository declaracionRepository;
    private final DeclaracionMapper declaracionMapper;

    public DeclaracionService(
            XmlValidationService xmlValidationService,
            XsltTransformationService xsltTransformationService,
            DeclaracionXmlParserService declaracionXmlParserService,
            DeclaracionRepository declaracionRepository,
            DeclaracionMapper declaracionMapper
    ) {
        this.xmlValidationService = xmlValidationService;
        this.xsltTransformationService = xsltTransformationService;
        this.declaracionXmlParserService = declaracionXmlParserService;
        this.declaracionRepository = declaracionRepository;
        this.declaracionMapper = declaracionMapper;
    }

    @Transactional
    public CreateDeclaracionResponse createFromXml(String xml) {
        xmlValidationService.validate(xml);
        String xmlInterno = xsltTransformationService.transform(xml);
        DeclaracionInternaDto dto = declaracionXmlParserService.parse(xmlInterno);

        if (declaracionRepository.existsByNumeroDespacho(dto.numeroDespacho())) {
            throw new DuplicateDeclarationException(dto.numeroDespacho());
        }

        BigDecimal totalFOB = calculateTotalFOB(dto);
        Declaracion declaracion = declaracionMapper.toEntity(dto, totalFOB);
        Declaracion saved = declaracionRepository.save(declaracion);
        return new CreateDeclaracionResponse(saved.getId(), saved.getNumeroDespacho());
    }

    @Transactional(readOnly = true)
    public DeclaracionResponse getByNumeroDespacho(String numeroDespacho) {
        return declaracionRepository.findByNumeroDespacho(numeroDespacho)
                .map(declaracionMapper::toResponse)
                .orElseThrow(() -> new DeclarationNotFoundException(numeroDespacho));
    }

    @Transactional(readOnly = true)
    public Page<DeclaracionResponse> list(LocalDate desde, LocalDate hasta, DeclarationStatus estado, Pageable pageable) {
        Specification<Declaracion> specification = Specification
                .where(DeclaracionSpecifications.fechaDesde(desde))
                .and(DeclaracionSpecifications.fechaHasta(hasta))
                .and(DeclaracionSpecifications.estado(estado));

        return declaracionRepository.findAll(specification, pageable)
                .map(declaracionMapper::toResponse);
    }

    public BigDecimal calculateTotalFOB(DeclaracionInternaDto dto) {
        return dto.items().stream()
                .map(this::calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemTotal(ItemDeclaracionInternaDto item) {
        return item.cantidad().multiply(item.valorUnitario());
    }
}
