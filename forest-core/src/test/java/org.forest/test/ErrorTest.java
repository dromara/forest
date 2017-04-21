package org.forest.test;

import junit.framework.TestCase;
import org.forest.client.ErrorClient;
import org.forest.config.ForestConfiguration;
import org.forest.http.ForestRequest;
import org.forest.callback.OnError;
import org.forest.exceptions.ForestRuntimeException;

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
        final boolean[] ts = new boolean[] {false};
        errorClient.testError(new OnError() {
            public void onError(ForestRuntimeException ex, ForestRequest requst) {
                ts[0] = true;
                assertNotNull(ex);
                assertNotNull(requst);
            }
        });
        assertTrue(ts[0]);
    }
}
