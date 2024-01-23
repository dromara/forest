package com.dtflys.forest.solon;

import com.dtflys.forest.config.VariableValueContext;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.ForestVariableDef;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.11
 */
public class SolonForestVariableDef implements ForestVariableDef {

    private final static Object[] DEFAULT_ARGUMENTS = new Object[0];

    private final Object bean;

    private final Method method;

    public SolonForestVariableDef(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    @Override
    public Object getValue(VariableValueContext valueContext) {
        Class<?>[] paramTypes = method.getParameterTypes();
        try {
            if (paramTypes.length == 0) {
                return method.invoke(bean, DEFAULT_ARGUMENTS);
            }
            if (paramTypes.length == 1 && ForestMethod.class.isAssignableFrom(paramTypes[0])) {
                return method.invoke(bean, valueContext.getForestMethod());
            }
            throw new ForestRuntimeException("[Forest] Method '" + method.getName() + "' can not be binding to a Forest variable");
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ForestRuntimeException(e);
        }
    }
}
