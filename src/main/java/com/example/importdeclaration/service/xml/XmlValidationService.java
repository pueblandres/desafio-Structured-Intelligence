package com.example.importdeclaration.service.xml;

import com.example.importdeclaration.exception.XmlValidationException;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.springframework.core.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class XmlValidationService {

    private static final Logger log = LoggerFactory.getLogger(XmlValidationService.class);
    private static final String XSD_PATH = "xml/declaracion.xsd";

    public void validate(String xml) {
        try {
            log.debug("Validando XML contra XSD. xsdPath={}", XSD_PATH);
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Schema schema = factory.newSchema(new StreamSource(new ClassPathResource(XSD_PATH).getInputStream()));
            schema.newValidator().validate(new StreamSource(new StringReader(xml)));
            log.info("XML validado correctamente contra XSD");
        } catch (Exception ex) {
            log.warn("XML invalido contra XSD. detalle={}", ex.getMessage());
            throw new XmlValidationException("El XML no cumple con el esquema declaracion.xsd: " + ex.getMessage(), ex);
        }
    }
}
