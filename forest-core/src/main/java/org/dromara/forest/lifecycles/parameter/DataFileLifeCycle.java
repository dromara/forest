package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.DataFile;
import org.dromara.forest.backend.ContentType;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.mapping.MappingTemplate;
import org.dromara.forest.multipart.ForestMultipartFactory;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.lifecycles.ParameterAnnotationLifeCycle;
import org.dromara.forest.reflection.MetaRequest;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.StringUtils;

/**
 * Forest &#064;DataFile注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 2:17
 */
public class DataFileLifeCycle implements ParameterAnnotationLifeCycle<DataFile, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, DataFile annotation) {
        final String name = annotation.value();
        final String fileName = annotation.fileName();
        final MetaRequest metaRequest = method.getMetaRequest();
        final String partContentType = annotation.partContentType();
        final MappingTemplate nameTemplate = method.makeTemplate(DataFile.class, "name", name);
        final MappingTemplate fileNameTemplate = method.makeTemplate(DataFile.class, "fileName", fileName);
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
    public boolean beforeExecute(ForestRequest request) {
        return ParameterAnnotationLifeCycle.super.beforeExecute(request);
    }
}
