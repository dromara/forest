package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.utils.ProgressUtils;
import com.dtflys.test.http.client.DownloadClient;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.GetMockServer;
import com.twitter.finagle.http.path.Path;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-05 22:40
 */
public class TestDownloadClient extends BaseClientTest {

    private static ForestConfiguration configuration;

    private DownloadClient downloadClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
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
        File file = downloadClient.downloadFile(dir, ProgressUtils::printProgressBar);
        assertNotNull(file);
        assertTrue(file.exists());
    }

    @Test
    public void testDownloadFileToBytes() {
        String dir = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "TestDownload";
        byte[] bytes = downloadClient.downloadFileToBytes(dir, progress -> {
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
    public void testPrint() {
        System.out.print("HahahaWoWoWo\b\b\b===");
        System.out.print("\b\b\b++++++");
    }
}
