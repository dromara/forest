package com.dtflys.test.exception;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.DataFile;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.auto.DefaultAutoConverter;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestFileNotFoundException;
import com.dtflys.forest.exceptions.ForestHandlerException;
import com.dtflys.forest.exceptions.ForestInterceptorDefineException;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestNoFileNameException;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.exceptions.ForestUnsupportException;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    private static class TestErrorInterceptor {
    }

    @Test
    public void testInterceptorDefineException() {
        ForestInterceptorDefineException exception = new ForestInterceptorDefineException(TestErrorInterceptor.class);
        assertThat(exception.getMessage())
                .isEqualTo("[Forest] Interceptor class 'com.dtflys.test.exception.TestExceptions$TestErrorInterceptor' cannot be initialized, because interceptor class must implements com.dtflys.forest.interceptor.Interceptor");
        assertThat(exception.getInterceptorClass()).isEqualTo(TestErrorInterceptor.class);
    }

    @Test
    public void testConvertException() {
        Throwable th = new Exception("xxx");
        ForestConverter<?> converter = new ForestFastjsonConverter();
        ForestConvertException exception = new ForestConvertException(converter, th);
        assertThat(exception.getMessage())
                .isEqualTo("[Forest] json converter: 'ForestFastjsonConverter' error: xxx");
        assertThat(exception.getConverterClass()).isEqualTo(ForestFastjsonConverter.class);

        converter = new ForestJacksonConverter();
        exception = new ForestConvertException(converter, th);
        assertThat(exception.getMessage())
                .isEqualTo("[Forest] json converter: 'ForestJacksonConverter' error: xxx");
        assertThat(exception.getConverterClass()).isEqualTo(ForestJacksonConverter.class);

        converter = new DefaultAutoConverter(ForestConfiguration.configuration());
        exception = new ForestConvertException(converter, th);
        assertThat(exception.getMessage())
                .isEqualTo("[Forest] auto converter: 'DefaultAutoConverter' error: xxx");
        assertThat(exception.getConverterClass()).isEqualTo(DefaultAutoConverter.class);
    }

    @Test
    public void testRetryException() {
        Throwable th = new Exception("xxx");
        ForestRequest<?> request = mock(ForestRequest.class);
        ForestRetryException exception = new ForestRetryException(th, request, 3, 1);
        assertThat(exception.getCause()).isEqualTo(th);
        assertThat(exception.getRequest()).isEqualTo(request);
        assertThat(exception.getMaxRetryCount()).isEqualTo(3);
        assertThat(exception.getCurrentRetryCount()).isEqualTo(1);
    }

    @Test
    public void testFileNotFoundException() {
        ForestFileNotFoundException exception = new ForestFileNotFoundException("/xxx/yyy");
        assertThat(exception.getMessage()).isEqualTo("File '/xxx/yyy' does not exist");
        assertThat(exception.getFilePath()).isEqualTo("/xxx/yyy");
    }

    @Test
    public void testNoFileNameException() {
        ForestNoFileNameException exception = new ForestNoFileNameException(byte[].class);
        assertThat(exception.getMessage())
                .isEqualTo("[Forest] '[B' parameters width @DataFile annotation must define a fileName");
        assertThat(exception.getParameterType()).isEqualTo(byte[].class);
    }

    @Test
    public void testUnsupportedException() {
        ForestUnsupportException exception = new ForestUnsupportException("Xxx");
        assertThat(exception.getMessage()).isEqualTo("[Forest] 'Xxx' is unsupported");
        assertThat(exception.getUnsupported()).isEqualTo("Xxx");
    }

    @Test
    public void testVariableUndefinedException() {
        ForestVariableUndefinedException exception = new ForestVariableUndefinedException("foo");
        assertThat(exception.getMessage()).isEqualTo("[Forest] Cannot resolve variable 'foo'");
        assertThat(exception.getVariableName()).isEqualTo("foo");
        assertThat(exception.getSource()).isNull();

        exception = new ForestVariableUndefinedException("bar", "foo=${bar}");
        assertThat(exception.getMessage())
                .isEqualTo("[Forest] Cannot resolve variable 'bar'" +
                        "\n\n\t[From Template]" +
                        "\n\ttemplate: foo=${bar}\n");
        assertThat(exception.getVariableName()).isEqualTo("bar");
        assertThat(exception.getSource()).isEqualTo("foo=${bar}");
    }

    @BaseRequest(baseURL = "http://localhost")
    public interface TestClient {
        @Get("/data/{data}")
        String urlVar();

        @Get(url = "/data/", contentType = "application/{json}")
        String contentTypeVar();

        @Get(url = "/data/")
        String fileVar(@DataFile(value = "file", fileName = "{filename}") String h);
    }

    @Test
    public void testVariableUndefinedException2() {
        TestClient testClient = Forest.client(TestClient.class);
        Throwable exception = null;
        try {
            testClient.urlVar();
        } catch (Throwable th){
            exception = th;
        }
        assertThat(exception).isNotNull().isInstanceOf(ForestVariableUndefinedException.class);
        assertThat(exception.getMessage()).isEqualTo("[Forest] Cannot resolve variable 'data'\n" +
                "\n" +
                "\t[From Template]\n" +
                "\tmethod: com.dtflys.test.exception.TestExceptions$TestClient.urlVar()\n" +
                "\tannotation: com.dtflys.forest.annotation.@Get\n" +
                "\tattribute: url = \"/data/{data}\"\n");
    }

    @Test
    public void testVariableUndefinedException3() {
        TestClient testClient = Forest.client(TestClient.class);
        Throwable exception = null;
        try {
            testClient.contentTypeVar();
        } catch (Throwable th){
            exception = th;
        }
        exception.printStackTrace();
        assertThat(exception).isNotNull().isInstanceOf(ForestVariableUndefinedException.class);
        assertThat(exception.getMessage()).isEqualTo("[Forest] Cannot resolve variable 'json'\n" +
                "\n" +
                "\t[From Template]\n" +
                "\tmethod: com.dtflys.test.exception.TestExceptions$TestClient.contentTypeVar()\n" +
                "\tannotation: com.dtflys.forest.annotation.@Get\n" +
                "\tattribute: contentType = \"application/{json}\"\n");
    }

    @Test
    public void testVariableUndefinedException_file() {
        TestClient testClient = Forest.client(TestClient.class);
        Throwable exception = null;
        try {
            testClient.fileVar("xxxx");
        } catch (Throwable th){
            exception = th;
        }
        assertThat(exception).isNotNull().isInstanceOf(ForestVariableUndefinedException.class);
        assertThat(exception.getMessage()).isNotNull().isEqualTo("[Forest] Cannot resolve variable 'filename'\n" +
                "\n" +
                "\t[From Template]\n" +
                "\tmethod: com.dtflys.test.exception.TestExceptions$TestClient.fileVar(java.lang.String)\n" +
                "\tannotation: com.dtflys.forest.annotation.@DataFile\n" +
                "\tattribute: fileName = \"{filename}\"\n");
    }


    @Test
    public void testForestHandlerException() {
        ForestRequest<?> request = mock(ForestRequest.class);
        ForestResponse<?> response = mock(ForestResponse.class);
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
