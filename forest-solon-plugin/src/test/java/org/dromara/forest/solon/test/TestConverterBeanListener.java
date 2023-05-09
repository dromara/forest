package org.dromara.forest.solon.test;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.converter.json.ForestJacksonConverter;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.solon.test.client2.GiteeClient;
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
