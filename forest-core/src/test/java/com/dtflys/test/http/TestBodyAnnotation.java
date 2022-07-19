package com.dtflys.test.http;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.httpclient.HttpclientBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.mock.MockServerRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author tanglingyan[xiao4852@qq.com]
 * @since 2022-06-04 10:57
 */
public class TestBodyAnnotation extends BaseClientTest {

    private static ForestConfiguration configuration;
    @Rule
    public final MockWebServer server = new MockWebServer();
    private final TestBodyAnnotationClient testBodyAnnotationClient;

    public TestBodyAnnotation(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        testBodyAnnotationClient = configuration.createInstance(TestBodyAnnotationClient.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public static MockServerRequest mockRequest(MockWebServer server) {
        try {
            RecordedRequest request = server.takeRequest();
            return new MockServerRequest(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterRequests() {
    }

    /**
     * 测试被@Body标识对象的属性是否都传递到服务端
     */
    @Test
    public void testAnalysisBodyParameter() {
        TestHttpEntity testHttpEntity = new TestHttpEntity();
        //long
        testHttpEntity.setTempObjLong(Long.MAX_VALUE);
        testHttpEntity.setTempLong(Long.MAX_VALUE);
        //char
        testHttpEntity.setTempChar('1');
        testHttpEntity.setTempObjCharacter(new Character('1'));
        //short
        testHttpEntity.setTempObjShort(Short.MAX_VALUE);
        testHttpEntity.setTempShort(Short.MAX_VALUE);
        //byte
        testHttpEntity.setTempObjByte(Byte.MAX_VALUE);
        testHttpEntity.setTempByte(Byte.MAX_VALUE);
        //double
        testHttpEntity.setTempDouble(Double.MAX_VALUE);
        testHttpEntity.setTempObjDouble(Double.MAX_VALUE);
        //String
        testHttpEntity.setTempObjString("test");

        server.enqueue(new MockResponse());
        //发送http请求
        testBodyAnnotationClient.testAnalysisBodyParameter(testHttpEntity);
        //检查参数是否缺少
        String body = mockRequest(server).bodyAsString();
        Field[] declaredFields = testHttpEntity.getClass().getDeclaredFields();
        boolean flag = true;
        System.out.println("body ---> " + body);
        for (int i = 0; i < declaredFields.length; i++) {
            Field declaredField = declaredFields[i];
            System.out.println("-------> " + declaredField.getName());
            String fieldName = declaredField.getName();
            if (!fieldName.startsWith("__") && body.indexOf(declaredField.getName()) == -1) {
                flag = false;
                break;
            }
        }
        Assert.assertTrue(flag);
    }


    /**
     * http接口
     *
     * @author tanglingyan[xiao4852@qq.com]
     * @since 2022-06-04 10:57
     */
    @BaseRequest(baseURL = "http://localhost:{port}", contentType = "application/json")
    public interface TestBodyAnnotationClient {
        @Post(
                url = "http://localhost:{port}/testAnalysisBodyParameter",
                contentType = "application/x-www-form-urlencoded", connectTimeout = 30000
        )
        void testAnalysisBodyParameter(@Body TestHttpEntity testHttpEntity);
    }

    /**
     * http交互对象
     *
     * @author tanglingyan[xiao4852@qq.com]
     * @since 2022-06-04 10:57
     */
    public static class TestHttpEntity {
        private String tempObjString;
        private Character tempObjCharacter;
        private char tempChar;
        private Double tempObjDouble;
        private double tempDouble;
        private Byte tempObjByte;
        private byte tempByte;
        private Short tempObjShort;
        private short tempShort;
        private Long tempObjLong;
        private long tempLong;

        public String getTempObjString() {
            return tempObjString;
        }

        public void setTempObjString(String tempObjString) {
            this.tempObjString = tempObjString;
        }

        public Character getTempObjCharacter() {
            return tempObjCharacter;
        }

        public void setTempObjCharacter(Character tempObjCharacter) {
            this.tempObjCharacter = tempObjCharacter;
        }

        public char getTempChar() {
            return tempChar;
        }

        public void setTempChar(char tempChar) {
            this.tempChar = tempChar;
        }

        public Double getTempObjDouble() {
            return tempObjDouble;
        }

        public void setTempObjDouble(Double tempObjDouble) {
            this.tempObjDouble = tempObjDouble;
        }

        public double getTempDouble() {
            return tempDouble;
        }

        public void setTempDouble(double tempDouble) {
            this.tempDouble = tempDouble;
        }

        public Byte getTempObjByte() {
            return tempObjByte;
        }

        public void setTempObjByte(Byte tempObjByte) {
            this.tempObjByte = tempObjByte;
        }

        public byte getTempByte() {
            return tempByte;
        }

        public void setTempByte(byte tempByte) {
            this.tempByte = tempByte;
        }

        public Short getTempObjShort() {
            return tempObjShort;
        }

        public void setTempObjShort(Short tempObjShort) {
            this.tempObjShort = tempObjShort;
        }

        public short getTempShort() {
            return tempShort;
        }

        public void setTempShort(short tempShort) {
            this.tempShort = tempShort;
        }

        public Long getTempObjLong() {
            return tempObjLong;
        }

        public void setTempObjLong(Long tempObjLong) {
            this.tempObjLong = tempObjLong;
        }

        public long getTempLong() {
            return tempLong;
        }

        public void setTempLong(long tempLong) {
            this.tempLong = tempLong;
        }

        @Override
        public String toString() {
            return "TestHttpEntity{" +
                    "tempObjString='" + tempObjString + '\'' +
                    ", tempObjCharacter=" + tempObjCharacter +
                    ", tempChar=" + tempChar +
                    ", tempObjDouble=" + tempObjDouble +
                    ", tempDouble=" + tempDouble +
                    ", tempObjByte=" + tempObjByte +
                    ", tempByte=" + tempByte +
                    ", tempObjShort=" + tempObjShort +
                    ", tempShort=" + tempShort +
                    ", tempObjLong=" + tempObjLong +
                    ", tempLong=" + tempLong +
                    '}';
        }
    }
}
