package org.dromara.forest.springboot.test.converter;

import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.converter.json.ForestFastjsonConverter;
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