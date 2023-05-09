package org.dromara.forest.backend.url;


import org.dromara.forest.http.ForestRequest;

/**
 * URL构造器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:09
 */
public abstract class URLBuilder {


    public abstract String buildUrl(ForestRequest request);


}
