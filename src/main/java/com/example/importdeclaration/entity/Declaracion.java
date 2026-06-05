package com.example.importdeclaration.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "declaraciones",
        indexes = {
                @Index(name = "idx_declaracion_numero_despacho", columnList = "numero_despacho", unique = true),
                @Index(name = "idx_declaracion_fecha_emision", columnList = "fecha_emision"),
                @Index(name = "idx_declaracion_estado", columnList = "estado")
        }
)
public class Declaracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_despacho", nullable = false, unique = true, length = 32)
    private String numeroDespacho;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "cuit_importador", nullable = false, length = 16)
    private String cuitImportador;

    @Column(name = "total_fob", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalFOB;

    @Column(nullable = false, length = 8)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private DeclarationStatus estado;

    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDateTime fechaRecepcion;

    @OneToMany(mappedBy = "declaracion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDeclaracion> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getNumeroDespacho() {
        return numeroDespacho;
    }

    public void setNumeroDespacho(String numeroDespacho) {
        this.numeroDespacho = numeroDespacho;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getCuitImportador() {
        return cuitImportador;
    }

    public void setCuitImportador(String cuitImportador) {
        this.cuitImportador = cuitImportador;
    }

    public BigDecimal getTotalFOB() {
        return totalFOB;
    }

    public void setTotalFOB(BigDecimal totalFOB) {
        this.totalFOB = totalFOB;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public DeclarationStatus getEstado() {
        return estado;
    }

    public void setEstado(DeclarationStatus estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public List<ItemDeclaracion> getItems() {
        return items;
    }

    public void addItem(ItemDeclaracion item) {
        items.add(item);
        item.setDeclaracion(this);
    }
}
