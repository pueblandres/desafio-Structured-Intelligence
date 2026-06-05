package com.example.importdeclaration.service.xml;

import com.example.importdeclaration.exception.XmlTransformationException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class XsltTransformationService {

    private static final String XSLT_PATH = "xml/declaracion-a-interno.xsl";

    public String transform(String xml) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

            Transformer transformer = factory.newTransformer(new StreamSource(new ClassPathResource(XSLT_PATH).getInputStream()));
            StringWriter writer = new StringWriter();
            transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
            return writer.toString();
        } catch (Exception ex) {
            throw new XmlTransformationException("No se pudo transformar el XML con declaracion-a-interno.xsl", ex);
        }
    }
}
