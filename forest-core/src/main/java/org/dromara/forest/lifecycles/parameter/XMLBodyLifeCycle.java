package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.DataFile;
import org.dromara.forest.annotation.XMLBody;
import org.dromara.forest.backend.ContentType;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.filter.Filter;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * Forest &#064;XMLBody注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class XMLBodyLifeCycle extends AbstractBodyLifeCycle<XMLBody> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, XMLBody annotation) {
        super.onParameterInitialized(method, parameter, annotation);
        MetaRequest metaRequest = method.getMetaRequest();
        String methodName = methodName(method);
        if (metaRequest == null) {
            throw new ForestRuntimeException("[Forest] method '" + methodName +
                    "' has not bind a Forest request annotation. Hence the annotation @XMLBody cannot be bind on a parameter in this method.");
        }
        String contentType = metaRequest.getContentType();
        if (StringUtils.isNotEmpty(contentType) &&
                !ContentType.APPLICATION_XML.equals(contentType) &&
                contentType.indexOf("$") < 0) {
            throw new ForestRuntimeException("[Forest] the Content-Type of request binding on method '" +
                    methodName + "' has already been set value '" + contentType +
                    "', not 'application/xml'. Hence the annotation @XMLBody cannot be bind on a parameter in this method.");
        }
        boolean hasDataFileAnn = false;
        for (Parameter param : method.getMethod().getParameters()) {
            Annotation dataFileAnn = param.getAnnotation(DataFile.class);
            if (dataFileAnn != null) {
                hasDataFileAnn = true;
                break;
            }
        }
        Filter filter = method.getConfiguration().newFilterInstance("xml");
        parameter.addFilter(filter);
        if (StringUtils.isBlank(contentType) && !hasDataFileAnn) {
            metaRequest.setContentType(ContentType.APPLICATION_XML);
        }
        if (metaRequest.getBodyType() == null) {
            metaRequest.setBodyType(ForestDataType.XML);
        }
    }

    private static String methodName(ForestMethod method) {
        return method.getMethod().toGenericString();
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String contentType = request.getContentType();
        if (StringUtils.isBlank(contentType)) {
            request.setContentType(ContentType.APPLICATION_XML);
        }

        if (contentType.indexOf(ContentType.APPLICATION_XML) < 0) {
            String methodName = methodName(request.getMethod());
            throw new ForestRuntimeException("[Forest] the Content-Type of request binding on method '" +
                    methodName + "' has already been set value '" + contentType +
                    "', not 'application/xml'. Hence the annotation @XMLBody cannot be bind on a parameter in this method.");
        }
        return true;
    }
}
