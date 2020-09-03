package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.utils.StringUtils;

import static com.dtflys.forest.mapping.MappingParameter.TARGET_QUERY;

/**
 * @Query注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 1:14
 */
public class QueryLifeCycle implements ParameterAnnotationLifeCycle<Query, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, Query annotation) {
        String name = annotation.value();
        String filterName = annotation.filter();
        if (StringUtils.isNotEmpty(name)) {
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
        method.processParameterFilter(parameter, filterName);
        parameter.setTarget(TARGET_QUERY);
        method.addNamedParameter(parameter);
    }
}
