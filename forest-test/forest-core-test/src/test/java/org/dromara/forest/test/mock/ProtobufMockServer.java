package org.dromara.forest.test.mock;

import org.apache.http.HttpHeaders;
import org.dromara.forest.backend.ContentType;
import org.dromara.forest.converter.protobuf.ForestGoogleProtobufConverter;
import org.dromara.forest.test.converter.protobuf.ProtobufProto;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class ProtobufMockServer extends MockServerRule {


    public ProtobufMockServer(Object target) {
        super(target);
    }

    public void initServer() {

        MockServerClient server = new MockServerClient("localhost", getPort());

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

        server.when(
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

        server.when(
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
