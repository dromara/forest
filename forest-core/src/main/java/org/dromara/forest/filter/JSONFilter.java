package org.dromara.forest.filter;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.converter.json.ForestJsonConverter;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-12-15 15:26
 */
public class JSONFilter implements Filter {
    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
        String json = jsonConverter.encodeToString(data);
        return json;
    }
}
