package com.adventure.battle.commands.exception;

import com.adventure.battle.commands.models.APIError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler
 */
@RestControllerAdvice
public class DefaultExceptionHandler {
    @ExceptionHandler(CommandException.class)
    protected ResponseEntity<Object> handleBadException(CommandException exception) {
        APIError apiError = new APIError();
        apiError.setCode(String.valueOf(exception.statusCode));
        apiError.setMessage(exception.getMessage());
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), HttpStatus.valueOf(apiError.getCode()));
    }
}
