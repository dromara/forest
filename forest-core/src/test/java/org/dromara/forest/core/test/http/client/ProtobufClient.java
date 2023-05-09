package org.dromara.forest.core.test.http.client;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.ProtobufBody;
import org.dromara.forest.backend.ContentType;
import org.dromara.forest.core.test.converter.protobuf.ProtobufProto;

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
