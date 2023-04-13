package org.dromara.forest.filter;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.utils.URLEncoder;

import java.nio.charset.StandardCharsets;

public class EncodeFormFilter implements Filter {

    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        if (data == null) {
            return null;
        }
        return URLEncoder.FORM_VALUE.encode(String.valueOf(data), StandardCharsets.UTF_8);
    }
}
