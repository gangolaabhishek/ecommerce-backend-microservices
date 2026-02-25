package com.ecommerce.product_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotfound(ProductNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {

        return new ResponseEntity<>(
                "Something went wrong: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
