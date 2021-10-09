package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.ProtobufBody;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.test.converter.protobuf.ProtobufProto;

/**
 * protobuf客户端测试
 *
 * @author YAKAX
 * @since 2021/03/24 11:22
 **/
@BaseRequest(contentType = "application/x-protobuf")
public interface ProtobufClient {

    @Post("http://localhost:${port}/proto/test")
    ProtobufProto.BaseData protobufTest(@Body ProtobufProto.BaseData baseData);

    @Post(
            url = "http://localhost:${port}/proto/test2",
            contentType = ContentType.APPLICATION_OCTET_STREAM)
    ProtobufProto.BaseData protobufTest2(@ProtobufBody ProtobufProto.BaseData baseData);

}
