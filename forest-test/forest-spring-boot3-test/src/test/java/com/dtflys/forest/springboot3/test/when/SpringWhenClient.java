package com.dtflys.forest.springboot3.test.when;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.annotation.Success;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.callback.DefaultRetryWhen;
import com.dtflys.forest.callback.DefaultSuccessWhen;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;

public interface SpringWhenClient {

    @Get("http://localhost:${port}/")
    @Retry(condition = DefaultRetryWhen.class)
    String testSuccessWhen(@Var("port") int port, OnError onError);

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "3")
    @Success(condition = DefaultSuccessWhen.class)
    String testRetryWhen(@Var("port") int port, OnSuccess onSuccess);

}
