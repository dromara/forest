package com.dtflys.forest.backend.url;


import com.dtflys.forest.http.ForestRequest;

/**
 * URL构造器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:09
 */
public abstract class URLBuilder {


    public abstract String buildUrl(ForestRequest request);


}
