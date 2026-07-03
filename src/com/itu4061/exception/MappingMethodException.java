package com.itu4061.exception;

public class MappingMethodException extends FrameworkException4061{

    public MappingMethodException() {
    }

    public MappingMethodException(String message) {
        super(message);
    }

    public MappingMethodException(Throwable cause) {
        super(cause);
    }

    public MappingMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingMethodException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
