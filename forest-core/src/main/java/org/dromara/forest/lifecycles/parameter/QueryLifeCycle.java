package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.Query;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.mapping.MappingVariable;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.lifecycles.ParameterAnnotationLifeCycle;
import org.dromara.forest.utils.ReflectUtil;
import org.dromara.forest.utils.StringUtil;

import java.util.Map;

/**
 * Forest &#064;Query注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 1:14
 */
public class QueryLifeCycle implements ParameterAnnotationLifeCycle<Query, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, Query annotation) {
        Map<String, Object> attrs = ReflectUtil.getAttributesFromAnnotation(annotation);
        String name = (String) attrs.get("name");
        String filterName = (String) attrs.get("filter");
        String defaultValue = (String) attrs.get("defaultValue");
        if (StringUtil.isNotEmpty(name)) {
            parameter.setName(name);
            MappingVariable variable = new MappingVariable(name, parameter.getType());
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
        if (StringUtil.isNotEmpty(defaultValue)) {
            parameter.setDefaultValue(defaultValue);
        }
        method.processParameterFilter(parameter, filterName);
        parameter.setTarget(MappingParameter.TARGET_QUERY);
        method.addNamedParameter(parameter);
    }
}
