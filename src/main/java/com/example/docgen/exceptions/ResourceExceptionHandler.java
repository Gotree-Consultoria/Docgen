package com.example.docgen.exceptions;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ResourceExceptionHandler {
	
	// Violação de integridade (Ex.: e-mail duplicado)
		@ExceptionHandler(DataIntegrityViolationException.class)
		public ResponseEntity<StandardError> handleDataIntegrity(DataIntegrityViolationException e,
				HttpServletRequest request) {

			HttpStatus status = HttpStatus.BAD_REQUEST;
			StandardError err = new StandardError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Violação de integridade",
					e.getMessage(), request.getRequestURI());

			return ResponseEntity.status(status).body(err);
		}

		// Recurso não encontrado (Ex.: User ID inexistente)
		@ExceptionHandler(ResourceNotFoundException.class)
		public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException e,
				HttpServletRequest request) {

			HttpStatus status = HttpStatus.BAD_REQUEST;
			StandardError err = new StandardError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Recurso não encontrado",
					e.getMessage(), request.getRequestURI());
			return ResponseEntity.status(status).body(err);

		}

		// Erros de validação de campos (DTO @Valid)
		@ExceptionHandler(MethodArgumentNotValidException.class)
		public ResponseEntity<StandardError> handleValidationErros(MethodArgumentNotValidException e,
				HttpServletRequest request) {

			HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
			StringBuilder sb = new StringBuilder("Erro de validação: ");

			e.getBindingResult().getFieldErrors()
					.forEach(error -> sb.append(String.format("[%s: %s]", error.getField(), error.getDefaultMessage())));

			StandardError err = new StandardError(Instant.now(), status.value(), "Erro de validação", sb.toString(),
					request.getRequestURI());

			return ResponseEntity.status(status).body(err);
			
		

		}

		// Exceções não tratadas (Erro inesperado no sistema)
		@ExceptionHandler(Exception.class)
		public ResponseEntity<StandardError> handleGlobalException(Exception e, HttpServletRequest request) {
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			StandardError err = new StandardError(Instant.now(), status.value(), "Erro interno no servidor", e.getMessage(),
					request.getRequestURI());
			return ResponseEntity.status(status).body(err);
		}
	
	
	
	

}
