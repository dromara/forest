package com.dtflys.forest.result;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class ForestRequestResultHandler extends ReturnOnInvokeMethodTypeHandler<ForestRequest> {

    public ForestRequestResultHandler() {
        super(ForestRequest.class);
    }


    @Override
    public  ForestRequest getReturnValue(Type returnType, Type onSuccessClassGenericType, ForestMethod forestMethod, Object[] args, ForestRequest request) {
        MethodLifeCycleHandler lifeCycleHandler = null;
        Type rType = null;
        final Type retType = forestMethod.getReturnType();
        if (retType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) retType;
            final Type[] genTypes = parameterizedType.getActualTypeArguments();
            if (genTypes.length > 0) {
                Type targetType = genTypes[0];
                rType = targetType;
            } else {
                rType = String.class;
            }
            if (rType instanceof WildcardType) {
                final WildcardType wildcardType = (WildcardType) rType;
                final Type[] bounds = wildcardType.getUpperBounds();
                if (bounds.length > 0) {
                    rType = bounds[0];
                } else {
                    rType = String.class;
                }
            }
            Type successType = rType;
            if (onSuccessClassGenericType != null) {
                successType = onSuccessClassGenericType;
            }
            lifeCycleHandler = new MethodLifeCycleHandler<>(
                    rType, successType);
            request.setLifeCycleHandler(lifeCycleHandler);
            lifeCycleHandler.handleInvokeMethod(request, forestMethod, args);
            return request;
        } else {
            lifeCycleHandler = new MethodLifeCycleHandler<>(
                    rType, onSuccessClassGenericType);
        }
        request.setLifeCycleHandler(lifeCycleHandler);
        lifeCycleHandler.handleInvokeMethod(request, forestMethod, args);
        return request;
    }
}
