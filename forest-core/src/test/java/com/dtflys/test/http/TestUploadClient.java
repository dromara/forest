package com.dtflys.test.http;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Editor;
import com.alibaba.fastjson.JSON;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.mock.MockServerRequest;
import com.dtflys.forest.multipart.ByteArrayMultipart;
import com.dtflys.forest.multipart.FileMultipart;
import com.dtflys.forest.multipart.FilePathMultipart;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.multipart.InputStreamMultipart;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.test.http.client.UploadClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class TestUploadClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final UploadClient uploadClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    public TestUploadClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        uploadClient = configuration.createInstance(UploadClient.class);
    }

    private static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

    public static String getPathFromURL(URL url) {
        String path = url.getPath();
        if (isWindows() && path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }

    private static void assertByteArrayEquals(byte[] bArray1, byte[] bArray2) {
        assertEquals(bArray1.length, bArray2.length);
        for (int i = 0; i < bArray1.length; i++) {
            byte bLeft = bArray1[i];
            byte bRight = bArray2[i];
            assertEquals(bLeft, bRight);
        }
    }


    @Test
    public void testUploadFilePath() throws FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        ForestRequest<Map> request = uploadClient.upload(path, progress -> {});
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FilePathMultipart);
        assertEquals("file", multipart.getName());
        File file = multipart.getFile();
        assertEquals("test-img.jpg", file.getName());
        assertEquals(path, file.getAbsolutePath().replaceAll("\\\\", "/"));
        assertEquals("test-img.jpg", multipart.getOriginalFileName());
        Object result = request.asObject();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(1, fileItems.size());
                    FileItem fileItem = fileItems.get(0);
                    assertEquals("test-img.jpg", fileItem.getName());
                    assertEquals("image/jpeg", fileItem.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem.getInputStream());
                        assertArrayEquals(IOUtils.toByteArray(new FileInputStream(file)), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private String getFilePath(final String filePath) {
        String path = Objects.requireNonNull(this.getClass().getResource(filePath)).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        return path;
    }

    @Test
    public void testUploadFile() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = getFilePath("/test-img.jpg");
        File file = new File(path);
        ForestRequest<Map> request = uploadClient.upload(file, progress -> {});
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        assertTrue(StringUtils.isNotBlank(request.getBoundary()));
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FileMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("test-img.jpg", multipart.getOriginalFileName());
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(1, fileItems.size());
                    FileItem fileItem = fileItems.get(0);
                    assertEquals("test-img.jpg", fileItem.getName());
                    assertEquals("image/jpeg", fileItem.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem.getInputStream());
                        assertArrayEquals(IOUtils.toByteArray(new FileInputStream(file)), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testUploadFile_emptyFile() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<Map> request = uploadClient.upload((File) null, progress -> {});
        Map result = request.asMap();
        assertNotNull(result);
    }


    @Test
    public void testUploadFile_withParams() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        ForestRequest<Map> request = uploadClient.upload_withParams(file, "foo", "bar");
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        assertTrue(StringUtils.isNotBlank(request.getBoundary()));
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FileMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("test-img.jpg", multipart.getOriginalFileName());
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(1, fileItems.size());
                    FileItem fileItem = fileItems.get(0);
                    assertEquals("test-img.jpg", fileItem.getName());
                    assertEquals("image/jpeg", fileItem.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem.getInputStream());
                        assertArrayEquals(IOUtils.toByteArray(new FileInputStream(file)), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .assertMultipart("a", "text/plain", "foo")
                .assertMultipart("b", "text/plain", "bar");
    }


    @Test
    public void testUploadByteArray() throws IOException, InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        URL url = this.getClass().getResource("/test-img.jpg");
        byte[] byteArray = IOUtils.toByteArray(url);
        ForestRequest<Map> request = uploadClient.upload(byteArray, "test-byte-array.jpg");
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        assertTrue(StringUtils.isNotBlank(request.getBoundary()));
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof ByteArrayMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("test-byte-array.jpg", multipart.getOriginalFileName());
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(1, fileItems.size());
                    FileItem fileItem = fileItems.get(0);
                    assertEquals("test-byte-array.jpg", fileItem.getName());
                    assertEquals("image/jpeg", fileItem.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem.getInputStream());
                        assertArrayEquals(byteArray, bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testUploadInputStream() throws IOException, InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = this.getClass().getResource("/test-img.jpg").getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        InputStream in = new FileInputStream(file);
        ForestRequest<Map> request = uploadClient.upload(in, "test-byte-array.jpg");
        assertNotNull(request);
        assertTrue(StringUtils.isNotEmpty(request.getBoundary()));
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof InputStreamMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("test-byte-array.jpg", multipart.getOriginalFileName());
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(1, fileItems.size());
                    FileItem fileItem = fileItems.get(0);
                    assertEquals("test-byte-array.jpg", fileItem.getName());
                    assertEquals("image/jpeg", fileItem.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem.getInputStream());
                        assertArrayEquals(IOUtils.toByteArray(new FileInputStream(file)), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    /** Test Path Collections **/

    @Test
    public void testUploadPathMap() throws FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path1 = this.getClass().getResource("/test-img.jpg").getPath();
        String path2 = this.getClass().getResource("/test-img2.jpg").getPath();
        Map<String, String> pathMap = new LinkedHashMap<>();
        pathMap.put("test-map-1.jpg", path1);
        pathMap.put("test-map-2.jpg", path2);
        for (String key : pathMap.keySet()) {
            String value = pathMap.get(key);
            if (isWindows() && value.startsWith("/")) {
                value = value.substring(1);
            }
            pathMap.put(key, value);
        }
        ForestRequest<Map> request = uploadClient.uploadPathMap(pathMap);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
//        assertTrue(Map.class.isAssignableFrom(request.getLifeCycleHandler().getResultType()));
        int i = 1;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file", multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            assertEquals("test-map-" + i + ".jpg", multipart.getOriginalFileName());
            i++;
        }
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(2, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-map-1.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        assertArrayEquals(IOUtils.toByteArray(new FileInputStream(path1)), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    FileItem fileItem2 = fileItems.get(1);
                    assertEquals("test-map-2.jpg", fileItem2.getName());
                    assertEquals("image/jpeg", fileItem2.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem2.getInputStream());
                        assertArrayEquals(IOUtils.toByteArray(new FileInputStream(path2)), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    @Test
    public void testUploadPathMap2() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path1 = this.getClass().getResource("/test-img.jpg").getPath();
        String path2 = this.getClass().getResource("/test-img2.jpg").getPath();
        Map<String, String> pathMap = new LinkedHashMap<>();
        pathMap.put("test-map-1.jpg", path1);
        pathMap.put("test-map-2.jpg", path2);
        for (String key : pathMap.keySet()) {
            String value = pathMap.get(key);
            if (isWindows() && value.startsWith("/")) {
                value = value.substring(1);
            }
            pathMap.put(key, value);
        }
        ForestRequest<Map> request = uploadClient.uploadPathMap2(pathMap);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file_" + i, multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            assertEquals("test-map-" + (i + 1) + ".jpg", multipart.getOriginalFileName());
            i++;
        }
        Map result = request.asMap();
        assertNotNull(result);

        mockRequest(server)
                .assertMultipart("file_0", fileItems1 -> {
                    assertEquals(1, fileItems1.size());
                    FileItem fileItem1 = fileItems1.get(0);
                    assertEquals("test-map-1.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        assertArrayEquals(IOUtils.toByteArray(new FileInputStream(path1)), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .assertMultipart("file_0", fileItems2 -> {
                    assertEquals(1, fileItems2.size());
                    FileItem fileItem1 = fileItems2.get(0);
                    assertEquals("test-map-1.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        assertArrayEquals(IOUtils.toByteArray(new FileInputStream(path1)), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    @Test
    public void testUploadPathList() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        URL[] urlArray = new URL[] {
                this.getClass().getResource("/test-img.jpg"),
                this.getClass().getResource("/test-img2.jpg")
        };
        List<String> pathList = new LinkedList<>();
        for (URL url : urlArray) {
            assertNotNull(url);
            String path = getPathFromURL(url);
            pathList.add(path);
        }
        ForestRequest<Map> request = uploadClient.uploadPathList(pathList);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file", multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            URL url = urlArray[i];
            assertEquals(getPathFromURL(url), file.getAbsolutePath().replaceAll("\\\\", "/"));
            assertEquals(getPathFromURL(url), ((FilePathMultipart) multipart).getFilePath());
            assertEquals("test-img-" + i + ".jpg", multipart.getOriginalFileName());
            i++;
        }
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(2, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-img-0.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        URL url = urlArray[0];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    FileItem fileItem2 = fileItems.get(1);
                    assertEquals("test-img-1.jpg", fileItem2.getName());
                    assertEquals("image/jpeg", fileItem2.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem2.getInputStream());
                        URL url = urlArray[1];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testUploadPathList2() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        URL[] urlArray = new URL[] {
                this.getClass().getResource("/test-img.jpg"),
                this.getClass().getResource("/test-img2.jpg")
        };
        List<String> pathList = new LinkedList<>();
        for (URL url : urlArray) {
            String path = getPathFromURL(url);
            pathList.add(path);
        }
        ForestRequest<Map> request = uploadClient.uploadPathList2(pathList);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file_" + i, multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            URL url = urlArray[i];
            assertEquals(getPathFromURL(url), file.getAbsolutePath().replaceAll("\\\\", "/"));
            assertEquals(getPathFromURL(url), ((FilePathMultipart) multipart).getFilePath());
            assertEquals("test-img-" + i + ".jpg", multipart.getOriginalFileName());
            i++;
        }
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file_0", fileItems -> {
                    assertEquals(1, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-img-0.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        URL url = urlArray[0];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .assertMultipart("file_1", fileItems -> {
                    assertEquals(1, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-img-1.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        URL url = urlArray[1];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testUploadPathArray() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        URL[] urlArray = new URL[] {
                this.getClass().getResource("/test-img.jpg"),
                this.getClass().getResource("/test-img2.jpg")
        };
        String[] pathArray = new String[urlArray.length];
        for (int i = 0; i < urlArray.length; i++) {
            URL url = urlArray[i];
            String path = getPathFromURL(url);
            pathArray[i] = path;
        }
        ForestRequest<Map> request = uploadClient.uploadPathArray(pathArray);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file", multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            assertEquals("test-img-" + i + ".jpg", multipart.getOriginalFileName());
            i++;
        }
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(2, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-img-0.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        URL url = urlArray[0];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    FileItem fileItem2 = fileItems.get(1);
                    assertEquals("test-img-1.jpg", fileItem2.getName());
                    assertEquals("image/jpeg", fileItem2.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem2.getInputStream());
                        URL url = urlArray[1];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testUploadPathArray2() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        URL[] urlArray = new URL[] {
                this.getClass().getResource("/test-img.jpg"),
                this.getClass().getResource("/test-img2.jpg")
        };
        String[] pathArray = new String[urlArray.length];
        for (int i = 0; i < urlArray.length; i++) {
            URL url = urlArray[i];
            String path = getPathFromURL(url);
            pathArray[i] = path;
        }
        ForestRequest<Map> request = uploadClient.uploadPathArray2(pathArray);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file_" + i, multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            assertEquals("test-img-" + i + ".jpg", multipart.getOriginalFileName());
            i++;
        }
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file_0", fileItems -> {
                    assertEquals(1, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-img-0.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        URL url = urlArray[0];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .assertMultipart("file_1", fileItems -> {
                    assertEquals(1, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-img-1.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        URL url = urlArray[1];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    /** Test Byte Array Collections **/

    @Test
    public void testUploadByteArrayMap() throws IOException, InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        URL[] urlArray = new URL[] {
                this.getClass().getResource("/test-img.jpg"),
                this.getClass().getResource("/test-img2.jpg")
        };
        Map<String, byte[]> byteArrayMap = new LinkedHashMap<>();
        for (int i = 0; i < urlArray.length; i++) {
            URL url = urlArray[i];
            byte[] byteArray = IOUtils.toByteArray(url);
            byteArrayMap.put("test-img-" + i + ".jpg", byteArray);
        }
        ForestRequest<Map> request = uploadClient.uploadByteArrayMap(byteArrayMap);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof ByteArrayMultipart);
            assertEquals("file", multipart.getName());
            byte[] bytes = multipart.getBytes();
            String key = "test-img-" + i + ".jpg";
            byte[] byteArray = byteArrayMap.get(key);
            assertNotNull(bytes);
            assertByteArrayEquals(byteArray, bytes);
            assertEquals(key, multipart.getOriginalFileName());
            i++;
        }
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(2, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-img-0.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        URL url = urlArray[0];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    FileItem fileItem2 = fileItems.get(1);
                    assertEquals("test-img-1.jpg", fileItem2.getName());
                    assertEquals("image/jpeg", fileItem2.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem2.getInputStream());
                        URL url = urlArray[1];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testUploadByteArrayList() throws IOException, InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        URL[] urlArray = new URL[] {
                this.getClass().getResource("/test-img.jpg"),
                this.getClass().getResource("/test-img2.jpg")
        };
        List<byte[]> byteArrayList = new LinkedList<>();
        for (int i = 0; i < urlArray.length; i++) {
            URL url = urlArray[i];
            byte[] byteArray = IOUtils.toByteArray(url);
            byteArrayList.add(byteArray);
        }
        ForestRequest<Map> request = uploadClient.uploadByteArrayList(byteArrayList);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof ByteArrayMultipart);
            assertEquals("file", multipart.getName());
            byte[] bytes = multipart.getBytes();
            String key = "test-img-" + i + ".jpg";
            byte[] byteArray = byteArrayList.get(i);
            assertNotNull(bytes);
            assertByteArrayEquals(byteArray, bytes);
            assertEquals(key, multipart.getOriginalFileName());
            i++;
        }
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(2, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-img-0.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        URL url = urlArray[0];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    FileItem fileItem2 = fileItems.get(1);
                    assertEquals("test-img-1.jpg", fileItem2.getName());
                    assertEquals("image/jpeg", fileItem2.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem2.getInputStream());
                        URL url = urlArray[1];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    @Test
    public void testUploadByteArrayArray() throws IOException, InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        URL[] urlArray = new URL[] {
                this.getClass().getResource("/test-img.jpg"),
                this.getClass().getResource("/test-img2.jpg")
        };
        byte[][] byteArrayArray = new byte[urlArray.length][];
        for (int i = 0; i < urlArray.length; i++) {
            URL url = urlArray[i];
            assertNotNull(url);
            byte[] byteArray = IOUtils.toByteArray(url);
            byteArrayArray[i] = byteArray;
        }
        ForestRequest<Map> request = uploadClient.uploadByteArrayArray(byteArrayArray);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof ByteArrayMultipart);
            assertEquals("file", multipart.getName());
            byte[] bytes = multipart.getBytes();
            String key = "test-img-" + i + ".jpg";
            byte[] byteArray = byteArrayArray[i];
            assertNotNull(bytes);
            assertByteArrayEquals(byteArray, bytes);
            assertEquals(key, multipart.getOriginalFileName());
            i++;
        }
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", fileItems -> {
                    assertEquals(2, fileItems.size());
                    FileItem fileItem1 = fileItems.get(0);
                    assertEquals("test-img-0.jpg", fileItem1.getName());
                    assertEquals("image/jpeg", fileItem1.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem1.getInputStream());
                        URL url = urlArray[0];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    FileItem fileItem2 = fileItems.get(1);
                    assertEquals("test-img-1.jpg", fileItem2.getName());
                    assertEquals("image/jpeg", fileItem2.getContentType());
                    try {
                        byte[] bytes = IOUtils.toByteArray(fileItem2.getInputStream());
                        URL url = urlArray[1];
                        assertNotNull(url);
                        assertArrayEquals(IOUtils.toByteArray(
                                new FileInputStream(url.getFile())), bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testMixtureUploadImage() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        ForestRequest request = uploadClient.imageUploadWithMapParams("img1.jpg", file, map);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FileMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("img1.jpg", multipart.getOriginalFileName());
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", multiparts -> {
                    assertEquals(1, multiparts.size());
                    FileItem fileItem = multiparts.get(0);
                    assertEquals("img1.jpg", fileItem.getName());
                    assertEquals("image/jpeg", fileItem.getContentType());

                })
                .assertMultipart("a", params -> {
                    assertEquals(1, params.size());
                    FileItem item = params.get(0);
                    ContentType contentType = new ContentType(item.getContentType());
                    assertEquals("text/plain", contentType.toStringWithoutParameters());
                    try {
                        assertEquals("1", IOUtils.toString(item.getInputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .assertMultipart("b", params -> {
                    assertEquals(1, params.size());
                    FileItem item = params.get(0);
                    ContentType contentType = new ContentType(item.getContentType());
                    assertEquals("text/plain", contentType.toStringWithoutParameters());
                    try {
                        assertEquals("2", IOUtils.toString(item.getInputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testMixtureUploadImageWithBodyParams() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        ForestRequest request = uploadClient.imageUploadWithBodyParams("img1.jpg", file, "1", "2");
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FileMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("img1.jpg", multipart.getOriginalFileName());
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", multiparts -> {
                    assertEquals(1, multiparts.size());
                    FileItem fileItem = multiparts.get(0);
                    assertEquals("img1.jpg", fileItem.getName());
                    assertEquals("image/jpeg", fileItem.getContentType());
                })
                .assertMultipart("a", params -> {
                    assertEquals(1, params.size());
                    FileItem item = params.get(0);
                    ContentType contentType = new ContentType(item.getContentType());
                    assertEquals("text/plain", contentType.toStringWithoutParameters());
                    try {
                        assertEquals("1", IOUtils.toString(item.getInputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .assertMultipart("b", params -> {
                    assertEquals(1, params.size());
                    FileItem item = params.get(0);
                    ContentType contentType = new ContentType(item.getContentType());
                    assertEquals("text/plain", contentType.toStringWithoutParameters());
                    try {
                        assertEquals("2", IOUtils.toString(item.getInputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testMixtureUploadImageWithJSONBodyParams() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        ForestRequest request = uploadClient.imageUploadWithJSONBodyParams("img1.jpg", file, map);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FileMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("img1.jpg", multipart.getOriginalFileName());
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", multiparts -> {
                    assertEquals(1, multiparts.size());
                    FileItem fileItem = multiparts.get(0);
                    assertEquals("img1.jpg", fileItem.getName());
                    assertEquals("image/jpeg", fileItem.getContentType());
                })
                .assertMultipart("params", params -> {
                    assertEquals(1, params.size());
                    FileItem item = params.get(0);
                    ContentType contentType = new ContentType(item.getContentType());
                    assertEquals("application/json", contentType.toStringWithoutParameters());
                    try {
                        assertEquals(JSON.toJSONString(map), IOUtils.toString(item.getInputStream()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void testMixtureImageUploadWithJSONBodyParamsAndWithoutContentType() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        ForestRequest request = uploadClient.imageUploadWithJSONBodyParamsAndWithoutContentType(
                "img1.jpg", file, map);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FileMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("img1.jpg", multipart.getOriginalFileName());
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("file", multiparts -> {
                    assertEquals(1, multiparts.size());
                    FileItem fileItem = multiparts.get(0);
                    assertEquals("img1.jpg", fileItem.getName());
                    assertEquals("image/jpeg", fileItem.getContentType());
                })
                .assertMultipart("params", params -> {
                    assertEquals(1, params.size());
                    FileItem item = params.get(0);
                    ContentType contentType = new ContentType(item.getContentType());
                    assertEquals("application/json", contentType.toStringWithoutParameters());
                    try {
                        assertEquals(JSON.toJSONString(map), IOUtils.toString(item.getInputStream(), "UTF-8"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }



    @Test
    public void testCancelUploadFile() throws InterruptedException, FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = Objects.requireNonNull(this.getClass().getResource("/test-img.jpg")).getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        ForestRequest<Map> request = uploadClient.upload(file, progress -> {
            if (progress.getRate() > 0.3F) {
                progress.getRequest().cancel();
                System.out.println("Progress: " + progress.getRate());
            }
        });
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        AtomicBoolean isError = new AtomicBoolean(false);
        request.onCanceled((req, res) -> {
            isCanceled.set(true);
        });
        request.onError((ex, req, res) -> {
            isError.set(true);
        });
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        assertTrue(StringUtils.isNotBlank(request.getBoundary()));
        ForestMultipart multipart = multipartList.get(0);
//        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FileMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("test-img.jpg", multipart.getOriginalFileName());
        request.execute();
        assertThat(isCanceled.get()).isTrue();
        assertThat(isError.get()).isFalse();
        assertThat(request.isCanceled()).isTrue();
    }

    @Test
    public void testUploadFileWithModel() throws FileUploadException {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String path = getFilePath("/test-img.jpg");
        File file = new File(path);
        UploadClient.Model model = new UploadClient.Model();
        model.setId("111");
        ForestRequest<Map> request = uploadClient.uploadWithModel(file, model);
        request.addBody("key", "value");
        Map result = request.asMap();
        assertNotNull(result);
        mockRequest(server)
                .assertMultipart("id", params -> {
                    try {
                        assertThat(IOUtils.toString(params.get(0).get(), "UTF-8")).isEqualTo("111");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .assertMultipart("key", params -> {
                    try {
                        assertThat(IOUtils.toString(params.get(0).get(), "UTF-8")).isEqualTo("value");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


}
