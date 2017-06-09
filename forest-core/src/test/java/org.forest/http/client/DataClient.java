package org.forest.http.client;

import org.forest.annotation.DataParam;
import org.forest.annotation.DataVariable;
import org.forest.annotation.Request;
import org.forest.callback.OnSuccess;

import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-06 16:11
 */
public interface DataClient {

    @Request(
            url = "http://localhost:3000/hello/data",
            headers = {"Accept:text/plan"},
            dataType = "json"
    )
    Map<String, Object> getData(@DataParam("type") String type);

}
