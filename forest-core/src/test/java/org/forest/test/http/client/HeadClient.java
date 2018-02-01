package org.forest.test.http.client;

import org.forest.annotation.Request;
import org.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:50
 */
public interface HeadClient {

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            type = "head",
            headers = {"Accept:text/plan"}
    )
    void simpleHead();


    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            type = "head",
            headers = {"Accept:text/plan"}
    )
    ForestResponse responseHead();

}
