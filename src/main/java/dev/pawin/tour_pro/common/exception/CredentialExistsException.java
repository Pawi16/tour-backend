package dev.pawin.tour_pro.common.exception;

public class CredentialExistsException extends RuntimeException {
    
    public CredentialExistsException() {
        super();
    }

    public CredentialExistsException(String message) {
        super(message);
    }
}
