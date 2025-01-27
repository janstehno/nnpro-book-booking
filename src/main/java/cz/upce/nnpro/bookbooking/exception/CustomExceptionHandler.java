package cz.upce.nnpro.bookbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    public static class EmailExistsException extends RuntimeException {
        public EmailExistsException() {
            super("Email already exists.");
        }
    }

    public static class UsernameExistsException extends RuntimeException {
        public UsernameExistsException() {
            super("Username already exists.");
        }
    }

    public static class UsernameNotFoundException extends RuntimeException {
        public UsernameNotFoundException() {
            super("Username does not exist.");
        }
    }

    public static class PasswordNotCorrectException extends RuntimeException {
        public PasswordNotCorrectException() {
            super("Password is not correct.");
        }
    }

    public static class OldPasswordIncorrectException extends RuntimeException {
        public OldPasswordIncorrectException() {
            super("Old password is not correct.");
        }
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException() {
            super("Invalid authentication token.");
        }
    }

    public static class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException() {
            super("Resource was not found.");
        }
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<String> handleEmailExistsException(EmailExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<String> handleUsernameExistsException(UsernameExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PasswordNotCorrectException.class)
    public ResponseEntity<String> handlePasswordNotCorrectException(PasswordNotCorrectException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(OldPasswordIncorrectException.class)
    public ResponseEntity<String> handleOldPasswordIncorrectException(OldPasswordIncorrectException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenExceptionException(InvalidTokenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}