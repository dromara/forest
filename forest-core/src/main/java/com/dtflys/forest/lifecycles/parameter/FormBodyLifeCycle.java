package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.FormBody;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest &#064;FormBody注解的生命周期
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-RC1
 */
public class FormBodyLifeCycle extends AbstractBodyLifeCycle<FormBody> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, FormBody annotation) {
        super.onParameterInitialized(method, parameter, annotation);
        final MetaRequest metaRequest = method.getMetaRequest();

        final String methodName = methodName(method);

        if (metaRequest == null) {
            throw new ForestRuntimeException("[Forest] method '" + methodName +
                    "' has not bind a Forest request annotation. Hence the annotation @FormBody cannot be bind on a parameter in this method.");
        }
        final String contentType = metaRequest.getContentType();
        if (StringUtils.isNotEmpty(contentType) &&
                !ContentType.APPLICATION_X_WWW_FORM_URLENCODED.equals(contentType) &&
                !ContentType.MULTIPART_FORM_DATA.equals(contentType) &&
                contentType.indexOf("$") < 0) {
            throw new ForestRuntimeException("[Forest] the Content-Type of request binding on method '" +
                    methodName + "' has already been set value '" + contentType +
                    "', not 'application/x-www-form-urlencoded'. Hence the annotation @FormBody cannot be bind on a parameter in this method.");
        }
        if (StringUtils.isBlank(contentType)) {
            metaRequest.setContentType(ContentType.APPLICATION_X_WWW_FORM_URLENCODED);
        }
    }

    private static String methodName(ForestMethod method) {
        return method.getMethod().toGenericString();
    }

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        String contentType = request.getContentType();
        if (StringUtils.isBlank(contentType)) {
            request.setContentType(ContentType.APPLICATION_X_WWW_FORM_URLENCODED);
        }

        if (contentType.indexOf(ContentType.APPLICATION_X_WWW_FORM_URLENCODED) < 0) {
            String methodName = methodName(request.getMethod());
            throw new ForestRuntimeException("[Forest] the Content-Type of request binding on method '" +
                    methodName + "' has already been set value '" + contentType +
                    "', not 'application/x-www-form-urlencoded'. Hence the annotation @FormBody cannot be bind on a parameter in this method.");
        }
        return proceed();
    }
}
