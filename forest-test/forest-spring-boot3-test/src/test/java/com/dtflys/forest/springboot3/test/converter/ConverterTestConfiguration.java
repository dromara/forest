package com.dtflys.forest.springboot3.test.converter;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caihongming
 * @since 2021-03-30
 **/
@Configuration
public class ConverterTestConfiguration {


    @Bean
    @ConditionalOnMissingClass
    public ForestConverter forestFastjsonConverter() {
        return new ForestFastjsonConverter();
    }


}
