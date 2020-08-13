package com.dtflys.forest.lifecycles;

import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.reflection.MetaRequest;

import java.lang.annotation.Annotation;


public class OptionsRequestLifeCycle extends RequestLifeCycle {

    @Override
    public MetaRequest buildMetaRequest(Annotation annotation) {
        MetaRequest metaRequest = super.buildMetaRequest(annotation);
        metaRequest.setType(ForestRequestType.OPTIONS.getName());
        return metaRequest;
    }


}
