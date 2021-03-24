package com.dtflys.test.http;

import cn.hutool.core.io.FileUtil;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.httpclient.HttpclientBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.BinaryClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertNotNull;

public class BinaryClientTest extends BaseClientTest {

    private static ForestConfiguration configuration;

    private static BinaryClient binaryClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
//        configuration.setVariableValue("port", BinaryClient.port);
    }


    public BinaryClientTest(HttpBackend backend) {
        super(backend, configuration);
        binaryClient = configuration.createInstance(BinaryClient.class);
    }

    private static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }

    @Test
    public void testUploadByteArray() {
        String path = this.getClass().getResource("/test-img.jpg").getPath();
        if (path.startsWith("/") && isWindows()) {
            path = path.substring(1);
        }
        File file = new File(path);
        byte[] byteArray = FileUtil.readBytes(file);
        String result = binaryClient.uploadOctetStreamWithByteArray(byteArray, "test-xxx.jpg");
        assertNotNull(result);
    }

}
