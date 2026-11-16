package br.com.globalsolution.findme.configurations;

import br.com.globalsolution.findme.exceptions.*;
import br.com.globalsolution.findme.dto.ErrorCustomResponse;
import br.com.globalsolution.findme.dto.ErrorFieldCustom;
import br.com.globalsolution.findme.dto.ValidationErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorCustomResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorCustomResponse> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Recurso não encontrado");
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorCustomResponse> handleInvalidStatusTransition(InvalidStatusTransitionException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(CasoNotEditableException.class)
    public ResponseEntity<ErrorCustomResponse> handleCasoNotEditable(CasoNotEditableException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AlertaAlreadyExistsException.class)
    public ResponseEntity<ErrorCustomResponse> handleAlertaAlreadyExists(AlertaAlreadyExistsException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ErrorCustomResponse> handleEmailAlreadyRegistered(EmailAlreadyRegisteredException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorCustomResponse> handleAuthentication(AuthenticationException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorCustomResponse> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Você não tem permissão para executar esta ação");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorCustomResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou malformado");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorCustomResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Parâmetro '" + ex.getName() + "' inválido: " + ex.getValue());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorCustomResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório ausente: " + ex.getParameterName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorCustomResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP não suportado: " + ex.getMethod());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorCustomResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<ErrorFieldCustom> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorFieldCustom(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        List<ErrorFieldCustom> globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(globalError -> new ErrorFieldCustom(globalError.getObjectName(), globalError.getDefaultMessage()))
                .toList();

        List<ErrorFieldCustom> errors = Stream.concat(fieldErrors.stream(), globalErrors.stream()).toList();

        ValidationErrorResponse body = new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errors
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorCustomResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorCustomResponse> handleGeneric(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado");
    }

    private ResponseEntity<ErrorCustomResponse> build(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorCustomResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message));
    }
}
