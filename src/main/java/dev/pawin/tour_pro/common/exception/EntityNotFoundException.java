package dev.pawin.tour_pro.common.exception;

public class EntityNotFoundException extends RuntimeException{
    
    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message) {
        super(message);
    }


}
