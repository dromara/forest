package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;

/**
 * @DataParam注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 0:50
 */
public class DataParamLifeCycle implements ParameterAnnotationLifeCycle<DataParam, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, DataParam annotation) {
        String name = annotation.value();
        String filterName = annotation.filter();
        parameter.setName(name);
        method.processParameterFilter(parameter, filterName);
        method.addNamedParameter(parameter);
        MappingVariable variable = new MappingVariable(name, parameter.getType());
        method.processParameterFilter(variable, filterName);
        variable.setIndex(parameter.getIndex());
        method.addVariable(annotation.value(), variable);
    }

}
