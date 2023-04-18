package org.dromara.forest.backend.url;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.utils.StringUtil;
import org.dromara.forest.utils.URLUtil;

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
        if (StringUtil.isNotEmpty(query)) {
            urlBuilder.append("?").append(query);
        }
        String ref = request.getRef();
        if (StringUtil.isNotEmpty(ref)) {
            urlBuilder.append("#").append(URLUtil.refEncode(ref, "UTF-8"));
        }
        return urlBuilder.toString();
    }

}
