package com.dtflys.forest.springboot3.test;

import jakarta.annotation.Resource;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.springboot3.test.client2.GiteeClient;
import com.dtflys.forest.springboot3.test.converter.ConverterTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * @author caihongming
 * @since 2021-03-30
 **/
@ActiveProfiles("test1")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TestConverterBeanListener.class)
@ComponentScan(basePackageClasses = ConverterTestConfiguration.class)
@EnableAutoConfiguration
public class TestConverterBeanListener extends BaseSpringBootTest {

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
