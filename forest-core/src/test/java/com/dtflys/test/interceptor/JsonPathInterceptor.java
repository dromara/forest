package com.dtflys.test.interceptor;

import com.alibaba.fastjson2.JSON;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.ResponseResult;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import java.lang.reflect.Type;
import java.util.Collection;

public class JsonPathInterceptor implements Interceptor<Object> {

    @Override
    public ResponseResult onResponse(ForestRequest request, ForestResponse response) {
        if (response.isError()) {
            return error();
        }
        final String jsonStr = response.readAsString();
        final Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonStr);
        final Object obj = JsonPath.read(document, "$.data");
        final Type type = request.getMethod().getReturnType();
        Object ret = JSON.parseObject(JSON.toJSONString(obj), type);
        return success(ret);
    }

}
