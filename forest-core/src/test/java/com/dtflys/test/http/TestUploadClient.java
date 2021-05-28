package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.multipart.ByteArrayMultipart;
import com.dtflys.forest.multipart.FileMultipart;
import com.dtflys.forest.multipart.FilePathMultipart;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.multipart.InputStreamMultipart;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.test.http.client.UploadClient;
import com.dtflys.test.mock.TraceMockServer;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class TestUploadClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestTraceClient.class);

    private static ForestConfiguration configuration;

    private UploadClient uploadClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", TraceMockServer.port);
    }

    public TestUploadClient(HttpBackend backend) {
        super(backend, configuration);
        uploadClient = configuration.createInstance(UploadClient.class);
    }

    private static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
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
    public void testUploadFilePath() {
        String path = this.getClass().getResource("/test-img.jpg").getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        ForestRequest<Map> request = uploadClient.upload(path, progress -> {});
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        ForestMultipart multipart = multipartList.get(0);
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FilePathMultipart);
        assertEquals("file", multipart.getName());
        File file = multipart.getFile();
        assertEquals("test-img.jpg", file.getName());
        assertEquals(path, file.getAbsolutePath().replaceAll("\\\\", "/"));
        assertEquals("test-img.jpg", multipart.getOriginalFileName());
    }


    @Test
    public void testUploadFile() {
        String path = this.getClass().getResource("/test-img.jpg").getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        ForestRequest<Map> request = uploadClient.upload(file, progress -> {});
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        assertTrue(StringUtils.isNotBlank(request.getBoundary()));
        ForestMultipart multipart = multipartList.get(0);
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FileMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("test-img.jpg", multipart.getOriginalFileName());
    }

    @Test
    public void testUploadByteArray() throws IOException {
        URL url = this.getClass().getResource("/test-img.jpg");
        byte[] byteArray = IOUtils.toByteArray(url);
        ForestRequest<Map> request = uploadClient.upload(byteArray, "test-byte-array.jpg");
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        assertTrue(StringUtils.isNotBlank(request.getBoundary()));
        ForestMultipart multipart = multipartList.get(0);
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof ByteArrayMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("test-byte-array.jpg", multipart.getOriginalFileName());
    }

    @Test
    public void testUploadInputStream() throws IOException {
        String path = this.getClass().getResource("/test-img.jpg").getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        InputStream in = new FileInputStream(file);
        ForestRequest<Map> request = uploadClient.upload(in, "test-byte-array.jpg");
        assertNotNull(request);
        assertTrue(StringUtils.isEmpty(request.getBoundary()));
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        ForestMultipart multipart = multipartList.get(0);
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof InputStreamMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("test-byte-array.jpg", multipart.getOriginalFileName());
    }


    /** Test Path Collections **/

    @Test
    public void testUploadPathMap() {
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
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 1;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file", multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            assertEquals("test-map-" + i + ".jpg", multipart.getOriginalFileName());
            i++;
        }
    }


    @Test
    public void testUploadPathMap2() {
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
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file_" + i, multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            assertEquals("test-map-" + (i + 1) + ".jpg", multipart.getOriginalFileName());
            i++;
        }
    }


    @Test
    public void testUploadPathList() {
        URL[] urlArray = new URL[] {
                this.getClass().getResource("/test-img.jpg"),
                this.getClass().getResource("/test-img2.jpg")
        };
        List<String> pathList = new LinkedList<>();
        for (URL url : urlArray) {
            String path = getPathFromURL(url);
            pathList.add(path);
        }
        ForestRequest<Map> request = uploadClient.uploadPathList(pathList);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
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
    }

    @Test
    public void testUploadPathList2() {
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
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
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
    }

    @Test
    public void testUploadPathArray() {
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
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file", multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            assertEquals("test-img-" + i + ".jpg", multipart.getOriginalFileName());
            i++;
        }
    }

    @Test
    public void testUploadPathArray2() {
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
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        int i = 0;
        for (ForestMultipart multipart : multipartList) {
            assertTrue(multipart instanceof FilePathMultipart);
            assertEquals("file_" + i, multipart.getName());
            File file = multipart.getFile();
            assertNotNull(file);
            assertEquals("test-img-" + i + ".jpg", multipart.getOriginalFileName());
            i++;
        }
    }


    /** Test Byte Array Collections **/

    @Test
    public void testUploadByteArrayMap() throws IOException {
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
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
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
    }

    @Test
    public void testUploadByteArrayList() throws IOException {
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
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
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
    }


    @Test
    public void testUploadByteArrayArray() throws IOException {
        URL[] urlArray = new URL[] {
                this.getClass().getResource("/test-img.jpg"),
                this.getClass().getResource("/test-img2.jpg")
        };
        byte[][] byteArrayArray = new byte[urlArray.length][];
        for (int i = 0; i < urlArray.length; i++) {
            URL url = urlArray[i];
            byte[] byteArray = IOUtils.toByteArray(url);
            byteArrayArray[i] = byteArray;
        }
        ForestRequest<Map> request = uploadClient.uploadByteArrayArray(byteArrayArray);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(2, multipartList.size());
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
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
    }

    @Test
    public void testMixtureUploadImage() throws IOException {
        String path = this.getClass().getResource("/test-img.jpg").getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);

        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        ForestRequest request = uploadClient.imageUpload("img1.jpg", file, map);
        assertNotNull(request);
        List<ForestMultipart> multipartList = request.getMultiparts();
        assertEquals(1, multipartList.size());
        ForestMultipart multipart = multipartList.get(0);
        assertTrue(Map.class.isAssignableFrom(request.getMethod().getReturnClass()));
        assertTrue(multipart instanceof FileMultipart);
        assertEquals("file", multipart.getName());
        assertEquals("img1.jpg", multipart.getOriginalFileName());
        List<ForestRequestBody> bodyList = request.getBody();
        assertTrue(bodyList.size() == 1);
        ForestRequestBody body = bodyList.get(0);
        assertTrue(body instanceof NameValueRequestBody);
        assertEquals("meta", ((NameValueRequestBody) body).getName());
    }

}
