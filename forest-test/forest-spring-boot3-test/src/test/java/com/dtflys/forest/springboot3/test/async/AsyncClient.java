package com.dtflys.forest.springboot3.test.async;

import com.dtflys.forest.annotation.Post;

import java.util.concurrent.Future;

public interface AsyncClient {

    @Post(url = "http://localhost:{port}/", async = true)
    Future<String> postFuture();

}
