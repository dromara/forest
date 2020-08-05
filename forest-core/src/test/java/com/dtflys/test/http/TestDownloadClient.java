package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.DownloadClient;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.GetMockServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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
        File file = downloadClient.downloadImage("D:\\TestDownload", "temp-img.png", progress -> {
            System.out.println("------------------------------------------");
            System.out.println("total bytes: " + progress.getTotalBytes());
            System.out.println("current bytes: " + progress.getCurrentBytes());
            System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");
        });
        assertNotNull(file);
        assertEquals("D:\\TestDownload\\temp-img.png", file.getAbsolutePath());
    }

}
