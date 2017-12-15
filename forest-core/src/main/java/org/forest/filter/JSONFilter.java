package org.forest.filter;

import org.forest.config.ForestConfiguration;
import org.forest.converter.json.ForestJsonConverter;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-12-15 15:26
 */
public class JsonFilter implements Filter {
    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
        String json = jsonConverter.convertToJson(data);
        return json;
    }
}
