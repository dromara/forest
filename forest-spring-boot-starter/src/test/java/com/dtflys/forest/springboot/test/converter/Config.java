package com.dtflys.forest.springboot.test.converter;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestGsonConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-03-30
 **/
@Configuration
public class Config {

    @Bean
    public ForestConverter forestGsonConverter(){
        return new ForestGsonConverter();
    }
}
