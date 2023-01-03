package com.dtflys.forest.solon.test.converter;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestGsonConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-24 22:38
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(env = "gson")
public class TestGsonConverter {

    @Inject
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfig() {
        ForestJsonConverter jsonConverter = forestConfiguration.getJsonConverter();
        assertThat(jsonConverter).isNotNull().isInstanceOf(ForestGsonConverter.class);
        ForestGsonConverter forestGsonConverter = (ForestGsonConverter) jsonConverter;
        assertThat(forestGsonConverter.getDateFormat()).isEqualTo("yyyy/MM/dd hh:mm:ss");
    }

}
