package com.dtflys.test.mock;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.converter.protobuf.ForestGoogleProtobufConverter;
import com.dtflys.test.converter.protobuf.ProtobufProto;
import org.apache.http.HttpHeaders;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ProtobufMockServer extends MockServerRule {

    public final static Integer port = 5088;


    public ProtobufMockServer(Object target) {
        super(target, port);
    }

    public void initServer() {

        MockServerClient mockClient = new MockServerClient("localhost", port);

        ForestGoogleProtobufConverter protobufConverter = new ForestGoogleProtobufConverter();

        ProtobufProto.BaseData.Builder reqBuilder = ProtobufProto.BaseData.newBuilder();
        reqBuilder.setInt32Val(1);
        ProtobufProto.BaseData reqData = reqBuilder.build();
        byte[] reqByteArray = protobufConverter.convertToByte(reqData);

        ProtobufProto.BaseData.Builder resBuilder = ProtobufProto.BaseData.newBuilder();
        resBuilder.setStringVal("中文字符串");
        resBuilder.setDoubleVal(3.2);
        resBuilder.setInt32Val(1);
        ProtobufProto.BaseData resData = resBuilder.build();
        byte[] resByteArray = protobufConverter.convertToByte(resData);

        mockClient.when(
                request()
                        .withPath("/proto/test")
                        .withMethod("POST")
                        .withHeader(new Header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_X_PROTOBUF))
                        .withBody(reqByteArray)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(resByteArray)
        );

        mockClient.when(
                request()
                        .withPath("/proto/test2")
                        .withMethod("POST")
                        .withHeader(new Header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_OCTET_STREAM))
                        .withBody(reqByteArray)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(resByteArray)
        );

    }

}
