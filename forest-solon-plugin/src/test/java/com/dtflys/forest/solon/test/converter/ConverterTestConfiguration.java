package com.dtflys.forest.solon.test.converter;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
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
