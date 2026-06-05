package com.example.importdeclaration.exception;

public class DuplicateDeclarationException extends RuntimeException {

    public DuplicateDeclarationException(String numeroDespacho) {
        super("Ya existe una declaracion con numeroDespacho: " + numeroDespacho);
    }
}
