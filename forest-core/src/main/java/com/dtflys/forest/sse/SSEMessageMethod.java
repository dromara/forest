package com.dtflys.forest.sse;

import com.dtflys.forest.annotation.SSEName;
import com.dtflys.forest.annotation.SSEValue;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.utils.ForestDataType;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * SSE 消息方法
 * <p>用于包装注册好的 SSE 消息处理方法</p>
 * 
 * @since 1.6.1
 */
public class SSEMessageMethod {

    /**
     * 方法所属实例
     */
    private final Object instance;

    /**
     * Java 方法
     */
    private final Method method;

    /**
     * 方法参数值获取函数表
     */
    private Function<EventSource, ?>[] argumentFunctions;
    

    public SSEMessageMethod(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
        init();
    }
    
    private void init() {
        final Parameter[] parameters = method.getParameters();
        argumentFunctions = new Function[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Class<?> paramType = parameter.getType();
            if (parameter.isVarArgs()) {
                final String varArgsTypeName = paramType.getSimpleName();
                throw new ForestRuntimeException(method.getDeclaringClass().getTypeName() + "." + method.getName() + ": SSE method VarArgs dose not be supported: " +
                        varArgsTypeName.substring(0, varArgsTypeName.length() - 2) + "...");
            }
            if (EventSource.class.isAssignableFrom(paramType)) {
                argumentFunctions[i] = eventSource -> eventSource;
            } else if (ForestRequest.class.isAssignableFrom(paramType)) {
                argumentFunctions[i] = eventSource -> eventSource.getRequest();
            } else if (ForestResponse.class.isAssignableFrom(paramType)) {
                argumentFunctions[i] = eventSource -> eventSource.getResponse();
            } else {
                final Annotation[] paramAnnArray = parameter.getAnnotations();
                if (paramAnnArray.length > 0) {
                    for (final Annotation ann : paramAnnArray) {
                        if (ann instanceof SSEName) {
                            argumentFunctions[i] = eventSource -> eventSource.getName();
                        } else if (ann instanceof SSEValue) {
                            setParameterValueFunction(method, i, paramType);
                        }
                    }
                } else {
                    setParameterValueFunction(method, i, paramType);
                }
            }
        }
    }

    public Method getMethod() {
        return method;
    }
    
    public void invoke(final EventSource eventSource) {
        final int len = argumentFunctions.length;
        final Object[] args = new Object[len];
        for (int i = 0; i < len; i++) {
            args[i] = argumentFunctions[i].apply(eventSource);
        }
        final boolean accessible = method.isAccessible();
        method.setAccessible(true);
        try {
            method.invoke(instance, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        } finally {
            method.setAccessible(accessible);
        }
    }

    private void setParameterValueFunction(Method method, int i, Class<?> paramType) {
        if (CharSequence.class.isAssignableFrom(paramType)) {
            argumentFunctions[i] = eventSource -> eventSource.getValue();
        } else {
            final Type type = method.getParameters()[i].getParameterizedType();
            argumentFunctions[i] = eventSource -> eventSource
                    .getRequest().getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(eventSource.getValue(), type);
        }
    }

}
