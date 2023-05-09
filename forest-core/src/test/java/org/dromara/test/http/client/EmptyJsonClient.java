package org.dromara.test.http.client;

import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.JSONBody;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.backend.ContentType;

import java.util.Map;

public interface EmptyJsonClient {

    @Post("http://localhost:${port}/empty/map")
    String postEmptyJsonMap(@JSONBody Map<String, Object> user);

    @Post("http://localhost:${port}/empty/map")
    String postEmptyJson2Map(@JSONBody Map<String, Object> user, @Body Map<String, Object> params);

    @Post(
            url = "http://localhost:${port}/empty/map",
            data = "${json(user)}",
            contentType = ContentType.APPLICATION_JSON
    )
    String postEmptyJsonString(@Var("user") Map<String, Object> user);

    @Post(
            url = "http://localhost:${port}/empty/map/{0}",
            data = "${json(user)}",
            contentType = ContentType.APPLICATION_JSON
    )
    String postEmptyJsonStringWithParams(String params, @Var("user") Map<String, Object> user);

}
