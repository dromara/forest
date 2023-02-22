package com.dtflys.forest.utils;

import com.dtflys.forest.callback.Lazy;
import com.dtflys.forest.http.ForestRequest;

public class LazyRequestNameValue extends RequestNameValue {

    private final ForestRequest request;

    public LazyRequestNameValue(ForestRequest request, String name, Object value, int target) {
        super(name, value, target);
        this.request = request;
    }

    public LazyRequestNameValue(ForestRequest request, String name, Lazy value, int target, String partContentType) {
        super(name, value, target, partContentType);
        this.request = request;
    }

    public ForestRequest getRequest() {
        return request;
    }

    @Override
    public Object getValue() {
        if (value instanceof Lazy) {
            return ((Lazy<?>) value).getValue(request);
        }
        return super.getValue();
    }
}
