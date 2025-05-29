package com.dtflys.forest.test.http.client;

import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnSuccess;

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
    Map<String, Object> getData(@DataParam("type") String type);

}
