package com.example.importdeclaration.service.xml;

import com.example.importdeclaration.exception.XmlValidationException;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class XmlValidationService {

    private static final String XSD_PATH = "xml/declaracion.xsd";

    public void validate(String xml) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Schema schema = factory.newSchema(new StreamSource(new ClassPathResource(XSD_PATH).getInputStream()));
            schema.newValidator().validate(new StreamSource(new StringReader(xml)));
        } catch (Exception ex) {
            throw new XmlValidationException("El XML no cumple con el esquema declaracion.xsd: " + ex.getMessage(), ex);
        }
    }
}
