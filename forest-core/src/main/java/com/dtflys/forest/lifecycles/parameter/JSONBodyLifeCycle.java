package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest &#064;JSONBody注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class JSONBodyLifeCycle implements ParameterAnnotationLifeCycle<JSONBody, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, JSONBody annotation) {
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
        MetaRequest metaRequest = method.getMetaRequest();
        if (metaRequest == null) {
            throw new ForestRuntimeException("[Forest] method '" + method.getMethodName() +
                    "' has not bind a Forest request annotation. Hence annotation @JSONBody cannot be bind on a parameter in this method.");
        }
        String contentType = metaRequest.getContentType();
        if (StringUtils.isNotEmpty(contentType) && !ContentType.APPLICATION_JSON.equals(contentType)) {
            throw new ForestRuntimeException("[Forest] the Content-Type of request binding on method '" +
                    method.getMethodName() + "' has already been set value '" + contentType +
                    "', not 'application/json'. Hence annotation @JSONBody cannot be bind on a parameter in this method.");
        }
        metaRequest.setContentType(ContentType.APPLICATION_JSON);
        method.processParameterFilter(parameter, filterName);
        parameter.setTarget(MappingParameter.TARGET_BODY);
        method.addNamedParameter(parameter);
    }
}
