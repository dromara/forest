package com.dtflys.forest.exceptions;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 12:42
 */
public class ForestHandlerException extends ForestRuntimeException {

    private ForestRequest request;

    private ForestResponse response;

    public ForestHandlerException(String message, Throwable cause, ForestRequest request, ForestResponse response) {
        super(message, cause);
        this.request = request;
        this.response = response;
    }

    public ForestHandlerException(String message, ForestRequest request, ForestResponse response) {
        super(message);
        this.request = request;
        this.response = response;
    }

    public ForestHandlerException(Throwable cause, ForestRequest request, ForestResponse response) {
        super(cause);
        this.request = request;
        this.response = response;
    }

    public ForestRequest getRequest() {
        return request;
    }

    public void setRequest(ForestRequest request) {
        this.request = request;
    }

    public ForestResponse getResponse() {
        return response;
    }

    public void setResponse(ForestResponse response) {
        this.response = response;
    }
}
