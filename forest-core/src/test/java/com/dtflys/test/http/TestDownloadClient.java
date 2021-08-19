package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ForestProgress;
import com.dtflys.test.http.client.DownloadClient;
import com.dtflys.test.mock.DownloadMockServer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-05 22:40
 */
public class TestDownloadClient extends BaseClientTest {

    @Rule
    public MockWebServer server = new MockWebServer();

    public DownloadMockServer mockserver = new DownloadMockServer(this);

    private static ForestConfiguration configuration;

    private DownloadClient downloadClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    @Override
    public void afterRequests() {
    }

    public TestDownloadClient(HttpBackend backend) {
        super(backend, configuration);
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
    public void testDownloadImage() {
        server.enqueue(new MockResponse().setBody(getImageBuffer()));
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        AtomicReference<ForestProgress> atomicProgress = new AtomicReference<>(null);
        File file = downloadClient.downloadImage(dir, "temp-img.png", progress -> {
            System.out.println("------------------------------------------");
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
            if (progress.isDone()) {
                atomicProgress.set(progress);
                assertThat(progress.getRequest()).isNotNull();
            }
        });
        assertThat(file)
                .isNotNull()
                .isFile();
        assertThat(atomicProgress.get())
                .isNotNull()
                .extracting(
                        ForestProgress::isDone,
                        ForestProgress::getRate)
                .contains(true, 1D);
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


}
