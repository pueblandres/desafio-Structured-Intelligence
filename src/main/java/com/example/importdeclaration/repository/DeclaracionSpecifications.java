package com.example.importdeclaration.repository;

import com.example.importdeclaration.entity.DeclarationStatus;
import com.example.importdeclaration.entity.Declaracion;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public final class DeclaracionSpecifications {

    private DeclaracionSpecifications() {
    }

    public static Specification<Declaracion> fechaDesde(LocalDate desde) {
        return (root, query, cb) -> desde == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("fechaEmision"), desde);
    }

    public static Specification<Declaracion> fechaHasta(LocalDate hasta) {
        return (root, query, cb) -> hasta == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("fechaEmision"), hasta);
    }

    public static Specification<Declaracion> estado(DeclarationStatus estado) {
        return (root, query, cb) -> estado == null ? cb.conjunction() : cb.equal(root.get("estado"), estado);
    }
}
