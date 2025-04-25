package com.dtflys.forest.spring;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestURL;
import com.dtflys.forest.reflection.ForestArgumentsVariable;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestMethodVariable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SpringMethodVariableValue implements ForestArgumentsVariable {

    private final static Object[] DEFAULT_ARGUMENTS = new Object[0];

    private final Object bean;

    private final Method method;

    public SpringMethodVariableValue(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }


    @Override
    public Object getValue(ForestRequest req, Object[] args) {
        Class<?>[] paramTypes = method.getParameterTypes();
        try {
            if (paramTypes.length == 0) {
                return method.invoke(bean, DEFAULT_ARGUMENTS);
            }
            Object[] invokeArgs = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                final Class<?> paramType = paramTypes[i];
                if (ForestRequest.class.isAssignableFrom(paramType)) {
                    invokeArgs[i] = paramType.cast(req);
                } else if (ForestMethod.class.isAssignableFrom(paramType)) {
                    invokeArgs[i] = req.getMethod();
                } else if (ForestURL.class.isAssignableFrom(paramType)) {
                    invokeArgs[i] = req.url();
                } else if (ForestBody.class.isAssignableFrom(paramType)) {
                    invokeArgs[i] = req.body();
                } else {
                    throw new ForestRuntimeException("[Forest] Method '" + method.getName() + "' can not be binding to a Forest variable, because parameter type '" + paramType + "' is not supported.");
                }
            }
            return method.invoke(bean, invokeArgs);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ForestRuntimeException(e);
        }

    }
}
