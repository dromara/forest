package com.dtflys.forest.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestProgress;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 18:30
 */
public class InterceptorChain implements Interceptor {

    private LinkedList<Interceptor> interceptors = new LinkedList<>();

    public synchronized InterceptorChain addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
        return this;
    }

    public int getInterceptorSize() {
        return interceptors.size();
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Iterator<Interceptor> iter = interceptors.iterator();
        for ( ; iter.hasNext(); ) {
            Interceptor item = iter.next();
            item.onInvokeMethod(request, method, args);
        }
    }


    @Override
    public boolean beforeExecute(ForestRequest request) {
        Iterator<Interceptor> iter = interceptors.iterator();
        for ( ; iter.hasNext(); ) {
            Interceptor item = iter.next();
            boolean result = item.beforeExecute(request);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        Iterator<Interceptor> iter = interceptors.iterator();
        for (; iter.hasNext(); ) {
            Interceptor item = iter.next();
            if (response != null) {
                data = response.getResult();
            }
            item.onSuccess(data, request, response);
        }
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        Iterator<Interceptor> iter = interceptors.iterator();
        for ( ; iter.hasNext(); ) {
            Interceptor item = iter.next();
            item.onError(ex, request, response);
        }
    }

    @Override
    public void onProgress(ForestProgress progress) {
        Iterator<Interceptor> iter = interceptors.iterator();
        for (; iter.hasNext(); ) {
            Interceptor item = iter.next();
            item.onProgress(progress);
        }
    }

    @Override
    public void onLoadCookie(ForestRequest request, ForestCookies cookies) {
        Iterator<Interceptor> iter = interceptors.iterator();
        for (; iter.hasNext(); ) {
            Interceptor item = iter.next();
            item.onLoadCookie(request, cookies);
        }
    }

    @Override
    public void onSaveCookie(ForestRequest request, ForestCookies cookies) {
        Iterator<Interceptor> iter = interceptors.iterator();
        for (; iter.hasNext(); ) {
            Interceptor item = iter.next();
            item.onSaveCookie(request, cookies);
        }
    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        Iterator<Interceptor> iter = interceptors.iterator();
        for ( ; iter.hasNext(); ) {
            Interceptor item = iter.next();
            item.afterExecute(request, response);
        }
    }
}
