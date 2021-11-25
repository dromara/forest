package com.dtflys.forest.springboot.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.converter.json.ForestGsonConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.springboot.test.client0.DisturbInterface;
import com.dtflys.forest.springboot.test.client1.BaiduClient;
import com.dtflys.forest.springboot.test.client2.GiteeClient;
import com.dtflys.forest.springboot.test.converter.ConverterTestConfiguration;
import com.dtflys.forest.utils.ForestDataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
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
        ForestConverter forestConverter = forestConfiguration.getConverterMap().get(ForestDataType.JSON);
        assertTrue(forestConverter instanceof ForestFastjsonConverter);
        ForestRequest<String> request = giteeClient.index2();
        System.out.println(request.execute());
    }

}
