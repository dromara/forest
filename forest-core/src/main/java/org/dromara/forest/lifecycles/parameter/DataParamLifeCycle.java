package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.DataParam;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.mapping.MappingVariable;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.lifecycles.ParameterAnnotationLifeCycle;

/**
 * Forest &#064;DataParam注解的生命周期
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
