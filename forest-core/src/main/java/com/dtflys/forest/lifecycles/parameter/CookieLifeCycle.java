package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.Cookie;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;

import java.util.Map;

public class CookieLifeCycle implements ParameterAnnotationLifeCycle<Cookie, Void> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, Cookie annotation) {
        final Map<String, Object> attrs = ReflectUtils.getAttributesFromAnnotation(annotation);
        final String name = (String) attrs.get("name");
        if (StringUtils.isNotEmpty(name)) {
            parameter.setName(name);
            final MappingVariable variable = new MappingVariable(name, parameter.getType());
            variable.setIndex(parameter.getIndex());
            method.addVariable(name, variable);
            parameter.setObjectProperties(false);
        } else {
            parameter.setObjectProperties(true);
        }
        method.addNamedParameter(parameter);
        parameter.setTarget(MappingParameter.TARGET_COOKIE);

    }
}
