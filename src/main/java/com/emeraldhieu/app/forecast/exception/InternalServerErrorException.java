package com.emeraldhieu.app.forecast.exception;

public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return "Internal server error: " + getMessage();
    }

}
