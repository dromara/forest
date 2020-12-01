package com.dtflys.forest.backend.url;


import com.dtflys.forest.http.ForestRequest;

/**
 * URL构造器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:09
 */
public abstract class URLBuilder {

    private final static URLBuilder SIMPLE_URL_BUILDER = new SimpleURLBuilder();

    private final static URLBuilder QUERYABLE_URL_BUILDER = new QueryableURLBuilder();

    public abstract String buildUrl(ForestRequest request);

    public static URLBuilder getSimpleURLBuilder() {
        return SIMPLE_URL_BUILDER;
    }

    public static URLBuilder getQueryableURLBuilder() {
        return QUERYABLE_URL_BUILDER;
    }

}
