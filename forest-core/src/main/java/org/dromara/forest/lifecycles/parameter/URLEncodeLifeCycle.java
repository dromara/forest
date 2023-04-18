package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.URLEncode;
import org.dromara.forest.lifecycles.ParameterAnnotationLifeCycle;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.utils.ReflectUtil;
import org.dromara.forest.utils.StringUtil;

import java.util.Map;

/**
 * Forest &#064;URLEncode注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.1
 */
public class URLEncodeLifeCycle implements ParameterAnnotationLifeCycle<URLEncode, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, URLEncode annotation) {
        Map<String, Object> attrs = ReflectUtil.getAttributesFromAnnotation(annotation);
        String charset = (String) attrs.get("charset");
        Boolean enabled = (Boolean) attrs.get("enabled");
        if (StringUtil.isBlank(charset)) {
            charset = "UTF-8";
        }
        parameter.setUrlEncode(enabled);
        if (enabled) {
            parameter.setCharset(charset);
        }
        method.addNamedParameter(parameter);
    }
}
