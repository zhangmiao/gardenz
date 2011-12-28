package org.walkmanz.gardenz.util;

public class ReflectException extends RuntimeException{
	
    public ReflectException() {
        super();
    }

    public ReflectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectException(String message) {
        super(message);
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }
}