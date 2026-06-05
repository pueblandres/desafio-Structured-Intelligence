package com.example.importdeclaration.repository;

import com.example.importdeclaration.entity.Declaracion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeclaracionRepository extends JpaRepository<Declaracion, Long>, JpaSpecificationExecutor<Declaracion> {

    boolean existsByNumeroDespacho(String numeroDespacho);

    Optional<Declaracion> findByNumeroDespacho(String numeroDespacho);
}
