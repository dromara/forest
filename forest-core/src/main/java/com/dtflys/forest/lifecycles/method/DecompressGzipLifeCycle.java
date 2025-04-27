package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.DecompressGzip;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;


/**
 * 标记是否开启解压GZIP响应内容的注解的生命周期类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.1
 */
public class DecompressGzipLifeCycle implements MethodAnnotationLifeCycle<DecompressGzip, Void> {

    private final static String PARAM_KEY_DECOMPRESS_GZIP = "__decompress_gzip";

    @Override
    public void onMethodInitialized(ForestMethod method, DecompressGzip annotation) {
        final boolean value = annotation.value();
        method.setExtensionParameterValue(PARAM_KEY_DECOMPRESS_GZIP, value);
    }


    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Boolean value = (Boolean) method.getExtensionParameterValue(PARAM_KEY_DECOMPRESS_GZIP);
        request.setDecompressResponseGzipEnabled(value);
    }

}
