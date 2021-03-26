package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ProgressUtils;
import com.dtflys.test.http.client.DownloadClient;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.DownloadMockServer;
import com.dtflys.test.mock.ErrorMockServer;
import com.dtflys.test.mock.GetMockServer;
import com.twitter.finagle.http.path.Path;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-05 22:40
 */
public class TestDownloadClient extends BaseClientTest {

    @Rule
    public DownloadMockServer server = new DownloadMockServer(this);

    private static ForestConfiguration configuration;

    private DownloadClient downloadClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", DownloadMockServer.port);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    public TestDownloadClient(HttpBackend backend) {
        super(backend, configuration);
        downloadClient = configuration.createInstance(DownloadClient.class);
    }

    @Test
    public void testDownloadImage() {
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        File file = downloadClient.downloadImage(dir, "temp-img.png", progress -> {
            System.out.println("------------------------------------------");
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
        });
        assertNotNull(file);
        assertTrue(file.exists());
    }

    @Test
    public void testDownloadFile() {
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        ForestResponse<File> response = downloadClient.downloadFile(dir, progress -> {
            System.out.println("------------------------------------------");
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
        });
        assertNotNull(response);
        File file = response.getResult();
        assertNotNull(file);
        assertTrue(file.exists());
    }

    @Test
    public void testDownloadFileToBytes() {
        byte[] bytes = downloadClient.downloadFileToBytes(progress -> {
            System.out.println("------------------------------------------");
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
            if (progress.isDone()) {
                System.out.println("--------   Download Completed!   --------");
            }
        });
        assertNotNull(bytes);
    }


    @Test
    public void testDownloadImageFile() {
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        File file = downloadClient.downloadImageFile(dir);
        assertNotNull(file);
        assertTrue(file.exists());
        assertEquals("test-xxx.jpg", file.getName());
    }


}
