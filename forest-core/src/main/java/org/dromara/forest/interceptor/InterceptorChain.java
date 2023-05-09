package org.dromara.forest.interceptor;

import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestCookies;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.utils.ForestProgress;

import java.util.LinkedList;

/**
 * 拦截器调用链
 *
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
        for (Interceptor item : interceptors) {
            item.onInvokeMethod(request, method, args);
        }
    }


    @Override
    public boolean beforeExecute(ForestRequest request) {
        for (Interceptor item : interceptors) {
            boolean result = item.beforeExecute(request);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    @Override
    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
        byte[] ret = encodedData;
        for (Interceptor item : interceptors) {
            ret = item.onBodyEncode(request, encoder, ret);
        }
        return ret;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        for (Interceptor item : interceptors) {
            if (response != null) {
                data = response.getResult();
            }
            item.onSuccess(data, request, response);
        }
    }

    @Override
    public void onRetry(ForestRequest request, ForestResponse response) {
        for (Interceptor item : interceptors) {
            item.onRetry(request, response);
        }
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        for (Interceptor item : interceptors) {
            item.onError(ex, request, response);
        }
    }

    @Override
    public void onCanceled(ForestRequest request, ForestResponse response) {
        for (Interceptor item : interceptors) {
            item.onCanceled(request, response);
        }
    }

    @Override
    public void onRedirection(ForestRequest redirectReq, ForestRequest prevReq, ForestResponse prevRes) {
        for (Interceptor item : interceptors) {
            item.onRedirection(redirectReq, prevReq, prevRes);
        }
    }

    @Override
    public void onProgress(ForestProgress progress) {
        for (Interceptor item : interceptors) {
            item.onProgress(progress);
        }
    }

    @Override
    public void onLoadCookie(ForestRequest request, ForestCookies cookies) {
        for (Interceptor item : interceptors) {
            item.onLoadCookie(request, cookies);
        }
    }

    @Override
    public void onSaveCookie(ForestRequest request, ForestCookies cookies) {
        for (Interceptor item : interceptors) {
            item.onSaveCookie(request, cookies);
        }
    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        for (Interceptor item : interceptors) {
            item.afterExecute(request, response);
        }
    }
}
