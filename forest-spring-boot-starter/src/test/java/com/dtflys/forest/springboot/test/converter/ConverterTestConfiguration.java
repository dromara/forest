package com.dtflys.forest.springboot.test.converter;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.converter.json.ForestGsonConverter;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
