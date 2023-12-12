package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.BinaryBody;
import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJoinpoint;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * Forest &#064;JSONBody注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class BinaryBodyLifeCycle extends AbstractBodyLifeCycle<BinaryBody> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, BinaryBody annotation) {
        super.onParameterInitialized(method, parameter, annotation);
        final MetaRequest metaRequest = method.getMetaRequest();

        final String methodName = methodName(method);

        if (metaRequest == null) {
            throw new ForestRuntimeException("[Forest] method '" + methodName +
                    "' has not bind a Forest request annotation. Hence the annotation @BinaryBody cannot be bind on a parameter in this method.");
        }
        boolean hasDataFileAnn = false;
        for (Parameter param : method.getMethod().getParameters()) {
            final Annotation dataFileAnn = param.getAnnotation(DataFile.class);
            if (dataFileAnn != null) {
                hasDataFileAnn = true;
                break;
            }
        }
        final String contentTypeStr = metaRequest.getContentType();
        if (StringUtils.isBlank(contentTypeStr) && !hasDataFileAnn) {
            metaRequest.setContentType(ContentType.APPLICATION_OCTET_STREAM);
        }
        if (metaRequest.getBodyType() == null) {
            metaRequest.setBodyType(ForestDataType.BINARY);
        }
        parameter.setTarget(MappingParameter.TARGET_BODY);
    }

    private static String methodName(ForestMethod method) {
        return method.getMethod().toGenericString();
    }

    @Override
    public ForestJoinpoint beforeExecute(ForestRequest request) {
        String contentType = request.getContentType();
        if (StringUtils.isBlank(contentType)) {
            request.setContentType(ContentType.APPLICATION_OCTET_STREAM);
        }
        return proceed();
    }

}
