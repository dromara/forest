package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest &#064;JSONBody注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class JSONBodyLifeCycle extends AbstractBodyLifeCycle<JSONBody> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, JSONBody annotation) {
        super.onParameterInitialized(method, parameter, annotation);
        MetaRequest metaRequest = method.getMetaRequest();

        String methodName = methodName(method);

        if (metaRequest == null) {
            throw new ForestRuntimeException("[Forest] method '" + methodName +
                    "' has not bind a Forest request annotation. Hence the annotation @JSONBody cannot be bind on a parameter in this method.");
        }
        String contentType = metaRequest.getContentType();
/*
        if (StringUtils.isNotEmpty(contentType) &&
                !(ContentType.APPLICATION_JSON.equals(contentType) ||
                        contentType.endsWith("json")) &&
                !ContentType.MULTIPART_FORM_DATA.equals(contentType) &&
                contentType.indexOf("$") < 0) {
            throw new ForestRuntimeException("[Forest] the Content-Type of request binding on method '" +
                    methodName + "' has already been set value '" + contentType +
                    "', not 'application/json'. Hence the annotation @JSONBody cannot be bind on a parameter in this method.");
        }
*/
        if (StringUtils.isBlank(contentType)) {
            metaRequest.setContentType(ContentType.APPLICATION_JSON);
        }
        parameter.setTarget(MappingParameter.TARGET_BODY);
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

/*
        if (contentType.indexOf(ContentType.APPLICATION_JSON) < 0 &&
                contentType.indexOf(ContentType.MULTIPART_FORM_DATA) < 0 &&
                !contentType.endsWith("json")) {
            String methodName = methodName(request.getMethod());
            throw new ForestRuntimeException("[Forest] the Content-Type of request binding on method '" +
                    methodName + "' has already been set value '" + contentType +
                    "', not 'application/json'. Hence the annotation @JSONBody cannot be bind on a parameter in this method.");
        }
*/
        return true;
    }

}
