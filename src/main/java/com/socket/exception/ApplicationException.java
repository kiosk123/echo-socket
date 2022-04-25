package com.socket.exception;

public class ApplicationException extends Exception {
    
    private String message;

    public ApplicationException(Throwable e) {
        super(e);
    }

    public ApplicationException(String message) {
        this.message = message;
    }
    
    public ApplicationException(String message, Throwable e) {
        super(e);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
