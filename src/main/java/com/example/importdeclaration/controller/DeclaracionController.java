package com.example.importdeclaration.controller;

import com.example.importdeclaration.dto.CreateDeclaracionResponse;
import com.example.importdeclaration.dto.DeclaracionResponse;
import com.example.importdeclaration.entity.DeclarationStatus;
import com.example.importdeclaration.service.DeclaracionService;
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

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateDeclaracionResponse> create(@RequestBody String xml) {
        CreateDeclaracionResponse response = declaracionService.createFromXml(xml);
        return ResponseEntity
                .created(URI.create("/api/declaraciones/" + response.numeroDespacho()))
                .body(response);
    }

    @GetMapping(path = "/{numeroDespacho}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeclaracionResponse> getByNumeroDespacho(@PathVariable String numeroDespacho) {
        return ResponseEntity.ok(declaracionService.getByNumeroDespacho(numeroDespacho));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DeclaracionResponse>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) DeclarationStatus estado,
            Pageable pageable
    ) {
        return ResponseEntity.ok(declaracionService.list(desde, hasta, estado, pageable));
    }
}
