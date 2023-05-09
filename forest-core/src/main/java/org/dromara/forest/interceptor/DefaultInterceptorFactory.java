/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dromara.forest.interceptor;

import org.dromara.forest.exceptions.ForestRuntimeException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认拦截器工厂
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-15 11:18
 */
public class DefaultInterceptorFactory implements InterceptorFactory {

    /**
     * 拦截器实例缓存
     */
    protected final Map<Class, Interceptor> INTERCEPTOR_CACHE = new ConcurrentHashMap<>();

    /**
     * 拦截器调用链
     */
    protected InterceptorChain interceptorChain = new InterceptorChain();

    @Override
    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    @Override
    public <T extends Interceptor> T getInterceptor(Class<T> clazz) {
        return (T) INTERCEPTOR_CACHE.computeIfAbsent(clazz, key ->
                createInterceptor(key));
    }

    protected <T extends Interceptor> Interceptor createInterceptor(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new ForestRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        }
    }

}
