package com.dtflys.forest.test.http;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ResponseResult;
import com.dtflys.forest.test.http.client.DownloadClient;
import com.dtflys.forest.test.http.model.UserParam;
import com.dtflys.forest.utils.ForestProgress;
import com.dtflys.forest.utils.TypeReference;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.Okio;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-05 22:40
 */
public class TestDownloadClient extends BaseClientTest {

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private DownloadClient downloadClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    public TestDownloadClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        downloadClient = configuration.createInstance(DownloadClient.class);
    }


    public Buffer getImageBuffer() {
        URL url = this.getClass().getResource("/test-img.jpg");
        byte[] byteArray = new byte[0];
        try {
            byteArray = IOUtils.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Buffer buffer = new Buffer();
        try {
            buffer.readFrom(new ByteArrayInputStream(byteArray));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }

    @Test
    public void testDownloadImage() throws InterruptedException {
        int count = 248;
        for (int i = 0; i < count; i++) {
            server.enqueue(new MockResponse().setBody(getImageBuffer()));
        }
        AtomicReference<ForestProgress> atomicProgress = new AtomicReference<>(null);
        Map<Integer, String> pathMap = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            int finalI = i;
            String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload/" + i;
            CompletableFuture.runAsync(() -> {
                File file = downloadClient.downloadImage(dir, "temp-img-" + finalI + ".png", finalI, progress -> {
                    System.out.println("------------------------------------------");
                    System.out.println("total bytes: " + progress.getTotalBytes());
                    System.out.println("current bytes: " + progress.getCurrentBytes());
                    System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
                    if (progress.isDone()) {
                        atomicProgress.set(progress);
                        assertThat(progress.getRequest()).isNotNull();
                        pathMap.put(finalI, progress.getRequest().path());
                        latch.countDown();
                    }
                });
                assertThat(file)
                        .isNotNull()
                        .isFile();
                assertThat(file.exists()).isTrue();
                assertThat(atomicProgress.get())
                        .isNotNull()
                        .extracting(
                                ForestProgress::isDone,
                                ForestProgress::getRate)
                        .contains(true, 1D);
            });
        }
        latch.await();
        for (int i = 0; i < count; i++) {
            String path = pathMap.get(i);
            assertThat(path).isNotNull().isEqualTo("/download/test-img-" + i + ".jpg");
        }
    }

    @Test
    public void testDownloadFile() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));
        AtomicReference<ForestProgress> atomicProgress = new AtomicReference<>(null);
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        ForestResponse<File> response = downloadClient.downloadFile(dir, progress -> {
            System.out.println("------------------------------------------");
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
            if (progress.isDone()) {
                atomicProgress.set(progress);
            }
        });
        File file = response.getResult();
        assertThat(response)
                .isNotNull()
                .extracting(ForestResponse::getStatusCode)
                .isEqualTo(200);
        assertThat(file)
                .isNotNull()
                .isFile();
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        byte[] fileBytes = IOUtils.toByteArray(new FileInputStream(file));
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);
        assertThat(atomicProgress.get())
                .isNotNull()
                .extracting(
                        ForestProgress::isDone,
                        ForestProgress::getRate,
                        ForestProgress::getRequest)
                .contains(true, 1D, response.getRequest());
    }

    @Test
    public void testDownloadFileToBytes() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));
        AtomicReference<ForestProgress> atomicProgress = new AtomicReference<>(null);
        byte[] bytes = downloadClient.downloadFileToBytes(progress -> {
            System.out.println("------------------------------------------");
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
            if (progress.isDone()) {
                System.out.println("--------   Download Completed!   --------");
                atomicProgress.set(progress);
            }
        });
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        assertThat(bytes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(out.length)
                .isEqualTo(out);
        assertThat(atomicProgress.get())
                .isNotNull()
                .extracting(ForestProgress::isDone, ForestProgress::getRate)
                .contains(true, 1D);
    }


    @Test
    public void testDownloadImageFile() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        File file = downloadClient.downloadImageFile(dir);
        assertThat(file)
                .isNotNull()
                .isFile()
                .hasName("test-img.jpg");
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        byte[] fileBytes = IOUtils.toByteArray(new FileInputStream(file));
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);
    }


    @Test
    public void testDownloadAsInputStream() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));
        server.enqueue(new MockResponse().setBody(buffer));
        server.enqueue(new MockResponse().setBody(buffer));

        buffer = getImageBuffer();
        InputStream in = downloadClient.downloadAsInputStream();
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        byte[] fileBytes = IOUtils.toByteArray(in);
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);

        buffer = getImageBuffer();
        in = downloadClient.downloadAsInputStream();
        bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        out = bytesOut.toByteArray();
        fileBytes = IOUtils.toByteArray(in);
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);

        buffer = getImageBuffer();
        in = downloadClient.downloadAsInputStream();
        bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        out = bytesOut.toByteArray();
        fileBytes = IOUtils.toByteArray(in);
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);

    }


    @Test
    public void testDownloadAsResponse() throws Exception {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));
        server.enqueue(new MockResponse().setBody(buffer));
        server.enqueue(new MockResponse().setBody(buffer));

        buffer = getImageBuffer();
        ForestResponse response = downloadClient.downloadAsInputResponse();
        InputStream in = response.getInputStream();

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        byte[] fileBytes = IOUtils.toByteArray(in);
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);

        buffer = getImageBuffer();
        response = downloadClient.downloadAsInputResponse();
        in = response.getInputStream();

        bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        out = bytesOut.toByteArray();
        fileBytes = IOUtils.toByteArray(in);
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);

        buffer = getImageBuffer();
        response = downloadClient.downloadAsInputResponse();
        in = response.getInputStream();

        bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        out = bytesOut.toByteArray();
        fileBytes = IOUtils.toByteArray(in);
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);
    }

    @Test
    public void testDownloadWithQuickApi() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));
        InputStream in = Forest.get("http://localhost:" + server.getPort() + "/download/test-img.jpg")
                .execute(InputStream.class);

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        byte[] fileBytes = IOUtils.toByteArray(in);
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);
    }


    @Test
    public void testDownloadWithQuickAcceptStream() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        AtomicBoolean acceptDone = new AtomicBoolean(false);
        AtomicBoolean responseBytesRead = new AtomicBoolean(false);

        Forest.get("http://localhost:{}/download/test-img.jpg", server.getPort())
                .onResponse((req, res) -> {
                    if (res.isError()) {
                        return ResponseResult.error();
                    }
                    res.openStream((in, _res) -> {
                        responseBytesRead.set(res.isBytesRead());
                        System.out.println("Accept stream");
                        try {
                            byte[] fileBytes = IOUtils.toByteArray(in);
                            assertThat(fileBytes)
                                    .hasSize(out.length)
                                    .isEqualTo(out);
                            acceptDone.set(true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    return ResponseResult.proceed();
                })
                .execute(new TypeReference<ForestResponse<InputStream>>() {});
        assertThat(acceptDone.get()).isTrue();
        assertThat(responseBytesRead.get()).isFalse();
    }


    @Test
    public void testDownloadWithInputStreamResponse() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        AtomicBoolean acceptDone = new AtomicBoolean(false);
        AtomicBoolean responseBytesRead = new AtomicBoolean(false);

        Forest.get("http://localhost:{}/download/test-img.jpg", server.getPort())
                .execute(new TypeReference<ForestResponse<InputStream>>() {})
                .openStream((in, res) -> {
                    responseBytesRead.set(res.isBytesRead());
                    System.out.println("Accept stream");
                    try {
                        byte[] fileBytes = IOUtils.toByteArray(in);
                        assertThat(fileBytes)
                                .hasSize(out.length)
                                .isEqualTo(out);
                        acceptDone.set(true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        assertThat(acceptDone.get()).isTrue();
        assertThat(responseBytesRead.get()).isFalse();
    }


    @Test
    public void testDownloadWithExecuteAsStream() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        AtomicBoolean acceptDone = new AtomicBoolean(false);
        AtomicBoolean responseBytesRead = new AtomicBoolean(false);

        Forest.get("http://localhost:{}/download/test-img.jpg", server.getPort())
                .executeAsStream((in, req, res) -> {
                    responseBytesRead.set(res.isBytesRead());
                    System.out.println("Accept stream");
                    try {
                        byte[] fileBytes = IOUtils.toByteArray(in);
                        assertThat(fileBytes)
                                .hasSize(out.length)
                                .isEqualTo(out);
                        acceptDone.set(true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        assertThat(acceptDone.get()).isTrue();
        assertThat(responseBytesRead.get()).isFalse();
    }


    @Test
    public void testDownloadWithExecuteAsStream2() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        AtomicBoolean acceptDone = new AtomicBoolean(false);
        AtomicBoolean responseBytesRead = new AtomicBoolean(false);

        byte[] bytes = Forest.get("http://localhost:{}/download/test-img.jpg", server.getPort())
                .executeAsStream((in, req, res) -> {
                    responseBytesRead.set(res.isBytesRead());
                    System.out.println("Accept stream");
                    try {
                        byte[] fileBytes = IOUtils.toByteArray(in);
                        acceptDone.set(true);
                        return fileBytes;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        assertThat(bytes).isEqualTo(out);
        assertThat(acceptDone.get()).isTrue();
        assertThat(responseBytesRead.get()).isFalse();
    }



    @Test
    public void testDownloadWithQuickAcceptStreamFunction() throws IOException {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        buffer.readAll(Okio.sink(bytesOut));
        byte[] out = bytesOut.toByteArray();
        AtomicBoolean acceptDone = new AtomicBoolean(false);

        byte[] fileBytes = Forest.get("http://localhost:{}/download/test-img.jpg", server.getPort())
                .executeAsResponse()
                .openStream((in, res) -> {
                    try {
                        return IOUtils.toByteArray(in);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        acceptDone.set(true);
                    }
                });
        assertThat(fileBytes)
                .hasSize(out.length)
                .isEqualTo(out);
        assertThat(acceptDone.get()).isTrue();
    }



    @Test
    public void testDownloadFileReturnNothing() {
        Buffer buffer = getImageBuffer();
        server.enqueue(new MockResponse().setBody(buffer));
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        downloadClient.downloadFileReturnNothing(dir, progress ->
                System.out.println(progress.getRate() * 100.0));
        File targetFile = new File(dir + File.separator + "test-img-void.jpg");
        assertThat(targetFile).isNotNull();
        assertThat(targetFile.exists()).isTrue();
    }

}
