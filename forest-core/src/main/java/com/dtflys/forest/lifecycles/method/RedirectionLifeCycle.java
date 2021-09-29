package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Redirection;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;


/**
 * 标记是否开启解压GZIP响应内容的注解的生命周期类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.1
 */
public class RedirectionLifeCycle implements MethodAnnotationLifeCycle<Redirection, Object> {

    private final static String PARAM_KEY_AUTO_REDIRECTS = "__auto_redirects";

    @Override
    public void onMethodInitialized(ForestMethod method, Redirection annotation) {
        boolean value = annotation.value();
        method.setExtensionParameterValue(PARAM_KEY_AUTO_REDIRECTS, value);
    }


    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Boolean value = (Boolean) method.getExtensionParameterValue(PARAM_KEY_AUTO_REDIRECTS);
        request.setAutoRedirection(value);
    }

}
