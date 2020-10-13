package com.dtflys.forest.http;

import java.util.LinkedList;
import java.util.List;

/**
 * Forest请求Query参数Map
 * <p>该类负责批量管理在Forest请求中所有的请求Query参数</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-11 12:45
 */
public class ForestQueryMap {

    private final List<ForestQuery> queries;

    public ForestQueryMap() {
        this.queries = new LinkedList<>();
    }


}
