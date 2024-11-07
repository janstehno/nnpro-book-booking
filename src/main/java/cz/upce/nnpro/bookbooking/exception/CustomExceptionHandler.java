package cz.upce.nnpro.bookbooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    public static class EmailExistsException extends RuntimeException {
        public EmailExistsException() {
            super("EMAIL_EXISTS");
        }
    }

    public static class OldPasswordIncorrectException extends RuntimeException {
        public OldPasswordIncorrectException() {
            super("OLD_PASSWORD_INCORRECT");
        }
    }

    public static class ItemNotFoundException extends RuntimeException {
        public ItemNotFoundException() {
            super();
        }
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<String> handleEmailExistsException(EmailExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<String> handleOldPasswordIncorrectException(OldPasswordIncorrectException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<String> handleItemNotFoundException() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

