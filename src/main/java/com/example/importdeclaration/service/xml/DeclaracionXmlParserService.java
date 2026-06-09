package com.example.importdeclaration.service.xml;

import com.example.importdeclaration.dto.DeclaracionInternaDTO;
import com.example.importdeclaration.dto.ItemDeclaracionInternaDTO;
import com.example.importdeclaration.entity.DeclarationStatus;
import com.example.importdeclaration.exception.XmlTransformationException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Service
public class DeclaracionXmlParserService {

    private static final Logger log = LoggerFactory.getLogger(DeclaracionXmlParserService.class);

    public DeclaracionInternaDTO parse(String xmlInterno) {
        try {
            log.debug("Parseando XML interno transformado");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlInterno)));
            Element root = document.getDocumentElement();

            DeclaracionInternaDTO dto = new DeclaracionInternaDTO(
                    text(root, "numeroDespacho"),
                    LocalDate.parse(text(root, "fechaEmision")),
                    text(root, "cuitImportador"),
                    text(root, "moneda"),
                    new BigDecimal(text(root, "totalFOB")),
                    DeclarationStatus.valueOf(text(root, "estado")),
                    parseItems(root)
            );
            log.info("XML interno parseado correctamente. numeroDespacho={} items={}",
                    dto.numeroDespacho(), dto.items().size());
            return dto;
        } catch (Exception ex) {
            log.error("No se pudo parsear el XML interno transformado", ex);
            throw new XmlTransformationException("No se pudo parsear el XML interno transformado", ex);
        }
    }

    private List<ItemDeclaracionInternaDTO> parseItems(Element root) {
        NodeList itemNodes = root.getElementsByTagName("item");
        List<ItemDeclaracionInternaDTO> items = new ArrayList<>();

        for (int i = 0; i < itemNodes.getLength(); i++) {
            Element item = (Element) itemNodes.item(i);
            items.add(new ItemDeclaracionInternaDTO(
                    text(item, "ncm"),
                    text(item, "descripcion"),
                    new BigDecimal(text(item, "cantidad")),
                    new BigDecimal(text(item, "valorUnitario"))
            ));
        }

        return items;
    }

    private String text(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            throw new IllegalArgumentException("Falta el nodo requerido: " + tagName);
        }
        return nodes.item(0).getTextContent().trim();
    }
}
