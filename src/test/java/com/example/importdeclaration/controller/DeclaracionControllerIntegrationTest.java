package com.example.importdeclaration.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(statements = {
        "delete from items_declaracion",
        "delete from declaraciones"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DeclaracionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postAndGetDeclaration() throws Exception {
        String xml = readResource("xml/ejemplo-declaracion.xml");

        mockMvc.perform(post("/api/declaraciones")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xml))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.numeroDespacho").value("26001IM04000123A"));

        mockMvc.perform(get("/api/declaraciones/{numeroDespacho}", "26001IM04000123A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroDespacho").value("26001IM04000123A"))
                .andExpect(jsonPath("$.cuitImportador").value("30715432109"))
                .andExpect(jsonPath("$.moneda").value("USD"))
                .andExpect(jsonPath("$.estado").value("RECIBIDA"))
                .andExpect(jsonPath("$.totalFOB").value(41565.00))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].ncm").value("8471.30.12"))
                .andExpect(jsonPath("$.items[1].valorUnitario").value(185.50));
    }

    @Test
    void postInvalidXmlReturnsBadRequest() throws Exception {
        String xml = readResource("xml/ejemplo-declaracion-invalida.xml");

        mockMvc.perform(post("/api/declaraciones")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xml))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("XML invalido"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void postDuplicateDeclarationReturnsConflict() throws Exception {
        String xml = readResource("xml/ejemplo-declaracion.xml");

        mockMvc.perform(post("/api/declaraciones")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xml))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/declaraciones")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xml))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Ya existe una declaracion con numeroDespacho: 26001IM04000123A"));
    }

    @Test
    void listDeclarationsWithFiltersReturnsPagedJson() throws Exception {
        String xml = readResource("xml/ejemplo-declaracion.xml");

        mockMvc.perform(post("/api/declaraciones")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xml))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/declaraciones")
                        .param("desde", "2026-05-01")
                        .param("hasta", "2026-05-31")
                        .param("estado", "RECIBIDA")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].numeroDespacho").value("26001IM04000123A"));
    }

    @Test
    void openApiDocsDocumentXmlRequestAndResourceExamplesPath() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"application/xml\"")))
                .andExpect(content().string(containsString("Ejemplos XML disponibles en src/main/resources/xml/")))
                .andExpect(content().string(containsString("\"type\":\"string\"")))
                .andExpect(content().string(not(containsString("\"Declaracion valida\""))))
                .andExpect(content().string(not(containsString("26001IM04000123A"))))
                .andExpect(content().string(not(containsString("root element name is undefined"))));
    }

    @Test
    void serviceWorkerRequestReturnsStaticNoopScript() throws Exception {
        mockMvc.perform(get("/service-worker.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("registration.unregister")));
    }

    private String readResource(String path) throws Exception {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }
}
