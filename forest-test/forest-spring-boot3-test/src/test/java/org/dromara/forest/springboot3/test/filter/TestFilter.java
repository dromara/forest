package org.dromara.forest.springboot3.test.filter;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.filter.Filter;

public class TestFilter implements Filter {
    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        return "[[" +data + "]]";
    }
}
