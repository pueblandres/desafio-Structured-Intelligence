package com.example.importdeclaration.dto;

import com.example.importdeclaration.entity.DeclarationStatus;
import java.time.LocalDate;
import java.util.List;

public record DeclaracionInternaDto(
        String numeroDespacho,
        LocalDate fechaEmision,
        String cuitImportador,
        String moneda,
        DeclarationStatus estado,
        List<ItemDeclaracionInternaDto> items
) {
}
