package com.dtflys.forest.springboot.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestFastjson2Converter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.springboot.test.client2.GiteeClient;
import com.dtflys.forest.springboot.test.converter.ConverterTestFastJson2Configuration;
import com.dtflys.forest.utils.ForestDataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

/**
 * @author caihongming
 * @since 2021-03-30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConverterBeanFastJson2Listener.class)
@ComponentScan(basePackageClasses = ConverterTestFastJson2Configuration.class)
@EnableAutoConfiguration
public class TestConverterBeanFastJson2Listener {

    @Resource
    private ForestConfiguration forestConfiguration;

    @Resource
    private GiteeClient giteeClient;


    @Test
    public void test1() {
        ForestConverter forestConverter = forestConfiguration.getConverterMap().get(ForestDataType.JSON);
        assertTrue(forestConverter instanceof ForestFastjson2Converter);
        ForestRequest<String> request = giteeClient.index2();
        System.out.println(request.execute());
    }

}
