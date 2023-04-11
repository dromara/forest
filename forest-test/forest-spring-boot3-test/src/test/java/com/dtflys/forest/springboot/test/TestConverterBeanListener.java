package com.dtflys.forest.springboot.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.springboot.test.client2.GiteeClient;
import com.dtflys.forest.springboot.test.converter.ConverterTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.annotation.Resource;

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
