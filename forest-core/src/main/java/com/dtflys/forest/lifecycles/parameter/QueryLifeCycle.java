package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;

import java.util.Map;

import static com.dtflys.forest.mapping.MappingParameter.TARGET_QUERY;

/**
 * Forest &#064;Query注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 1:14
 */
public class QueryLifeCycle implements ParameterAnnotationLifeCycle<Query> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, Query annotation) {
        final Map<String, Object> attrs = ReflectUtils.getAttributesFromAnnotation(annotation);
        final String name = (String) attrs.get("name");
        final String filterName = (String) attrs.get("filter");
        final String defaultValue = (String) attrs.get("defaultValue");
        if (StringUtils.isNotEmpty(name)) {
            parameter.setName(name);
            final MappingVariable variable = new MappingVariable(name, parameter.getType());
            method.processParameterFilter(variable, filterName);
            variable.setIndex(parameter.getIndex());
            method.addVariable(annotation.value(), variable);
            parameter.setObjectProperties(false);
        } else if (CharSequence.class.isAssignableFrom(parameter.getType())) {
            parameter.setName(null);
            parameter.setObjectProperties(false);
        } else {
            parameter.setObjectProperties(true);
        }
        if (StringUtils.isNotEmpty(defaultValue)) {
            parameter.setDefaultValue(defaultValue);
        }
        method.processParameterFilter(parameter, filterName);
        parameter.setTarget(TARGET_QUERY);
        method.addNamedParameter(parameter);
    }
}
