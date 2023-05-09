package org.dromara.forest.core.test.http;

import cn.hutool.core.io.FileUtil;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.dromara.forest.backend.ContentType;
import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.core.test.http.client.BinaryClient;
import org.dromara.forest.mock.MockServerRequest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

import static junit.framework.Assert.assertEquals;

public class TestUploadBinary extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static BinaryClient binaryClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();

    }

    public byte[] getUploadByteArray() {
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        return FileUtil.readBytes(file);
    }

    @Override
    public void afterRequests() {
    }

    public TestUploadBinary(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        binaryClient = configuration.createInstance(BinaryClient.class);
    }

    private static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    @Test
    public void testUploadByteArray() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        byte[] byteArray = FileUtil.readBytes(file);
        String result = binaryClient.uploadOctetStreamWithByteArray(byteArray, "test-xxx.jpg");
        assertEquals(EXPECTED, result);

        MockServerRequest.mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/upload-octet-stream/test-xxx.jpg")
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_OCTET_STREAM)
                .assertBodyEquals(byteArray);
    }

    @Test
    public void testUploadFile() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        String result = binaryClient.uploadOctetStreamWithFile(file, "test-xxx.jpg");
        assertEquals(EXPECTED, result);

        MockServerRequest.mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/upload-octet-stream/test-xxx.jpg")
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_OCTET_STREAM)
                .assertBodyEquals(getUploadByteArray());
    }

    @Test
    public void testUploadDataFile() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = this.getClass().getResource("/test-img.jpg").getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        String result = binaryClient.uploadOctetStreamWithDataFile(file, "test-xxx.jpg");
        assertEquals(EXPECTED, result);

        MockServerRequest.mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/upload-octet-stream/test-xxx.jpg")
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_OCTET_STREAM)
                .assertBodyEquals(getUploadByteArray());
    }

    @Test
    public void testUploadBinaryBody() throws InterruptedException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = this.getClass().getResource("/test-img.jpg").getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        String result = binaryClient.uploadOctetStreamWithBinaryBody(file, "test-xxx.jpg");
        assertEquals(EXPECTED, result);

        MockServerRequest.mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/upload-octet-stream/test-xxx.jpg")
                .assertBodyEquals(getUploadByteArray());
    }


}
