package com.dtflys.forest.filter;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestJsonConverter;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-12-15 15:26
 */
public class JSONFilter implements Filter {
    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
        String json = jsonConverter.convertToJson(data);
        return json;
    }
}
