package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;

import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-06 16:11
 */
public interface DataClient {

    @Request(
            url = "http://localhost:${port}/hello/data",
            headers = {"Accept:text/plain"},
            dataType = "json"
    )
    Map<String, Object> getData(@Query("type") String type);

}
