package com.dtflys.forest.mock;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author gongjun
 */
public class MockServerRequest {

    private final RecordedRequest request;

    private final HttpUrl url;

    private String stringBodyValue;

    private Map<String, List<FileItem>> multiparts;

    public MockServerRequest(RecordedRequest request) {
        this.request = request;
        this.url = request.getRequestUrl();
    }

    public static MockServerRequest mockRequest(MockWebServer server) {
        try {
            RecordedRequest request = server.takeRequest();
            return new MockServerRequest(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String requestLine() {
        return request.getRequestLine();
    }

    public String scheme() {
        return url.scheme();
    }

    public String method() {
        return request.getMethod();
    }

    public String path() {
        return url.encodedPath();
    }

    public String query() {
        return url.query();
    }

    public String username() {
        return url.username();
    }

    public String password() {
        return url.password();
    }

    public String encodedQuery() {
        return url.encodedQuery();
    }

    public String query(String name) {
        return url.queryParameter(name);
    }

    public Headers headers() {
        return request.getHeaders();
    }

    public String header(String name) {
        return request.getHeader(name);
    }

    public byte[] bodyAsBytes() {
        return request.getBody().readByteArray();
    }

    public InputStream bodyAsInputStream() {
        return request.getBody().inputStream();
    }

    public Map<String, List<FileItem>> multiparts(String contentType) throws FileUploadException {
        if (multiparts != null) {
            return multiparts;
        }
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        HttpServletRequest request = new MockHttpServletRequest(bodyAsBytes(), contentType);
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            multiparts = new HashMap<>();
            return multiparts;
        }
        List<FileItem> items = upload.parseRequest(request);
        this.multiparts = items.stream()
                .collect(Collectors.toMap(FileItem::getFieldName, item -> {
                    List<FileItem> list = new ArrayList<>();
                    list.add(item);
                    return list;
                }, (oldList, newList) -> {
                    oldList.addAll(newList);
                    return oldList;
                }));
        return multiparts;
    }

    public Map<String, List<FileItem>> multiparts() throws FileUploadException {
        return this.multiparts(header(FileUploadBase.CONTENT_TYPE));
    }

    public List<FileItem> multipart(String name, String contentType) throws FileUploadException {
        return this.multiparts(contentType).get(name);
    }

    public List<FileItem> multipart(String name) throws FileUploadException {
        return this.multipart(name, header(FileUploadBase.CONTENT_TYPE));
    }


    public String bodyAsString(Charset charset) {
        if (stringBodyValue == null) {
            stringBodyValue = request.getBody().readString(charset);
        }
        return stringBodyValue;
    }

    public String bodyAsString() {
        return bodyAsString(StandardCharsets.UTF_8);
    }

    public MockServerRequest assertRequestLineEquals(String expected) {
        Assert.assertEquals(expected, requestLine());
        return this;
    }


    public MockServerRequest assertSchemeEquals(String expected) {
        Assert.assertEquals(expected, scheme());
        return this;
    }


    public MockServerRequest assertMethodEquals(String expected) {
        Assert.assertEquals(expected, method());
        return this;
    }

    public MockServerRequest assertBodyNotEmpty() {
        Assert.assertNotEquals(0, request.getBodySize());
        return this;
    }

    public MockServerRequest assertBodyEmpty() {
        Assert.assertEquals(0, request.getBodySize());
        return this;
    }

    public MockServerRequest assertBodyEquals(String expected) {
        Assert.assertEquals(expected, bodyAsString());
        return this;
    }

    public MockServerRequest assertBodyEquals(byte[] expected) {
        Assert.assertArrayEquals(expected, bodyAsBytes());
        return this;
    }

    public MockServerRequest assertUsernameEquals(String username) {
        Assert.assertEquals(username, username());
        return this;
    }

    public MockServerRequest assertPasswordEquals(String password) {
        Assert.assertEquals(password, password());
        return this;
    }

    public MockServerRequest assertPathEquals(String path) {
        Assert.assertEquals(path, path());
        return this;
    }

    public MockServerRequest assertQueryEquals(String expected) {
        Assert.assertEquals(expected, query());
        return this;
    }

    public MockServerRequest assertQueryEquals(String name, String expected) {
        Assert.assertEquals(expected, query(name));
        return this;
    }

    public MockServerRequest assertEncodedQueryEquals(String expected) {
        Assert.assertEquals(expected, encodedQuery());
        return this;
    }


    public MockServerRequest assertHeaderEquals(String name, String expected) {
        Assert.assertEquals(expected, header(name));
        return this;
    }



    public MockServerRequest assertMultipart(String name, String contentType, Consumer<List<FileItem>> itemConsumer) throws FileUploadException {
        if (itemConsumer != null) {
            itemConsumer.accept(this.multipart(name, contentType));
        }
        return this;
    }

    public MockServerRequest assertMultipart(String name, Consumer<List<FileItem>> itemConsumer) throws FileUploadException {
        return assertMultipart(name, header(FileUploadBase.CONTENT_TYPE), itemConsumer);
    }

    public MockServerRequest assertMultipart(String name, String contentType, String text) throws FileUploadException {
        return assertMultipart(name, contentType, params -> {
            assertEquals(1, params.size());
            FileItem item = params.get(0);
            try {
                assertEquals(text, IOUtils.toString(item.getInputStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
