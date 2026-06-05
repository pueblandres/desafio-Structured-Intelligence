package com.example.importdeclaration.service.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class XsltTransformationServiceTest {

    private final XsltTransformationService service = new XsltTransformationService();

    @Test
    void transformMapsExternalDeclarationToInternalXml() throws Exception {
        String xml = new ClassPathResource("xml/ejemplo-declaracion.xml").getContentAsString(StandardCharsets.UTF_8);

        String transformed = service.transform(xml);

        assertThat(transformed)
                .contains("<declaracionInterna>")
                .contains("<numeroDespacho>26001IM04000123A</numeroDespacho>")
                .contains("<cuitImportador>30715432109</cuitImportador>")
                .contains("<moneda>USD</moneda>")
                .contains("<estado>RECIBIDA</estado>")
                .contains("<ncm>8471.30.12</ncm>")
                .contains("<valorUnitario>185.50</valorUnitario>");
    }
}
