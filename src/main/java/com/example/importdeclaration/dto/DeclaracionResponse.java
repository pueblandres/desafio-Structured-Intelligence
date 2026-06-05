package com.example.importdeclaration.dto;

import com.example.importdeclaration.entity.DeclarationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DeclaracionResponse(
        Long id,
        String numeroDespacho,
        LocalDate fechaEmision,
        String cuitImportador,
        BigDecimal totalFOB,
        String moneda,
        DeclarationStatus estado,
        LocalDateTime fechaRecepcion,
        List<ItemDeclaracionResponse> items
) {
}
