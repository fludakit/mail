package io.github.fludakit.mail;

/**
 * Exception thrown when mail sending fails.
 */
public class MailException extends RuntimeException {
    
    public MailException(String message) {
        super(message);
    }
    
    public MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
