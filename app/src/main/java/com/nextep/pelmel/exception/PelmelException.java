package com.nextep.pelmel.exception;

/**
 * Created by cfondacci on 09/08/15.
 */
public class PelmelException extends Exception {

    public PelmelException(String msg) {
        super(msg);
    }
    public PelmelException(String msg, Throwable cause) {
        super(msg,cause);
    }
}
