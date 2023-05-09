package org.dromara.forest.core.test.http.client;

import org.dromara.forest.annotation.DataParam;
import org.dromara.forest.annotation.Request;

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
