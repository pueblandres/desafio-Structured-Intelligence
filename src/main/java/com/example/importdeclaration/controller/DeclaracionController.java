package com.example.importdeclaration.controller;

import com.example.importdeclaration.dto.CreateDeclaracionResponseDTO;
import com.example.importdeclaration.dto.DeclaracionResponseDTO;
import com.example.importdeclaration.entity.DeclarationStatus;
import com.example.importdeclaration.service.DeclaracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.net.URI;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/declaraciones")
public class DeclaracionController {

    private final DeclaracionService declaracionService;

    public DeclaracionController(DeclaracionService declaracionService) {
        this.declaracionService = declaracionService;
    }

    @Operation(
            summary = "Recibir declaracion XML",
            description = "Recibe una declaracion de importacion en application/xml, la valida contra XSD, aplica XSLT, calcula totalFOB y la persiste.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_XML_VALUE,
                            examples = @ExampleObject(
                                    name = "Declaracion valida",
                                    value = """
                                            <?xml version="1.0" encoding="UTF-8"?>
                                            <declaracion>
                                                <numeroDespacho>26001IM04000123A</numeroDespacho>
                                                <fechaEmision>2026-05-12</fechaEmision>
                                                <importador>
                                                    <cuit>30715432109</cuit>
                                                    <razonSocial>Importadora del Plata S.A.</razonSocial>
                                                </importador>
                                                <moneda>USD</moneda>
                                                <items>
                                                    <item>
                                                        <ncm>8471.30.12</ncm>
                                                        <descripcion>Notebook 14 pulgadas</descripcion>
                                                        <cantidad>50</cantidad>
                                                        <valorUnitario>720.00</valorUnitario>
                                                    </item>
                                                </items>
                                            </declaracion>
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Declaracion creada",
                            content = @Content(schema = @Schema(implementation = CreateDeclaracionResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "XML invalido"),
                    @ApiResponse(responseCode = "409", description = "numeroDespacho duplicado"),
                    @ApiResponse(responseCode = "500", description = "Error interno")
            }
    )
    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateDeclaracionResponseDTO> create(@RequestBody String xml) {
        CreateDeclaracionResponseDTO response = declaracionService.createFromXml(xml);
        return ResponseEntity
                .created(URI.create("/api/declaraciones/" + response.numeroDespacho()))
                .body(response);
    }

    @Operation(
            summary = "Consultar declaracion por numeroDespacho",
            description = "Devuelve una declaracion persistida con sus items.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Declaracion encontrada",
                            content = @Content(schema = @Schema(implementation = DeclaracionResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Declaracion no encontrada")
            }
    )
    @GetMapping(path = "/{numeroDespacho}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeclaracionResponseDTO> getByNumeroDespacho(@PathVariable String numeroDespacho) {
        return ResponseEntity.ok(declaracionService.getByNumeroDespacho(numeroDespacho));
    }

    @Operation(
            summary = "Listar declaraciones",
            description = "Lista declaraciones paginadas con filtros opcionales por fecha de emision desde/hasta y estado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pagina de declaraciones")
            }
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DeclaracionResponseDTO>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) DeclarationStatus estado,
            Pageable pageable
    ) {
        return ResponseEntity.ok(declaracionService.list(desde, hasta, estado, pageable));
    }
}
