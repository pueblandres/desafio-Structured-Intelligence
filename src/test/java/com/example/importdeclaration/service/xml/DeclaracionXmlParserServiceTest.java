package com.example.importdeclaration.service.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class DeclaracionXmlParserServiceTest {

    private final XsltTransformationService transformationService = new XsltTransformationService();
    private final DeclaracionXmlParserService parserService = new DeclaracionXmlParserService();

    @Test
    void parseReadsInternalDeclarationIncludingTotalFOB() throws Exception {
        String xml = new ClassPathResource("xml/ejemplo-declaracion.xml").getContentAsString(StandardCharsets.UTF_8);
        String xmlInterno = transformationService.transform(xml);

        var dto = parserService.parse(xmlInterno);

        assertThat(dto.numeroDespacho()).isEqualTo("26001IM04000123A");
        assertThat(dto.totalFOB()).isEqualByComparingTo("41565.00");
        assertThat(dto.items()).hasSize(2);
    }
}
