package org.forest.executors.url;

import org.forest.http.ForestRequest;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:10
 */
public class SimpleURLBuilder extends URLBuilder {

    @Override
    public String buildUrl(ForestRequest request) {
        return request.getUrl();
    }

}
