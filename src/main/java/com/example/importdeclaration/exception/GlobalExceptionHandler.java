package com.example.importdeclaration.exception;

import com.example.importdeclaration.dto.ErrorResponseDTO;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(XmlValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleXmlValidation(XmlValidationException ex) {
        return build(HttpStatus.BAD_REQUEST, "XML invalido", ex.getMessage());
    }

    @ExceptionHandler(DuplicateDeclarationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicate(DuplicateDeclarationException ex) {
        return build(HttpStatus.CONFLICT, "Declaracion duplicada", ex.getMessage());
    }

    @ExceptionHandler(DeclarationNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(DeclarationNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Declaracion no encontrada", ex.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleBadRequest(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, "Solicitud invalida", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnexpected(Exception ex) {
        log.error("Error inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", "Ocurrio un error inesperado");
    }

    private ResponseEntity<ErrorResponseDTO> build(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(new ErrorResponseDTO(
                status.value(),
                error,
                message,
                LocalDateTime.now()
        ));
    }
}
