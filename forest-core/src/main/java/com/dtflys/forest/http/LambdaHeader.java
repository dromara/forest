package com.dtflys.forest.http;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public class LambdaHeader implements ForestHeader<LambdaHeader, Function<ForestRequest, String>> {

    private final WeakReference<ForestRequest> requestRef;

    private final String name;

    private Function<ForestRequest, String> lambdaValue;

    public LambdaHeader(ForestRequest request, String name, Function<ForestRequest, String> lambdaValue) {
        this.requestRef = new WeakReference<>(request);
        this.name = name;
        this.lambdaValue = lambdaValue;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        ForestRequest request = requestRef.get();
        if (request == null) {
            throw new ForestRuntimeException(
                    "the request reference of header[name=" + name + "] has been dropped");
        }
        if (lambdaValue == null) {
            throw new ForestRuntimeException("the lambda function of header[name=" + name + "] is null");
        }
        return lambdaValue.apply(request);
    }

    @Override
    public LambdaHeader setValue(Function<ForestRequest, String> lambdaValue) {
        this.lambdaValue = lambdaValue;
        return this;
    }
}
