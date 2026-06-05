package com.example.importdeclaration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "items_declaracion")
public class ItemDeclaracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String ncm;

    @Column(nullable = false, length = 512)
    private String descripcion;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "valor_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorUnitario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "declaracion_id", nullable = false)
    private Declaracion declaracion;

    public Long getId() {
        return id;
    }

    public String getNcm() {
        return ncm;
    }

    public void setNcm(String ncm) {
        this.ncm = ncm;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Declaracion getDeclaracion() {
        return declaracion;
    }

    public void setDeclaracion(Declaracion declaracion) {
        this.declaracion = declaracion;
    }
}
