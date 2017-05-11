package org.forest.http;

import org.forest.annotation.Request;

import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:24
 */
public interface GetClient {

    @Request(
            url = "http://localhost:5000/hello/foo",
            headers = {"Accept:text/plan"}
    )
    String simpleGet();


    @Request(
            url = "http://localhost:5000/hello/foo",
            dataType = "json",
            headers = {"Accept:text/plan"}
    )
    Map jsonMapGet();

}
