package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.BodyType;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;

public class BodyTypeLifeCycle implements MethodAnnotationLifeCycle<BodyType, Object> {


    @Override
    public void onMethodInitialized(ForestMethod method, BodyType annotation) {
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String type = getAttributeAsString(request, "type");
        Object encoderObj = getAttribute(request, "encoder");
        Class<? extends ForestEncoder> encoder = null;
        if (encoderObj != null && encoderObj instanceof ForestEncoder) {
            encoder = (Class<? extends ForestEncoder>) encoderObj;
        }
        return false;
    }

}
