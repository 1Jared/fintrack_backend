package com.fintrack.api.fintrack.error;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    private  static  final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    ResponseEntity<Map<String, Object>> handleApi(ApiException exception){
        return  error(exception.getStatus() , exception.getMessage());
    }

    private static ResponseEntity <Map<String, Object>> error(HttpStatus status, String message){
        return ResponseEntity.status(status).body(Map.of("error", Map.of("message",message)));
    }

    
}
