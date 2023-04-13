package org.dromara.forest.springboot3.test.async;

import org.dromara.forest.annotation.Post;

import java.util.concurrent.Future;

public interface AsyncClient {

    @Post(url = "http://localhost:{port}/", async = true)
    Future<String> postFuture();

}
