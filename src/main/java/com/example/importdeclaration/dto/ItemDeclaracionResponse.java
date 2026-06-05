package com.example.importdeclaration.dto;

import java.math.BigDecimal;

public record ItemDeclaracionResponse(
        Long id,
        String ncm,
        String descripcion,
        BigDecimal cantidad,
        BigDecimal valorUnitario
) {
}
