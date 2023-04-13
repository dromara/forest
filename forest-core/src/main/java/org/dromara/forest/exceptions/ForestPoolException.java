package org.dromara.forest.exceptions;

import org.dromara.forest.http.ForestRequest;

public abstract class ForestPoolException extends ForestRuntimeException {

    protected ForestRequest request;

    public ForestPoolException(ForestRequest request, String message) {
        super(message);
        this.request = request;
    }

    public ForestRequest getRequest() {
        return request;
    }

    public void setRequest(ForestRequest request) {
        this.request = request;
    }
}
