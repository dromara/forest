package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.FormBody;
import org.dromara.forest.backend.ContentType;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;
import org.dromara.forest.utils.StringUtils;

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
    public boolean beforeExecute(ForestRequest request) {
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
        return true;
    }
}
