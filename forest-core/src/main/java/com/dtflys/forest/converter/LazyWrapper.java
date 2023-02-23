package com.dtflys.forest.converter;

import com.dtflys.forest.callback.Lazy;
import com.dtflys.forest.http.ForestRequest;

import static com.dtflys.forest.mapping.MappingParameter.TARGET_BODY;

public class LazyWrapper<T> {

    private final ForestRequest request;

    private final String fieldName;

    private final Lazy<T> lazyValue;

    private final int target;


    public LazyWrapper(ForestRequest request, String fieldName, Lazy<T> lazyValue, int target) {
        this.request = request;
        this.fieldName = fieldName;
        this.lazyValue = lazyValue;
        this.target = target;
    }

    public ForestRequest getRequest() {
        return request;
    }

    public Lazy<T> getLazyValue() {
        return lazyValue;
    }

    public T getValue() {
        if (target == TARGET_BODY) {
            request.setCurrentBodyLazyFieldName(fieldName);
        }
        return lazyValue.getValue(request);
    }

}
