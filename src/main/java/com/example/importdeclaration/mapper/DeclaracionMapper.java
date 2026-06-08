package com.example.importdeclaration.mapper;

import com.example.importdeclaration.dto.DeclaracionInternaDTO;
import com.example.importdeclaration.dto.DeclaracionResponseDTO;
import com.example.importdeclaration.dto.ItemDeclaracionInternaDTO;
import com.example.importdeclaration.dto.ItemDeclaracionResponseDTO;
import com.example.importdeclaration.entity.Declaracion;
import com.example.importdeclaration.entity.ItemDeclaracion;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class DeclaracionMapper {

    public Declaracion toEntity(DeclaracionInternaDTO dto) {
        Declaracion declaracion = Declaracion.builder()
                .numeroDespacho(dto.numeroDespacho())
                .fechaEmision(dto.fechaEmision())
                .cuitImportador(dto.cuitImportador())
                .moneda(dto.moneda())
                .estado(dto.estado())
                .totalFOB(dto.totalFOB())
                .fechaRecepcion(LocalDateTime.now())
                .build();

        dto.items().stream()
                .map(this::toEntity)
                .forEach(declaracion::addItem);

        return declaracion;
    }

    public DeclaracionResponseDTO toResponse(Declaracion declaracion) {
        return new DeclaracionResponseDTO(
                declaracion.getId(),
                declaracion.getNumeroDespacho(),
                declaracion.getFechaEmision(),
                declaracion.getCuitImportador(),
                declaracion.getTotalFOB(),
                declaracion.getMoneda(),
                declaracion.getEstado(),
                declaracion.getFechaRecepcion(),
                declaracion.getItems().stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private ItemDeclaracion toEntity(ItemDeclaracionInternaDTO dto) {
        return ItemDeclaracion.builder()
                .ncm(dto.ncm())
                .descripcion(dto.descripcion())
                .cantidad(dto.cantidad())
                .valorUnitario(dto.valorUnitario())
                .build();
    }

    private ItemDeclaracionResponseDTO toResponse(ItemDeclaracion item) {
        return new ItemDeclaracionResponseDTO(
                item.getId(),
                item.getNcm(),
                item.getDescripcion(),
                item.getCantidad(),
                item.getValorUnitario()
        );
    }
}
