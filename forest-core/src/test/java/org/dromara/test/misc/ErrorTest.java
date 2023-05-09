package org.dromara.test.misc;

import org.dromara.forest.callback.OnError;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.test.ErrorClient;
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
