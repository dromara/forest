package org.dromara.forest.springboot3.test.converter;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.converter.json.ForestJacksonConverter;
import org.dromara.forest.converter.json.ForestJsonConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-24 22:38
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("jackson")
@SpringBootTest(classes = TestJacksonConverter.class)
@EnableAutoConfiguration
public class TestJacksonConverter {

    @Resource
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfig() {
        ForestJsonConverter jsonConverter = forestConfiguration.getJsonConverter();
        assertThat(jsonConverter).isNotNull().isInstanceOf(ForestJacksonConverter.class);
        ForestJacksonConverter forestJacksonConverter = (ForestJacksonConverter) jsonConverter;
        assertThat(forestJacksonConverter.getDateFormat()).isEqualTo("yyyy/MM/dd hh:mm:ss");
    }

}
