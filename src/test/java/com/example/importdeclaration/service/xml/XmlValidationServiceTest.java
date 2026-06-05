package com.example.importdeclaration.service.xml;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.importdeclaration.exception.XmlValidationException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class XmlValidationServiceTest {

    private final XmlValidationService service = new XmlValidationService();

    @Test
    void validateAcceptsValidXml() throws Exception {
        String xml = readResource("xml/ejemplo-declaracion.xml");

        assertThatCode(() -> service.validate(xml)).doesNotThrowAnyException();
    }

    @Test
    void validateRejectsInvalidXml() throws Exception {
        String xml = readResource("xml/ejemplo-declaracion-invalida.xml");

        assertThatThrownBy(() -> service.validate(xml))
                .isInstanceOf(XmlValidationException.class)
                .hasMessageContaining("declaracion.xsd");
    }

    private String readResource(String path) throws Exception {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }
}
