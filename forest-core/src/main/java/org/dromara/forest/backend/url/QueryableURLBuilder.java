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
        String url = request.getUrl();
        StringBuilder urlBuilder = new StringBuilder(url);
        String query = request.queryString();
        if (StringUtils.isNotEmpty(query)) {
            urlBuilder.append("?").append(query);
        }
        String ref = request.getRef();
        if (StringUtils.isNotEmpty(ref)) {
            urlBuilder.append("#").append(URLUtils.refEncode(ref, "UTF-8"));
        }
        return urlBuilder.toString();
    }

}
