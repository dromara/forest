package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.FileBody;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.multipart.ForestMultipartFactory;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest &#064;DataFile注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 2:17
 */
public class DataFileLifeCycle implements ParameterAnnotationLifeCycle<FileBody> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, FileBody annotation) {
        final String name = annotation.value();
        final String fileName = annotation.fileName();
        final MetaRequest metaRequest = method.getMetaRequest();
        final String partContentType = annotation.partContentType();
        final MappingTemplate nameTemplate = MappingTemplate.annotation(method, FileBody.class, "name", name);
        final MappingTemplate fileNameTemplate = MappingTemplate.annotation(method, FileBody.class, "fileName", fileName);
        if (StringUtils.isNotBlank(partContentType)) {
            parameter.setPartContentType(partContentType.trim());
        }
        final ForestMultipartFactory factory = ForestMultipartFactory.createFactory(
                method,
                parameter.getType(),
                parameter.getIndex(),
                nameTemplate,
                fileNameTemplate,
                partContentType);
        method.addMultipartFactory(factory);
        final String contentType = metaRequest.getContentType();
        if (metaRequest.getBodyType() == null) {
            if (ContentType.APPLICATION_OCTET_STREAM.equals(contentType)) {
                metaRequest.setBodyType(ForestDataType.BINARY);
            } else {
                metaRequest.setBodyType(ForestDataType.MULTIPART);
            }
        }
    }

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        return ParameterAnnotationLifeCycle.super.beforeExecute(request);
    }
}
