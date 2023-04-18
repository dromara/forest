package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.DataFile;
import org.dromara.forest.annotation.ProtobufBody;
import org.dromara.forest.backend.ContentType;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * Forest &#064;JSONBody注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class ProtobufBodyLifeCycle extends AbstractBodyLifeCycle<ProtobufBody> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, ProtobufBody annotation) {
        super.onParameterInitialized(method, parameter, annotation);
        MetaRequest metaRequest = method.getMetaRequest();

        String methodName = methodName(method);

        if (metaRequest == null) {
            throw new ForestRuntimeException("[Forest] method '" + methodName +
                    "' has not bind a Forest request annotation. Hence the annotation @BinaryBody cannot be bind on a parameter in this method.");
        }
        boolean hasDataFileAnn = false;
        for (Parameter param : method.getMethod().getParameters()) {
            Annotation dataFileAnn = param.getAnnotation(DataFile.class);
            if (dataFileAnn != null) {
                hasDataFileAnn = true;
                break;
            }
        }
        String contentTypeStr = metaRequest.getContentType();
        if (StringUtil.isBlank(contentTypeStr) && !hasDataFileAnn) {
            metaRequest.setContentType(ContentType.APPLICATION_X_PROTOBUF);
        }
        if (metaRequest.getBodyType() == null) {
            metaRequest.setBodyType(ForestDataType.PROTOBUF);
        }
        parameter.setTarget(MappingParameter.TARGET_BODY);
    }

    private static String methodName(ForestMethod method) {
        return method.getMethod().toGenericString();
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String contentType = request.getContentType();
        if (StringUtil.isBlank(contentType)) {
            request.setContentType(ContentType.APPLICATION_X_PROTOBUF);
        }
        return true;
    }

}
