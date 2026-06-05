package com.example.importdeclaration.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.importdeclaration.dto.DeclaracionInternaDto;
import com.example.importdeclaration.dto.ItemDeclaracionInternaDto;
import com.example.importdeclaration.entity.DeclarationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeclaracionServiceTest {

    @Test
    void calculateTotalFOBUsesQuantityTimesUnitValueForAllItems() {
        DeclaracionService service = new DeclaracionService(null, null, null, null, null);
        DeclaracionInternaDto dto = new DeclaracionInternaDto(
                "26001IM04000123A",
                LocalDate.parse("2026-05-12"),
                "30715432109",
                "USD",
                DeclarationStatus.RECIBIDA,
                List.of(
                        new ItemDeclaracionInternaDto("8471.30.12", "Notebook", new BigDecimal("50"), new BigDecimal("720.00")),
                        new ItemDeclaracionInternaDto("8528.72.00", "Monitor", new BigDecimal("30"), new BigDecimal("185.50"))
                )
        );

        assertThat(service.calculateTotalFOB(dto)).isEqualByComparingTo(new BigDecimal("41565.00"));
    }
}
