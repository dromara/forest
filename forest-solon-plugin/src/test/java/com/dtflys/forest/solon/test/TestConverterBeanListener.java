package com.dtflys.forest.solon.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.solon.test.client2.GiteeClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import static org.junit.Assert.assertTrue;

/**
 * @author caihongming
 * @since 2021-03-30
 **/
@SolonTest(env = "test1")
@RunWith(SolonJUnit4ClassRunner.class)
public class TestConverterBeanListener {

    @Inject
    private ForestConfiguration forestConfiguration;

    @Inject
    private GiteeClient giteeClient;


    @Test
    public void test1() {
        ForestConverter forestConverter = forestConfiguration.getJsonConverter();
        assertTrue(forestConverter instanceof ForestJacksonConverter);
        ForestRequest<String> request = giteeClient.index2();
        System.out.println(request.execute());
    }

}
