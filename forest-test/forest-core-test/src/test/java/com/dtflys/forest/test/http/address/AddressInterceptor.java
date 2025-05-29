package com.dtflys.forest.test.http.address;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;

public class AddressInterceptor implements Interceptor<Object> {

    @Override
    public boolean beforeExecute(ForestRequest request) {
        System.out.println(request.getUrl());
        return true;
    }
}
