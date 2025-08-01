package com.project_management.ds.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<String> handleBusinessException(ProjectException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // <-- Faltava essa linha
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleEnumParseError(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        String mensagem = ex.getMessage();

        if (mensagem != null && mensagem.contains("Cargo")) {
            response.put("atribuicao", "Atribuição inválida. Use FUNCIONARIO ou GERENTE.");
        } else if (mensagem != null && mensagem.contains("StatusProject")) {
            response.put("status", "Status inválido. Use um dos valores permitidos como EM_ANALISE, ANALISE_REALIZADA, etc.");
        } else {
            response.put("erro", "Erro de leitura na requisição. Verifique os dados enviados.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
