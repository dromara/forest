package com.dtflys.forest.http;

import cn.hutool.core.collection.CollectionUtil;
import com.dtflys.forest.utils.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ForestQueryMapWrapper extends ForestQueryMap {

    public ForestQueryMapWrapper(ForestRequest request) {
        super(request);
    }

    @Override
    public SimpleQueryParameter getQuery(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        if (request != null) {
            final ForestURL url = request.url();
            if (url != null) {
                for (final SimpleQueryParameter query : url.getQuery().queries) {
                    if (query.getName().equalsIgnoreCase(name)) {
                        return query;
                    }
                }
            }
        }
        for (SimpleQueryParameter query : queries) {
            if (query.getName().equalsIgnoreCase(name)) {
                return query;
            }
        }
        return null;
    }

    @Override
    public List<SimpleQueryParameter> getQueries(String name) {
        final List<SimpleQueryParameter> list = new LinkedList<>();
        if (StringUtils.isEmpty(name)) {
            return list;
        }
        if (request != null) {
            final ForestURL url = request.url();
            if (url != null) {
                for (final SimpleQueryParameter query : url.query.queries) {
                    if (query.getName().equalsIgnoreCase(name)) {
                        list.add(query);
                    }
                }
            }
        }
        for (final SimpleQueryParameter query : queries) {
            if (query.getName().equalsIgnoreCase(name)) {
                list.add(query);
            }
        }
        return list;
    }

    @Override
    protected Iterator<SimpleQueryParameter> allQueryIterator() {
        Iterator<SimpleQueryParameter> iterator1 = null;
        if (request != null) {
            final ForestURL url = request.url();
            if (url != null) {
                iterator1 = url.getQuery().queries.iterator();
            }
        }

        final Iterator<SimpleQueryParameter> iterator2 = queries.iterator();

        Iterator<SimpleQueryParameter> urlQueriesIterator = iterator1;
        return new Iterator<SimpleQueryParameter>() {

            private boolean urlQueriesIteratorHasNext = true;

            @Override
            public boolean hasNext() {
                if (urlQueriesIterator != null) {
                    if (!urlQueriesIterator.hasNext()) {
                        urlQueriesIteratorHasNext = false;
                        return iterator2.hasNext();
                    }
                    return true;
                }
                return iterator2.hasNext();
            }

            @Override
            public SimpleQueryParameter next() {
                if (urlQueriesIterator != null && urlQueriesIteratorHasNext) {
                    return urlQueriesIterator.next();
                }
                return iterator2.next();
            }
        };
    }

    @Override
    public String toQueryString() {
        return super.toQueryString();
    }
}
