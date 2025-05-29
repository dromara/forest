package com.dtflys.forest.test.http.client;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.backend.ContentType;

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
