package org.forest.exceptions;

import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

/**
 * Created by Administrator on 2016/5/30.
 */
public class ForestRuntimeException extends RuntimeException {

    private Integer errorCode;

    private ForestRequest request;

    private ForestResponse response;

    public Integer getErrorCode() {
        return errorCode;
    }


    public ForestRuntimeException(ForestNetworkException cause) {
        super(cause);
        this.errorCode = cause.getStatusCode();
    }

    public ForestRuntimeException(String message) {
        super(message);
    }

    public ForestRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForestRuntimeException(Throwable cause, ForestRequest request, ForestResponse response) {
        super(cause);
        this.request = request;
        this.response = response;
    }

    public ForestRuntimeException(Throwable cause) {
        super(cause);
    }


    public ForestRequest getRequest() {
        return request;
    }

    public ForestResponse getResponse() {
        return response;
    }
}
