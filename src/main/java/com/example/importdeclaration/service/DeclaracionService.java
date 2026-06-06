package com.example.importdeclaration.service;

import com.example.importdeclaration.dto.CreateDeclaracionResponseDTO;
import com.example.importdeclaration.dto.DeclaracionResponseDTO;
import com.example.importdeclaration.entity.DeclarationStatus;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeclaracionService {

    CreateDeclaracionResponseDTO createFromXml(String xml);

    DeclaracionResponseDTO getByNumeroDespacho(String numeroDespacho);

    Page<DeclaracionResponseDTO> list(LocalDate desde, LocalDate hasta, DeclarationStatus estado, Pageable pageable);
}
