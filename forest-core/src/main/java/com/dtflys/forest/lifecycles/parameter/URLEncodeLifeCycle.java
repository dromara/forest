package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.URLEncode;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;

import java.util.Map;

/**
 * Forest &#064;URLEncode注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.1
 */
public class URLEncodeLifeCycle implements ParameterAnnotationLifeCycle<URLEncode, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, URLEncode annotation) {
        final Map<String, Object> attrs = ReflectUtils.getAttributesFromAnnotation(annotation);
        String charset = (String) attrs.get("charset");
        final Boolean enabled = (Boolean) attrs.get("enabled");
        if (StringUtils.isBlank(charset)) {
            charset = "UTF-8";
        }
        parameter.setUrlEncode(enabled);
        if (enabled) {
            parameter.setCharset(charset);
        }
        method.addNamedParameter(parameter);
    }
}
