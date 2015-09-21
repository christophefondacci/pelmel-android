package com.nextep.pelmel.exception;

import java.io.InputStream;

/**
 * Created by cfondacci on 16/09/15.
 */
public class PelmelWebServiceException extends PelmelException {

    int statusCode;
    InputStream responseStream;

    public PelmelWebServiceException(int statusCode, InputStream responseStream) {
        this(statusCode,responseStream,null);
    }
    public PelmelWebServiceException(int statusCode, InputStream responseStream, Throwable cause) {
        super("Webservice error " + statusCode,cause);
        this.statusCode = statusCode;
        this.responseStream = responseStream;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public InputStream getResponse() {
        return responseStream;
    }
}
