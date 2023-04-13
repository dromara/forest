package org.dromara.forest.solon.test.async;

import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Post;

import java.util.concurrent.Future;

@ForestClient
public interface AsyncClient {

    @Post(url = "http://localhost:{port}/", async = true)
    Future<String> postFuture();

}
