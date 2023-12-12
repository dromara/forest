package com.dtflys.test.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestJoinpoint;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorChain;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 18:35
 */
public class TestInterceptorChain {

    @Test
    public void testInterceptorChain() {

        final AtomicInteger count = new AtomicInteger(0);
        final AtomicBoolean inter1Before = new AtomicBoolean(false);
        final AtomicBoolean inter2Before = new AtomicBoolean(false);
        final AtomicBoolean inter3Before = new AtomicBoolean(false);
        final AtomicBoolean inter1Success = new AtomicBoolean(false);
        final AtomicBoolean inter2Success = new AtomicBoolean(false);
        final AtomicBoolean inter1Error = new AtomicBoolean(false);
        final AtomicBoolean inter2Error = new AtomicBoolean(false);
        final AtomicBoolean inter1After = new AtomicBoolean(false);
        final AtomicBoolean inter2After = new AtomicBoolean(false);
        final AtomicBoolean only2After = new AtomicBoolean(false);


        Interceptor interceptor1 = new Interceptor() {
            @Override
            public ForestJoinpoint beforeExecute(ForestRequest request) {
                inter1Before.set(true);
                return proceed();
            }

            @Override
            public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
                inter1Success.set(true);
                count.incrementAndGet();
            }

            @Override
            public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
                inter1Error.set(true);
            }

            @Override
            public void afterExecute(ForestRequest request, ForestResponse response) {
                inter1After.set(true);
            }
        };


        Interceptor interceptor2 = new Interceptor() {
            @Override
            public ForestJoinpoint beforeExecute(ForestRequest request) {
                inter2Before.set(true);
                return proceed();
            }

            @Override
            public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
                inter2Success.set(true);
                count.incrementAndGet();
            }

            @Override
            public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
                inter2Error.set(true);
            }

            @Override
            public void afterExecute(ForestRequest request, ForestResponse response) {
                inter2After.set(true);
                only2After.set(true);
            }
        };


        Interceptor interceptor3 = new Interceptor() {
            @Override
            public ForestJoinpoint beforeExecute(ForestRequest request) {
                inter3Before.set(true);
                return proceed();
            }

            @Override
            public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
            }

            @Override
            public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
            }

            @Override
            public void afterExecute(ForestRequest request, ForestResponse response) {
            }
        };


        InterceptorChain chain = new InterceptorChain();
        chain
                .addInterceptor(interceptor1)
                .addInterceptor(interceptor2);
        assertEquals(2, chain.getInterceptorSize());

        assertTrue(chain.beforeExecute(null).isProceed());
        assertTrue(inter1Before.get());
        assertTrue(inter2Before.get());

        chain.addInterceptor(interceptor3);
        assertFalse(inter3Before.get());

        chain.onSuccess(null, null, null);
        assertTrue(inter1Success.get());
        assertTrue(inter2Success.get());
        assertEquals(2, count.get());

        chain.onError(null, null, null);
        assertTrue(inter1Error.get());
        assertTrue(inter2Error.get());

        chain.afterExecute(null, null);
        assertTrue(inter1After.get());
        assertTrue(inter2After.get());
        assertTrue(only2After.get());
    }

}
