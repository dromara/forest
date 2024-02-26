package com.dtflys.forest.springboot3.test.converter;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.springboot3.test.BaseSpringBootTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import jakarta.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-24 22:38
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("fastjson")
@SpringBootTest
@ContextConfiguration(classes = TestFastjsonConverter.class)
@EnableAutoConfiguration
public class TestFastjsonConverter extends BaseSpringBootTest {

    @Resource
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfig() {
        ForestJsonConverter jsonConverter = forestConfiguration.getJsonConverter();
        assertThat(jsonConverter).isNotNull().isInstanceOf(ForestFastjsonConverter.class);
        ForestFastjsonConverter forestFastjsonConverter = (ForestFastjsonConverter) jsonConverter;
        assertThat(forestFastjsonConverter.getDateFormat()).isEqualTo("yyyy/MM/dd hh:mm:ss");
    }

}
