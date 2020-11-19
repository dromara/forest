package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.XMLBody;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest &#064;XMLBody注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class XMLBodyLifeCycle implements ParameterAnnotationLifeCycle<XMLBody, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, XMLBody annotation) {
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

        String methodName = methodName(method);

        if (metaRequest == null) {
            throw new ForestRuntimeException("[Forest] method '" + methodName +
                    "' has not bind a Forest request annotation. Hence the annotation @JSONBody cannot be bind on a parameter in this method.");
        }
        String contentType = metaRequest.getContentType();
        if (StringUtils.isNotEmpty(contentType) &&
                !ContentType.APPLICATION_JSON.equals(contentType) &&
                contentType.indexOf("$") < 0) {
            throw new ForestRuntimeException("[Forest] the Content-Type of request binding on method '" +
                    methodName + "' has already been set value '" + contentType +
                    "', not 'application/json'. Hence the annotation @JSONBody cannot be bind on a parameter in this method.");
        }
        metaRequest.setContentType(ContentType.APPLICATION_XML);
        method.processParameterFilter(parameter, filterName);
        parameter.setTarget(MappingParameter.TARGET_BODY);
        method.addNamedParameter(parameter);
    }

    private static String methodName(ForestMethod method) {
        return method.getMethod().toGenericString();
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String contentType = request.getContentType();
        if (StringUtils.isBlank(contentType)) {
            request.setContentType(ContentType.APPLICATION_JSON);
        }

        if (contentType.indexOf(ContentType.APPLICATION_JSON) < 0) {
            String methodName = methodName(request.getMethod());
            throw new ForestRuntimeException("[Forest] the Content-Type of request binding on method '" +
                    methodName + "' has already been set value '" + contentType +
                    "', not 'application/json'. Hence the annotation @JSONBody cannot be bind on a parameter in this method.");
        }
        return true;
    }
}
