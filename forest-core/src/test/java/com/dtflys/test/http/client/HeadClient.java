package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:50
 */
public interface HeadClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            type = "head",
            headers = {
                "Accept:text/plan",
                "accessToken:11111111"
            }
    )
    void simpleHead();


    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            type = "head",
            headers = {
                "Accept:text/plan",
                "accessToken:11111111"
            }
    )
    ForestResponse responseHead();

}
