package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTests {
    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void handleValidateException() {

        //given
        ResponseEntity<Map<String, String>> x = errorHandler.handleValidateException(new ValidateException("ValidateException"));

        //then
        assertEquals(x, new ResponseEntity<>(
                Map.of("message", "ValidateException"),
                HttpStatus.INTERNAL_SERVER_ERROR
        ));
    }

    @Test
    public void handleObjectNotFound() {

        //given
        ResponseEntity<Map<String, String>> x = errorHandler.handleObjectNotFound(new ObjectNotFoundException("not found"));

        //then
        assertEquals(x, new ResponseEntity<>(
                Map.of("message", "not found"),
                HttpStatus.NOT_FOUND
        ));
    }

    @Test
    public void handleObjectNotFound2() {

        //given
        ResponseEntity<Map<String, String>> x = errorHandler.handleObjectNotFound(new BadRequestException("not found"));

        //then
        assertEquals(x, new ResponseEntity<>(
                Map.of("error", "not found"),
                HttpStatus.BAD_REQUEST
        ));
    }

    @Test
    public void handleDataIntegrityViolationException() {

        //given
        ResponseEntity<Map<String, String>> x = errorHandler.handleDataIntegrityViolationException(
                new DataIntegrityViolationException("Email exist, not created new user"));

        //then
        assertEquals(x, new ResponseEntity<>(
                Map.of("error", "Email exist, not created new user"),
                HttpStatus.CONFLICT
        ));
    }

    @Test
    public void handleRemainingErrors() {

        //given
        ResponseEntity<Map<String, String>> x = errorHandler.handleRemainingErrors(
                new RuntimeException("RuntimeException"));

        //then
        assertEquals(x, new ResponseEntity<>(
                Map.of("message", "RuntimeException"),
                HttpStatus.INTERNAL_SERVER_ERROR
        ));
    }

    @Test
    public void handleEmptyResultDataAccessException() {

        //given
        ResponseEntity<Map<String, String>> x = errorHandler.handleEmptyResultDataAccessException(
                new EmptyResultDataAccessException(1));

        //then
        assertEquals(x, new ResponseEntity<>(
                Map.of("error", "There is no object with this idy"),
                HttpStatus.BAD_REQUEST
        ));
    }
}

