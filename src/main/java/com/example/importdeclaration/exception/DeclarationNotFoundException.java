package com.example.importdeclaration.exception;

public class DeclarationNotFoundException extends RuntimeException {

    public DeclarationNotFoundException(String numeroDespacho) {
        super("No existe una declaracion con numeroDespacho: " + numeroDespacho);
    }
}
