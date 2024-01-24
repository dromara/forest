package com.dtflys.test.converter.protobuf;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.test.http.client.ProtobufClient;
import com.dtflys.test.mock.ProtobufMockServer;
import com.google.common.io.BaseEncoding;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * protobuf 测试
 *
 * @author YAKAX
 * @since 2020/12/18 21:11
 **/
public class TestForestProtobufConverter extends BaseClientTest {

    @Rule
    public ProtobufMockServer server = new ProtobufMockServer(this);


    private static ForestConfiguration configuration;

    private static ProtobufClient protobufClient;

    public TestForestProtobufConverter(HttpBackend backend) {
        super(backend, configuration);
        configuration.variable("port", server.getPort());

    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        protobufClient = configuration.createInstance(ProtobufClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    private ProtobufProto.BaseData getBaseDataProtoBuilder() {
        return ProtobufProto.BaseData.newBuilder()
                .setDoubleVal(100.123D)
                .setFloatVal(12.3F)
                .setInt32Val(32)
                .setInt64Val(64)
                .setUint32Val(132)
                .setUint64Val(164)
                .setSint32Val(232)
                .setSint64Val(264)
                .setFixed32Val(332)
                .setFixed64Val(364)
                .setSfixed32Val(432)
                .setSfixed64Val(464)
                .setBoolVal(true)
                .setStringVal("string-test")
//                .setBytesVal(ByteString.copyFromUtf8("itsbytes"))
                .setEnumVal(ProtobufProto.Color.BLUE)
                .addReStrVal("re-item-0")
                .putMapVal("m-key", ProtobufProto.BaseData.newBuilder()
                        .setStringVal("base-data 嵌套")
                        .build())
                .build();

    }

    public Protobuf getBaseDataPojo() {
        Map<String, Protobuf> map = new HashMap<>();
        Protobuf protobuf = new Protobuf();
        protobuf.setStringVal("base-data 嵌套");
        map.put("m-key", protobuf);

        Protobuf object = new Protobuf();
        object.setDoubleVal(100.123D);
        object.setFloatVal(12.3F);
        object.setInt32Val(32);
        object.setInt64Val(64);
        object.setUint32Val(132);
        object.setUint64Val(164);
        object.setSint32Val(232);
        object.setSint64Val(264);
        object.setFixed32Val(332);
        object.setFixed64Val(364);
        object.setSfixed32Val(432);
        object.setSfixed64Val(464);
        object.setBoolVal(true);
        object.setStringVal("string-test");
        object.setBytesVal("itsbytes");
        object.setEnumVal(ProtobufProto.Color.BLUE.toString());
        object.setReStrVal(Collections.singletonList("re-item-0"));
        object.setMapVal(map);
        return object;
    }

    /**
     *     @PostRequest(value = "/", decoder = ForestProtobufConverter.class)
     *     Protobuf post(@Body Message build);
     */
    @Test
    public void convertToProto() {
//        ForestProtobufConverter forestProtobufConverter = new ForestProtobufConverter();
//        Protobuf protobuf = getBaseDataPojo();
//        Message message = forestProtobufConverter.convertToProto(builder, protobuf);
//        byte[] bytes = message.toByteArray();
//        Object o = forestProtobufConverter.convertToJavaObject(bytes, Protobuf.class);
        //        assertEqualsVerify(protobuf, baseDataProto);
        ProtobufProto.BaseData.Builder builder = ProtobufProto.BaseData.newBuilder();
        builder.setInt32Val(1);
        Message build = builder.build();

        byte[] bytes1 = build.toByteArray();
        Parser<ProtobufProto.BaseData> parser = ProtobufProto.BaseData.parser();
    }

    private void assertEqualsVerify(Protobuf protobuf, ProtobufProto.BaseData baseData) {
        assertEquals((protobuf == null), (!baseData.isInitialized()));
        if (protobuf == null) {
            return;
        }
        assertEquals(protobuf.getDoubleVal(), baseData.getDoubleVal(), 0.0000001D);
        assertEquals(protobuf.getFloatVal(), baseData.getFloatVal(), 0.00000001D);
        assertEquals(protobuf.getInt32Val(), baseData.getInt32Val());
        assertEquals(protobuf.getInt64Val(), baseData.getInt64Val());
        assertEquals(protobuf.getUint32Val(), baseData.getUint32Val());
        assertEquals(protobuf.getUint64Val(), baseData.getUint64Val());
        assertEquals(protobuf.getSint32Val(), baseData.getSint32Val());
        assertEquals(protobuf.getSint64Val(), baseData.getSint64Val());
        assertEquals(protobuf.getFixed32Val(), baseData.getFixed32Val());
        assertEquals(protobuf.getInt64Val(), baseData.getInt64Val());
        assertEquals(protobuf.isBoolVal(), baseData.getBoolVal());
        assertEquals(protobuf.isBoolVal(), baseData.getBoolVal());
        if (protobuf.getStringVal() == null) {
            assertTrue(baseData.getStringVal().isEmpty());
        } else {
            assertEquals(protobuf.getStringVal(), baseData.getStringVal());
        }

        // ByteString 转 base64 Strings
        if (protobuf.getBytesVal() == null) {
            // 默认值为 ""
            assertTrue(baseData.getBytesVal().isEmpty());
        } else {
            assertEquals(protobuf.getBytesVal(), BaseEncoding.base64().encode(baseData.getBytesVal().toByteArray()));
        }
        // Enum 转 String
        if (protobuf.getEnumVal() == null) {
            // 默认值为 0
            assertEquals(ProtobufProto.Color.forNumber(0), baseData.getEnumVal());
        } else {
            assertEquals(protobuf.getEnumVal(), baseData.getEnumVal().toString());
        }
        if (protobuf.getReStrVal() == null) {
            // 默认为空列表
            assertEquals(0, baseData.getReStrValList().size());
        } else {
            assertEquals(protobuf.getReStrVal().size(), baseData.getReStrValList().size());
            for (int i = 0; i < protobuf.getReStrVal().size(); i++) {
                assertEquals(protobuf.getReStrVal().get(i), baseData.getReStrValList().get(i));
            }
        }

        if (protobuf.getMapVal() == null) {
            // 默认为空集合
            assertEquals(0, baseData.getMapValMap().size());
        } else {
            assertEquals(protobuf.getMapVal().size(), baseData.getMapValMap().size());
            for (Map.Entry<String, ProtobufProto.BaseData> entry : baseData.getMapValMap().entrySet()) {
                assertEqualsVerify(protobuf.getMapVal().get(entry.getKey()), entry.getValue());
            }
        }
    }


    @Test
    public void protobufHttpTest() {
        ProtobufProto.BaseData.Builder builder = ProtobufProto.BaseData.newBuilder();
        builder.setInt32Val(1);
        ProtobufProto.BaseData reqData = builder.build();
        ProtobufProto.BaseData resData = protobufClient.protobufTest(reqData);
        assertNotNull(resData);
        assertEquals("中文字符串", resData.getStringVal());
        assertEquals(Double.valueOf(3.2), Double.valueOf(resData.getDoubleVal()));
    }

    @Test
    public void protobufHttpTest2() {
        ProtobufProto.BaseData.Builder builder = ProtobufProto.BaseData.newBuilder();
        builder.setInt32Val(1);
        ProtobufProto.BaseData reqData = builder.build();
        ProtobufProto.BaseData resData = protobufClient.protobufTest2(reqData);
        assertNotNull(resData);
        assertEquals("中文字符串", resData.getStringVal());
        assertEquals(Double.valueOf(3.2), Double.valueOf(resData.getDoubleVal()));
    }

}
