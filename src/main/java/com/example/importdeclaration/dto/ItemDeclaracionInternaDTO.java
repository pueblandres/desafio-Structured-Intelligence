package com.example.importdeclaration.dto;

import java.math.BigDecimal;

public record ItemDeclaracionInternaDTO(
        String ncm,
        String descripcion,
        BigDecimal cantidad,
        BigDecimal valorUnitario
) {
}
