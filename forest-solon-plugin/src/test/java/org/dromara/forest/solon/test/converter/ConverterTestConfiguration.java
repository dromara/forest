package org.dromara.forest.solon.test.converter;

import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.converter.json.ForestFastjsonConverter;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author caihongming
 * @since 2021-03-30
 **/
@Configuration
public class ConverterTestConfiguration {


    @Bean
    public ForestConverter forestFastjsonConverter() {
        return new ForestFastjsonConverter();
    }


}
