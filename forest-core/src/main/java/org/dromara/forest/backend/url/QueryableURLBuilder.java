package org.dromara.forest.backend.url;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.utils.StringUtils;
import org.dromara.forest.utils.URLUtils;

/**
 * 带查询参数的URL构造器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:11
 */
public class QueryableURLBuilder extends URLBuilder {

    @Override
    public String buildUrl(ForestRequest request) {
        final String url = request.getUrl();
        final StringBuilder urlBuilder = new StringBuilder(url);
        final String query = request.queryString();
        if (StringUtils.isNotEmpty(query)) {
            urlBuilder.append("?").append(query);
        }
        final String ref = request.getRef();
        if (StringUtils.isNotEmpty(ref)) {
            urlBuilder.append("#").append(URLUtils.refEncode(ref, "UTF-8"));
        }
        return urlBuilder.toString();
    }

}
