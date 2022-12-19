package com.dtflys.forest.solon.test.when;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.DefaultRetryWhen;
import com.dtflys.forest.callback.DefaultSuccessWhen;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;

@ForestClient
public interface SpringWhenClient {

    @Get("http://localhost:${port}/")
    @Retry(condition = DefaultRetryWhen.class)
    String testSuccessWhen(@Var("port") int port, OnError onError);

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "3")
    @Success(condition = DefaultSuccessWhen.class)
    String testRetryWhen(@Var("port") int port, OnSuccess onSuccess);

}
