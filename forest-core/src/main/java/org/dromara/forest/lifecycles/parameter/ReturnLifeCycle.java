package org.dromara.forest.lifecycles.parameter;

import org.dromara.forest.annotation.Return;
import org.dromara.forest.exceptions.ForestReturnException;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.ParameterAnnotationLifeCycle;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MethodLifeCycleHandler;
import org.dromara.forest.utils.TypeReference;

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
    public boolean beforeExecute(ForestRequest request) {

        return true;
    }
}
