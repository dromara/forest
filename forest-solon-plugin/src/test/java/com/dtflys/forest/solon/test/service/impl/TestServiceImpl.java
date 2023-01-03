package com.dtflys.forest.solon.test.service.impl;

import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.solon.test.client0.BeastshopClient;
import com.dtflys.forest.solon.test.service.TestService;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-03-23 23:04
 */
@Component
public class TestServiceImpl implements TestService {

    @Inject
    private BeastshopClient beastshopClient;

    public TestServiceImpl() {
        System.out.println("创建 TestService");
    }

    @Override
    public ForestResponse<String> shops() {
        return beastshopClient.shops();
    }

    public String testRetry() {
        return beastshopClient.testRetry();
    }
}
