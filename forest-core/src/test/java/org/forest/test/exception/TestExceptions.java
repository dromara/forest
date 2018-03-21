package org.forest.test.exception;

import org.forest.exceptions.ForestHandlerException;
import org.forest.exceptions.ForestNetworkException;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 12:33
 */
public class TestExceptions {


    @Test
    public void testForestNetworkException() {
        try {
            throw new ForestNetworkException("misc network exception", 500, null);
        } catch (ForestNetworkException e) {
            assertEquals("HTTP 500 Error: misc network exception", e.getMessage());
            assertEquals(Integer.valueOf(500), e.getStatusCode());
        }
    }

    @Test
    public void testForestRuntimeException() {
        try {
            throw new Exception("first Exception");
        } catch (Exception e) {
            try {
                throw new ForestRuntimeException(e);
            } catch (ForestRuntimeException fe) {
                assertEquals(e, fe.getCause());
            }
        }

        try {
            throw new Exception("first Exception");
        } catch (Exception e) {
            try {
                throw new ForestRuntimeException("second Exception", e);
            } catch (ForestRuntimeException fe) {
                assertEquals("second Exception", fe.getMessage());
                assertEquals(e, fe.getCause());
            }
        }

        try {
            throw new ForestRuntimeException("runtime exception");
        } catch (ForestRuntimeException fe) {
            assertEquals("runtime exception", fe.getMessage());
        }
    }

    @Test
    public void testForestHandlerException() {
        ForestRequest request = mock(ForestRequest.class);
        ForestResponse response = mock(ForestResponse.class);
        try {
            throw new ForestHandlerException("misc", request, response);
        } catch (ForestHandlerException e) {
            assertEquals("misc", e.getMessage());
            assertEquals(request, e.getRequest());
            assertEquals(response, e.getResponse());
            e.setRequest(null);
            assertNull(e.getRequest());
            e.setResponse(null);
            assertNull(e.getResponse());
        }

        try {
            throw new Exception("first Exception");
        } catch (Exception e) {
            try {
                throw new ForestHandlerException("second Exception", e, request, response);
            } catch (ForestHandlerException fe) {
                assertEquals("second Exception", fe.getMessage());
                assertEquals(e, fe.getCause());
                assertEquals(request, fe.getRequest());
                assertEquals(response, fe.getResponse());
            }
        }

        try {
            throw new Exception("first Exception");
        } catch (Exception e) {
            try {
                throw new ForestHandlerException( e, request, response);
            } catch (ForestHandlerException fe) {
                assertEquals(e, fe.getCause());
                assertEquals(request, fe.getRequest());
                assertEquals(response, fe.getResponse());
            }
        }


    }

}
