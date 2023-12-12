package com.dtflys.forest.lifecycles.parameter;

import com.dtflys.forest.annotation.Return;
import com.dtflys.forest.exceptions.ForestReturnException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJoinpoint;
import com.dtflys.forest.lifecycles.ParameterAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.utils.TypeReference;

import java.lang.reflect.Type;

public class ReturnLifeCycle implements ParameterAnnotationLifeCycle<Return, Object> {


    private final static String PARAM_KEY_RETURN_TYPE_NAME = "__return_type";

    @Override
    public void onParameterInitialized(ForestMethod method, MappingParameter parameter, Return annotation) {
        final Class<?> paramType = parameter.getType();
        if (!Class.class.isAssignableFrom(paramType) &&
                !Type.class.isAssignableFrom(paramType) &&
                !TypeReference.class.isAssignableFrom(paramType)) {
            throw new ForestReturnException(paramType);
        }
        method.setExtensionParameterValue(PARAM_KEY_RETURN_TYPE_NAME, parameter.getIndex());
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Object ext = method.getExtensionParameterValue(PARAM_KEY_RETURN_TYPE_NAME);
        if (ext != null) {
            final Integer index = (Integer) ext;
            final Object arg = args[index];
            if (arg == null) {
                return;
            }
            if (!(arg instanceof Type)) {
                return;
            }
            final Type type = (Type) arg;
            final LifeCycleHandler lifeCycleHandler = request.getLifeCycleHandler();
            if (lifeCycleHandler != null && lifeCycleHandler instanceof MethodLifeCycleHandler) {
                final MethodLifeCycleHandler methodLifeCycleHandler = (MethodLifeCycleHandler) lifeCycleHandler;
                methodLifeCycleHandler.setResultType(type);
            }
        }
    }

    @Override
    public ForestJoinpoint beforeExecute(ForestRequest request) {
        return proceed();
    }
}
