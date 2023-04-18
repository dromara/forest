package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.JSONQuery;
import org.dromara.forest.lifecycles.ParameterAnnotationLifeCycle;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.mapping.MappingVariable;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.utils.StringUtil;

/**
 * Forest &#064;Query注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 1:14
 */
public class JSONQueryLifeCycle implements ParameterAnnotationLifeCycle<JSONQuery, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, JSONQuery annotation) {
        String name = annotation.value();
        parameter.setTarget(MappingParameter.TARGET_QUERY);
        parameter.setJsonParam(true);
        if (StringUtil.isNotEmpty(name)) {
            parameter.setName(name);
            MappingVariable variable = new MappingVariable(name, parameter.getType());
            variable.setIndex(parameter.getIndex());
            method.addVariable(annotation.value(), variable);
            parameter.setObjectProperties(false);
        } else if (CharSequence.class.isAssignableFrom(parameter.getType())) {
            parameter.setName(null);
            parameter.setObjectProperties(false);
        } else {
            parameter.setObjectProperties(true);
        }
        method.addNamedParameter(parameter);
    }
}
