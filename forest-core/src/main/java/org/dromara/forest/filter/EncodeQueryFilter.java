package org.dromara.forest.filter;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.utils.URLEncoder;

import java.nio.charset.StandardCharsets;

public class EncodeQueryFilter implements Filter {

    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        if (data == null) {
            return null;
        }
        return URLEncoder.QUERY_VALUE.encode(String.valueOf(data), StandardCharsets.UTF_8);
    }
}
