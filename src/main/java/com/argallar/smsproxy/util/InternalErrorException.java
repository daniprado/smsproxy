package com.argallar.smsproxy.util;

/**
 * Marker exception to show something odd has happened.
 */
public class InternalErrorException extends RuntimeException {

    private static final long serialVersionUID = 4749445612911169233L;

    public InternalErrorException(String msg) {
        super(msg);
    }
    
    public InternalErrorException(String msg, Throwable e) {
        super(msg, e);
    }
}
