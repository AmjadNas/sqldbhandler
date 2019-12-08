package com.amjadnas.sqldbmanager.exceptions;

public class IllegalMethodException extends RuntimeException {

    public IllegalMethodException(Throwable cause) {
        super(cause);
    }

    public IllegalMethodException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }


}
