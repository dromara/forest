package com.dtflys.forest.springboot.test.async;

import com.dtflys.forest.annotation.Post;

public interface AsyncClient {

    @Post("http://localhost:{port}/")
    String postData();

}
