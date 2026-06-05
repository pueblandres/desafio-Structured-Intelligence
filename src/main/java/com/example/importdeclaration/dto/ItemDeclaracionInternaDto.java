package com.example.importdeclaration.dto;

import java.math.BigDecimal;

public record ItemDeclaracionInternaDto(
        String ncm,
        String descripcion,
        BigDecimal cantidad,
        BigDecimal valorUnitario
) {
}
