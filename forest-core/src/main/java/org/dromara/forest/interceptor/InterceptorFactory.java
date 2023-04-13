package org.dromara.forest.interceptor;

/**
 * 拦截器工厂
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 */
public interface InterceptorFactory {

    InterceptorChain getInterceptorChain();

    <T extends Interceptor> T getInterceptor(Class<T> clazz);
}
