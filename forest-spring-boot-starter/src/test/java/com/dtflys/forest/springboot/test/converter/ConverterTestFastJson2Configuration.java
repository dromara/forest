package com.dtflys.forest.springboot.test.converter;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestFastjson2Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caihongming
 * @since 2021-03-30
 **/
@Configuration
public class ConverterTestFastJson2Configuration {


    @Bean
    public ForestConverter forestFastjsonConverter() {
        return new ForestFastjson2Converter();
    }

}
