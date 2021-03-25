package com.dtflys.test.mock;

import cn.hutool.core.io.FileUtil;
import com.dtflys.forest.backend.ContentType;
import org.apache.http.HttpHeaders;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import java.io.File;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class BinaryMockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static Integer port = 5089;

    public BinaryMockServer(Object target) {
        super(target, port);
    }

    private static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }

    public void initServer() {
        String path = this.getClass().getResource("/test-img.jpg").getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        byte[] byteArray = FileUtil.readBytes(file);

        MockServerClient mockClient = new MockServerClient("localhost", port);
        mockClient.when(
                request()
                        .withPath("/upload-octet-stream/test-xxx.jpg")
                        .withMethod("POST")
                        .withHeader(new Header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_OCTET_STREAM))
                        .withBody(byteArray))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(EXPECTED));
    }

}
