package com.dtflys.test.interceptor;

import com.dtflys.forest.converter.xml.ForestXmlConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.test.http.model.XmlTestParam;

public class XmlResponseInterceptor implements Interceptor<Object> {
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        String source = response.getContent();
        ForestXmlConverter converter = request.getConfiguration().getXmlConverter();
        Object obj = converter.convertToJavaObject(source, request.getMethod().getReturnType());
        if (obj instanceof XmlTestParam) {
            ((XmlTestParam) obj).setA(10);
            ((XmlTestParam) obj).setB(20);
        }
        response.setResult(obj);
        request.methodReturn(obj);
    }
}
