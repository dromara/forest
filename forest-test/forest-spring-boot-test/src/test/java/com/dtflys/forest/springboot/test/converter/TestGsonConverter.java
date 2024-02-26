package com.dtflys.forest.springboot.test.converter;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestGsonConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.springboot.test.BaseSpringBootTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-24 22:38
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("gson")
@SpringBootTest(classes = TestGsonConverter.class)
@EnableAutoConfiguration
public class TestGsonConverter extends BaseSpringBootTest {

    @Resource
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfig() {
        ForestJsonConverter jsonConverter = forestConfiguration.getJsonConverter();
        assertThat(jsonConverter).isNotNull().isInstanceOf(ForestGsonConverter.class);
        ForestGsonConverter forestGsonConverter = (ForestGsonConverter) jsonConverter;
        assertThat(forestGsonConverter.getDateFormat()).isEqualTo("yyyy/MM/dd hh:mm:ss");
    }

}
