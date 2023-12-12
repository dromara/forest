package com.dtflys.test.http.address;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.interceptor.Interceptor;

public class AddressInterceptor implements Interceptor<Object> {

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        System.out.println(request.getUrl());
        return proceed();
    }
}
