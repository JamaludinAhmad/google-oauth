package com.example.hundredtrygoogle.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exceptionHandler(Exception ex){
        return ResponseEntity.status(400).body(ex.getMessage());
    }

}
