package com.dtflys.test.mock;

import okio.GzipSource;
import okio.Source;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.http.HttpHeaders;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:08
 */
public class Get2MockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static Integer port = 5022;

    public Get2MockServer(Object target) {
        super(target, port);
    }

    public void initServer() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = null;
        String str = "测试gzip数据";
        try {
            gzipOut = new GZIPOutputStream(out);
            gzipOut.write(str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                gzipOut.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        MockServerClient mockClient = new MockServerClient("localhost", port);
        mockClient.when(
                request()
                        .withPath("/hello/user")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plain"))
                        .withQueryStringParameter("username", "foo")
                        .withQueryStringParameter("password", "bar")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(EXPECTED)
                );

        mockClient.when(
                request()
                        .withPath("/boolean/true")
                        .withMethod("GET")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("true")
                );


        mockClient.when(
                request()
                        .withPath("/boolean/false")
                        .withMethod("GET")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("false")
                );

        mockClient.when(
                request()
                        .withPath("/gzip")
                        .withMethod("GET")
        )
                .respond(
                        response()
                                .withHeader(new Header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8"))
                                .withHeader(new Header(HttpHeaders.CONTENT_ENCODING, "gzip, deflate"))
                                .withStatusCode(200)
                                .withBody(out.toByteArray())
                );

    }

}
