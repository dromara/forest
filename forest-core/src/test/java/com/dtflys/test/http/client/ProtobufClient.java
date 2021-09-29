package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.test.converter.protobuf.ProtobufProto;

/**
 * protobuf客户端测试
 *
 * @author YAKAX
 * @since 2021/03/24 11:22
 **/
@BaseRequest(contentType = "application/x-protobuf")
public interface ProtobufClient {

    @Post(url = "http://localhost:${port}/proto/test")
    ProtobufProto.BaseData protobufTest(@Body ProtobufProto.BaseData baseData);

}
