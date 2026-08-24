package com.ecommerce.order_service.exception;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {
  
  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
    
    log.warn("resource not found - Path: {}, Message: {}", request.getDescription(false), ex.getMessage());
    
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    
    problemDetail.setTitle("Resource not found");
    problemDetail.setType(URI.create("https://api.ecommerce.com/errors/not-found"));
    problemDetail.setProperty("Timestamp", Instant.now());

    problemDetail.setProperty("Resource", ex.getResourceName());
    problemDetail.setProperty("Field", ex.getFieldName());
    problemDetail.setProperty("Value", ex.getFieldValue());

    return problemDetail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "validation failed in one or more fields");

    problemDetail.setTitle("Validation error");
    problemDetail.setType(URI.create("https://api.ecommerce.com/errors/error-validation"));
    problemDetail.setProperty("Timestamp", Instant.now());

    Map<String, String> errorMap = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(
      error -> errorMap.put(error.getField(), error.getDefaultMessage())
    );


    problemDetail.setProperty("errors", errorMap);

    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception ex, WebRequest request){

    log.error("an unexpected error occurred {}: {}", request.getDescription(false), ex.getMessage(), ex);

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "an unexpected error occurred. Please contact the administrator");

    problemDetail.setTitle("Internal server error");
    problemDetail.setType(URI.create("https://api.ecommerce.com/errors/internal"));
    problemDetail.setProperty("Timestamp", Instant.now());


    return problemDetail;
  }

}
