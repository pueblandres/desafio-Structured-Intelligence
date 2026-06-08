package com.example.importdeclaration.controller;

import com.example.importdeclaration.dto.CreateDeclaracionResponseDTO;
import com.example.importdeclaration.dto.DeclaracionResponseDTO;
import com.example.importdeclaration.entity.DeclarationStatus;
import com.example.importdeclaration.service.DeclaracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.net.URI;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
            description = "Recibe una declaracion de importacion en application/xml, la valida contra XSD, aplica XSLT, calcula totalFOB y la persiste. Ejemplos XML disponibles en src/main/resources/xml/.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_XML_VALUE,
                            schema = @Schema(type = "string")
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
            description = "Lista declaraciones paginadas con filtros opcionales por fecha de emision desde/hasta y estado. La paginacion usa los parametros page, size y sort de Spring Data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pagina de declaraciones")
            }
    )
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DeclaracionResponseDTO>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) DeclarationStatus estado,
            @Parameter(description = "Numero de pagina, empezando en 0", schema = @Schema(defaultValue = "0", minimum = "0"))
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de elementos por pagina", schema = @Schema(defaultValue = "20", minimum = "1"))
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(declaracionService.list(desde, hasta, estado, pageable));
    }
}
