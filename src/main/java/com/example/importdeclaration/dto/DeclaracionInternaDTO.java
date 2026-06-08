package com.example.importdeclaration.dto;

import com.example.importdeclaration.entity.DeclarationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DeclaracionInternaDTO(
        String numeroDespacho,
        LocalDate fechaEmision,
        String cuitImportador,
        String moneda,
        BigDecimal totalFOB,
        DeclarationStatus estado,
        List<ItemDeclaracionInternaDTO> items
) {
}
