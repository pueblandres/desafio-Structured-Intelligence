package com.example.importdeclaration.mapper;

import com.example.importdeclaration.dto.DeclaracionInternaDto;
import com.example.importdeclaration.dto.DeclaracionResponse;
import com.example.importdeclaration.dto.ItemDeclaracionInternaDto;
import com.example.importdeclaration.dto.ItemDeclaracionResponse;
import com.example.importdeclaration.entity.Declaracion;
import com.example.importdeclaration.entity.ItemDeclaracion;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class DeclaracionMapper {

    public Declaracion toEntity(DeclaracionInternaDto dto, BigDecimal totalFOB) {
        Declaracion declaracion = new Declaracion();
        declaracion.setNumeroDespacho(dto.numeroDespacho());
        declaracion.setFechaEmision(dto.fechaEmision());
        declaracion.setCuitImportador(dto.cuitImportador());
        declaracion.setMoneda(dto.moneda());
        declaracion.setEstado(dto.estado());
        declaracion.setTotalFOB(totalFOB);
        declaracion.setFechaRecepcion(LocalDateTime.now());
        dto.items().stream()
                .map(this::toEntity)
                .forEach(declaracion::addItem);
        return declaracion;
    }

    public DeclaracionResponse toResponse(Declaracion declaracion) {
        return new DeclaracionResponse(
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

    private ItemDeclaracion toEntity(ItemDeclaracionInternaDto dto) {
        ItemDeclaracion item = new ItemDeclaracion();
        item.setNcm(dto.ncm());
        item.setDescripcion(dto.descripcion());
        item.setCantidad(dto.cantidad());
        item.setValorUnitario(dto.valorUnitario());
        return item;
    }

    private ItemDeclaracionResponse toResponse(ItemDeclaracion item) {
        return new ItemDeclaracionResponse(
                item.getId(),
                item.getNcm(),
                item.getDescripcion(),
                item.getCantidad(),
                item.getValorUnitario()
        );
    }
}
