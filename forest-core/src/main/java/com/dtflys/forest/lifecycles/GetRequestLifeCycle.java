package com.dtflys.forest.lifecycles;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.reflection.MetaRequestLifeCycle;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.annotation.Annotation;
import java.util.Map;


public class GetRequestLifeCycle extends RequestLifeCycle {

    @Override
    public MetaRequest buildMetaRequest(Annotation annotation) {
        MetaRequest metaRequest = super.buildMetaRequest(annotation);
        metaRequest.setType("GET");
        return metaRequest;
    }


}
