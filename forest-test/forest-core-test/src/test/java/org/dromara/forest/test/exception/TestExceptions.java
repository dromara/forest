package org.dromara.forest.test.exception;

import org.dromara.forest.Forest;
import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.DataFile;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.exceptions.ForestFileNotFoundException;
import org.dromara.forest.exceptions.ForestHandlerException;
import org.dromara.forest.exceptions.ForestInterceptorDefineException;
import org.dromara.forest.exceptions.ForestNetworkException;
import org.dromara.forest.exceptions.ForestNoFileNameException;
import org.dromara.forest.exceptions.ForestRetryException;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.exceptions.ForestUnsupportException;
import org.dromara.forest.exceptions.ForestVariableUndefinedException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
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
                .isEqualTo("[Forest] Interceptor class 'org.dromara.forest.test.exception.TestExceptions$TestErrorInterceptor' cannot be initialized, because interceptor class must implements org.dromara.forest.interceptor.Interceptor");
        assertThat(exception.getInterceptorClass()).isEqualTo(TestErrorInterceptor.class);
    }

/**
 * TODO: 移动到 forest-fastjson 去
 *
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
*/

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
                "\tmethod: org.dromara.forest.test.exception.TestExceptions$TestClient.urlVar()\n" +
                "\tannotation: org.dromara.forest.annotation.@Get\n" +
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
                "\tmethod: org.dromara.forest.test.exception.TestExceptions$TestClient.contentTypeVar()\n" +
                "\tannotation: org.dromara.forest.annotation.@Get\n" +
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
        assertThat(exception.getMessage()).isEqualTo("[Forest] Cannot resolve variable 'filename'\n" +
                "\n" +
                "\t[From Template]\n" +
                "\tmethod: org.dromara.forest.test.exception.TestExceptions$TestClient.fileVar(java.lang.String)\n" +
                "\tannotation: org.dromara.forest.annotation.@DataFile\n" +
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
