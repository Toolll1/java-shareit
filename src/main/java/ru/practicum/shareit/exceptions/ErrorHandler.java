package ru.practicum.shareit.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

import java.util.Map;

@ControllerAdvice(assignableTypes = {ItemController.class, UserController.class, BookingController.class,
        ItemRequestController.class})
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleObjectNotFound(final ObjectNotFoundException e) {

        return new ResponseEntity<>(
                Map.of("message", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleObjectNotFound(final BadRequestException e) {

        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {

        return new ResponseEntity<>(
                Map.of("error", "Email exist, not created new user"),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleEmptyResultDataAccessException(final EmptyResultDataAccessException e) {

        return new ResponseEntity<>(
                Map.of("error", "There is no object with this idy"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidateException(final ValidateException e) {

        return new ResponseEntity<>(
                Map.of("message", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleRemainingErrors(final RuntimeException e) {

        return new ResponseEntity<>(
                Map.of("message", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
