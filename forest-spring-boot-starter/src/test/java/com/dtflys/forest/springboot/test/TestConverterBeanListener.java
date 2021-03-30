package com.dtflys.forest.springboot.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestGsonConverter;
import com.dtflys.forest.springboot.test.converter.Config;
import com.dtflys.forest.utils.ForestDataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-03-30
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConverterBeanListener.class)
@ComponentScan(basePackageClasses = Config.class)
@EnableAutoConfiguration(exclude = GsonAutoConfiguration.class)
public class TestConverterBeanListener {

    @Resource
    private ForestConfiguration forestConfiguration;

    @Test
    public void test1() {
        ForestConverter forestConverter = forestConfiguration.getConverterMap().get(ForestDataType.JSON);
        assertTrue(forestConverter instanceof ForestGsonConverter);
    }

}
