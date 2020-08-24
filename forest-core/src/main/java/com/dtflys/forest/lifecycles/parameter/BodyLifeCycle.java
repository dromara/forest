package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.utils.StringUtils;

/**
 * @Body注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 1:25
 */
public class BodyLifeCycle implements ParameterAnnotationLifeCycle<Body, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, Body annotation) {
        String name = annotation.value();
        String filterName = annotation.filter();
        if (StringUtils.isNotEmpty(name)) {
            parameter.setName(name);
            MappingVariable variable = new MappingVariable(name, parameter.getType());
            variable.setIndex(parameter.getIndex());
            method.addVariable(name, variable);
            parameter.setObjectProperties(false);
        } else {
            parameter.setObjectProperties(true);
        }
        method.processParameterFilter(parameter, filterName);
        parameter.setTarget(MappingParameter.TARGET_BODY);
        method.addNamedParameter(parameter);
    }
}
