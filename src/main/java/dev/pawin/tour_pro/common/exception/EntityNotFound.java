package dev.pawin.tour_pro.common.exception;

public class EntityNotFound extends RuntimeException{
    
    public EntityNotFound() {
        super();
    }

    public EntityNotFound(String message) {
        super(message);
    }


}
