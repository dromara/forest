package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest &#064;Header注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 1:31
 */
public class HeaderLifeCycle implements ParameterAnnotationLifeCycle<Header, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, Header annotation) {
        String name = annotation.value();
        if (StringUtils.isNotEmpty(name)) {
            parameter.setName(name);
            MappingVariable variable = new MappingVariable(name, parameter.getType());
            variable.setIndex(parameter.getIndex());
            method.addVariable(name, variable);
            parameter.setObjectProperties(false);
        } else {
            parameter.setObjectProperties(true);
        }
        parameter.setTarget(MappingParameter.TARGET_HEADER);
        method.addNamedParameter(parameter);
    }

}
