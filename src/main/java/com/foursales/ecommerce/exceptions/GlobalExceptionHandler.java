package com.foursales.ecommerce.exceptions;

import com.foursales.ecommerce.resource.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(
            RuntimeException e, HttpServletRequest request) {

        return buildErrorResponse(request, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentialsException(
            BadCredentialsException e, HttpServletRequest request) {

        return buildErrorResponse(request, e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        return buildErrorResponse(request, e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleSecurityException(
            SecurityException e, HttpServletRequest request) {

        return buildErrorResponse(request, e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(
            EntityNotFoundException e, HttpServletRequest request) {

        return buildErrorResponse(request, e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalStateException(
            IllegalStateException e, HttpServletRequest request) {

        return buildErrorResponse(request, e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleIllegalStateException(
            BusinessException e, HttpServletRequest request) {

        return buildErrorResponse(request, e.getMessage(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        List<Map<String, String>> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "error", fieldError.getDefaultMessage()
                )).toList();

        return buildErrorResponse(request, errors, HttpStatus.BAD_REQUEST);
    }

    private static ErrorResponse buildErrorResponse(
            HttpServletRequest request, Object errors, HttpStatus httpStatus) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(errors)
                .path(request.getRequestURI())
                .build();
    }
}
