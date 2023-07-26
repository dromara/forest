package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.mapping.MappingVariable;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.lifecycles.ParameterAnnotationLifeCycle;
import org.dromara.forest.utils.ReflectUtils;
import org.dromara.forest.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Forest &#064;DataVariable注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 1:37
 */
public class VariableLifeCycle implements ParameterAnnotationLifeCycle<Annotation, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, Annotation annotation) {
        final Map<String, Object> attrs = ReflectUtils.getAttributesFromAnnotation(annotation);
        String name = (String) attrs.get("value");
        if (StringUtils.isEmpty(name)) {
            name = parameter.getName();
        }
        final String filterName = (String) attrs.get("filter");
        final MappingVariable variable = new MappingVariable(name, parameter.getType());
        method.processParameterFilter(variable, filterName);
        variable.setIndex(parameter.getIndex());
        method.addVariable(name, variable);
    }
}
