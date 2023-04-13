package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.Redirection;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.reflection.ForestMethod;


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
