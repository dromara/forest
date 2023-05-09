package org.dromara.forest.test.convert.xml.jaxb.interceptor;

import org.dromara.forest.converter.xml.ForestXmlConverter;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;
import org.dromara.forest.test.convert.xml.jaxb.pojo.XmlTestParam;

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
