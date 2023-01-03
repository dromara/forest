package com.dtflys.forest.solon.test.async;

import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.annotation.Post;

import java.util.concurrent.Future;

@ForestClient
public interface AsyncClient {

    @Post(url = "http://localhost:{port}/", async = true)
    Future<String> postFuture();

}
