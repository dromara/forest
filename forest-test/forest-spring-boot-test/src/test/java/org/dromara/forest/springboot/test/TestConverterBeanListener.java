package org.dromara.forest.springboot.test;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.converter.json.ForestFastjsonConverter;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.springboot.test.client2.GiteeClient;
import org.dromara.forest.springboot.test.converter.ConverterTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

/**
 * @author caihongming
 * @since 2021-03-30
 **/
@ActiveProfiles("test1")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConverterBeanListener.class)
@ComponentScan(basePackageClasses = ConverterTestConfiguration.class)
@EnableAutoConfiguration
public class TestConverterBeanListener {

    @Resource
    private ForestConfiguration forestConfiguration;

    @Resource
    private GiteeClient giteeClient;


    @Test
    public void test1() {
        ForestConverter forestConverter = forestConfiguration.getJsonConverter();
        assertTrue(forestConverter instanceof ForestFastjsonConverter);
        ForestRequest<String> request = giteeClient.index2();
        System.out.println(request.execute());
    }

}
