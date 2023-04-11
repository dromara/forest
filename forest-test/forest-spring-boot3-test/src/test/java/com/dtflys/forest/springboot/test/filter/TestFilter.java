package com.dtflys.forest.springboot.test.filter;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.filter.Filter;

public class TestFilter implements Filter {
    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        return "[[" +data + "]]";
    }
}
