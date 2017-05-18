package org.forest;

import org.forest.exceptions.ForestRuntimeException;
import org.forest.interceptor.Interceptor;
import org.forest.interceptor.InterceptorChain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-15 11:18
 */
public class Forest {

    private final static Map<Class, Interceptor> interceptorMap = new ConcurrentHashMap<>();

    private static InterceptorChain interceptorChain = new InterceptorChain();

    public static InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    public static <T extends Interceptor> Interceptor getInterceptor(Class<T> clazz) {
        Interceptor interceptor = interceptorMap.get(clazz);
        if (interceptor == null) {
            try {
                interceptor = clazz.newInstance();
                interceptorMap.put(clazz, interceptor);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }
        return interceptor;
    }
}
