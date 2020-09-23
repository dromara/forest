package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.multipart.ForestMultipartFactory;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.utils.StringUtils;

import static com.dtflys.forest.backend.body.AbstractBodyBuilder.TYPE_MULTIPART_FORM_DATA;

/**
 * Forest &#064;DataFile注解的生命周期
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 2:17
 */
public class DataFileLifeCycle implements ParameterAnnotationLifeCycle<DataFile, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, DataFile annotation) {
        String name = annotation.value();
        String fileName = annotation.fileName();
        MappingTemplate nameTemplate = method.makeTemplate(name);
        MappingTemplate fileNameTemplate = method.makeTemplate(fileName);
        ForestMultipartFactory factory = ForestMultipartFactory.createFactory(
                parameter.getType(), parameter.getIndex(), nameTemplate, fileNameTemplate);
        method.addMultipartFactory(factory);
    }

}
