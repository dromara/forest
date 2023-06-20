package com.dtflys.forest.filter;

import com.dtflys.forest.config.ForestConfiguration;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-07-12 16:30
 */
public class FilterChain implements Filter {

    private LinkedList<Filter> filters = new LinkedList<>();

    @Override
    public Object doFilter(ForestConfiguration configuration, Object data) {
        final Iterator<Filter> iter = filters.iterator();
        Object result = data;
        for ( ; iter.hasNext(); ) {
            final Filter filter = iter.next();
            result = filter.doFilter(configuration, result);
        }
        return result;
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public boolean isEmpty() {
        return filters.isEmpty();
    }
}
