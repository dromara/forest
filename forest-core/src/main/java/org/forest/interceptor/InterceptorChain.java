package org.forest.interceptor;

import org.forest.exceptions.ForestRuntimeException;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

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
            data = response.getResult();
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
    public void afterExecute(ForestRequest request, ForestResponse response) {
        Iterator<Interceptor> iter = interceptors.iterator();
        for ( ; iter.hasNext(); ) {
            Interceptor item = iter.next();
            item.afterExecute(request, response);
        }
    }
}
