package com.dtflys.test.misc;

import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.ErrorClient;
import junit.framework.TestCase;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestResponse;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gongjun
 * @date 2016-06-01
 */
public class ErrorTest extends TestCase {
    ForestConfiguration configuration = ForestConfiguration.configuration();
    ErrorClient errorClient = configuration.createInstance(ErrorClient.class);

    public void testError() {
        boolean t = false;
        try {
            String result = errorClient.testError();
        } catch (ForestRuntimeException e) {
            t = true;
        }
        assertTrue(t);
    }

    public void testErrorCallback() {
        final AtomicInteger count = new AtomicInteger(0);
        final boolean[] ts = new boolean[] {false};
        errorClient.testError(new OnError() {
            public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
                int status = response.getStatusCode();
                count.incrementAndGet();
                assertNotNull(ex);
                assertNotNull(request);
            }
        });
        assertEquals(1, count.get());
    }
}
