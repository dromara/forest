/*
 * Copyright (C) 2011-2021 ShenZhen iBOXCHAIN Information Technology Co.,Ltd.
 *
 * All right reserved.
 *
 * This software is the confidential and proprietary
 * information of iBOXCHAIN Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with iBOXCHAIN inc.
 *
 */
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
