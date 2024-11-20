package cz.upce.nnpro.bookbooking.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    public static class EmailExistsException extends RuntimeException {
        public EmailExistsException() {
            super("EMAIL_EXISTS");
        }
    }

    public static class UsernameExistsException extends RuntimeException {
        public UsernameExistsException() {
            super("USERNAME_EXISTS");
        }
    }

    public static class PasswordNotCorrectException extends RuntimeException {
        public PasswordNotCorrectException() {
            super("PASSWORD_NOT_CORRECT");
        }
    }

    public static class OldPasswordIncorrectException extends RuntimeException {
        public OldPasswordIncorrectException() {
            super("OLD_PASSWORD_INCORRECT");
        }
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException() {
            super("INVALID_TOKEN");
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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}