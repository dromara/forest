package com.dtflys.forest.example.controller;

import com.dtflys.forest.example.client.TestInterceptorClient;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class InterceptorController {

    private static Logger logger = LoggerFactory.getLogger(InterceptorController.class);

    @Inject
    private TestInterceptorClient testInterceptorClient;

    @Post
    @Mapping("/receive-interceptor")
    public String receiveInterceptor(Context ctx, String username) {
        String token = ctx.header("accessToken");
        logger.info("accessToken: {}", token);
        return "ok";
    }

    @Get
    @Mapping("/test-interceptor")
    public String testInterceptor(String username) {
        String result = testInterceptorClient.testInterceptor(username);
        return result;
    }
}
