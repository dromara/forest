package com.dtflys.forest.backend.url;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.StringUtils;

import java.util.Map;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:10
 */
public class SimpleURLBuilder extends URLBuilder {

    @Override
    public String buildUrl(ForestRequest request) {
        String url = request.getUrl();
        String queryString = request.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            url += "?" + queryString;
        }
        return url;
    }

}
