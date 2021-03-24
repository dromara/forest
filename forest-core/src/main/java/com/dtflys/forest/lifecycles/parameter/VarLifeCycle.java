package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest &#064;DataVariable注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 1:37
 */
public class VarLifeCycle implements ParameterAnnotationLifeCycle<Var, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, Var annotation) {
        String name = annotation.value();
        if (StringUtils.isEmpty(name)) {
            name = parameter.getName();
        }
        String filterName = annotation.filter();
        MappingVariable variable = new MappingVariable(name, parameter.getType());
        method.processParameterFilter(variable, filterName);
        variable.setIndex(parameter.getIndex());
        method.addVariable(name, variable);
    }
}
