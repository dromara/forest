package com.dtflys.forest.test.misc;

import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.test.ErrorClient;
import junit.framework.TestCase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gongjun
 * @date 2016-06-01
 */
public class ErrorTest extends TestCase {
    ForestConfiguration configuration = ForestConfiguration.configuration();
    ErrorClient errorClient = configuration.createInstance(ErrorClient.class);



    public void testError() {
        configuration.setTimeout(10);
        boolean t = false;
        try {
            String result = errorClient.testError();
        } catch (ForestRuntimeException e) {
            t = true;
        }
        assertTrue(t);
    }

    public void testErrorCallback() {
        configuration.setTimeout(10);
        final AtomicInteger count = new AtomicInteger(0);
        final boolean[] ts = new boolean[] {false};
        errorClient.testError(new OnError() {
            @Override
            public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
                int status = response.getStatusCode();
                count.incrementAndGet();
                assertNotNull(ex);
                assertNotNull(request);
            }
        });
        assertEquals(1, count.get());
    }


    public void testErrorResponse() {
        ForestResponse<String> response = errorClient.testErrorResponse();
        assertNotNull(response);
    }

}
