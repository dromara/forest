package com.dtflys.forest.interceptor;

import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestProgress;

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
        for (final Interceptor item : interceptors) {
            item.onInvokeMethod(request, method, args);
        }
    }


    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        ForestJointPoint jointpoint = ForestJointPoint.PROCEED;
        for (final Interceptor item : interceptors) {
            final ForestJointPoint jp = item.beforeExecute(request);
            jointpoint = jp != null ? jp : ForestJointPoint.PROCEED;
            if (!jointpoint.isProceed()) {
                return jointpoint;
            }
        }
        return jointpoint;
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
    public void onSuccess(ForestRequest request, ForestResponse response) {
        for (final Interceptor item : interceptors) {
            item.onSuccess(request, response);
        }
    }

    @Override
    public void onRetry(ForestRequest request, ForestResponse response) {
        for (final Interceptor item : interceptors) {
            item.onRetry(request, response);
        }
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        for (final Interceptor item : interceptors) {
            item.onError(ex, request, response);
        }
    }

    @Override
    public void onCanceled(ForestRequest request, ForestResponse response) {
        for (final Interceptor item : interceptors) {
            item.onCanceled(request, response);
        }
    }

    @Override
    public void onRedirection(ForestRequest redirectReq, ForestRequest prevReq, ForestResponse prevRes) {
        for (final Interceptor item : interceptors) {
            item.onRedirection(redirectReq, prevReq, prevRes);
        }
    }

    @Override
    public void onProgress(ForestProgress progress) {
        for (final Interceptor item : interceptors) {
            item.onProgress(progress);
        }
    }

    @Override
    public void onLoadCookie(ForestRequest request, ForestCookies cookies) {
        for (final Interceptor item : interceptors) {
            item.onLoadCookie(request, cookies);
        }
    }

    @Override
    public void onSaveCookie(ForestRequest request, ForestCookies cookies) {
        for (final Interceptor item : interceptors) {
            item.onSaveCookie(request, cookies);
        }
    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        for (final Interceptor item : interceptors) {
            item.afterExecute(request, response);
        }
    }
}
