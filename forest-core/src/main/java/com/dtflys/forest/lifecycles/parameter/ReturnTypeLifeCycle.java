package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.ReturnType;
import com.dtflys.forest.exceptions.ForestReturnTypeException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.TypeReference;

import java.lang.reflect.Type;

public class ReturnTypeLifeCycle implements ParameterAnnotationLifeCycle<ReturnType, Object> {

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, ReturnType annotation) {
        Class<?> paramType = parameter.getType();
        if (!Class.class.isAssignableFrom(paramType) &&
                !Type.class.isAssignableFrom(paramType) &&
                !TypeReference.class.isAssignableFrom(paramType)) {
            throw new ForestReturnTypeException(paramType);
        }
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        return true;
    }
}
