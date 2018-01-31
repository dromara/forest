package org.forest.http.client;

import org.forest.annotation.Request;
import org.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 18:25
 */
public interface OptionsClient {

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            type = "options",
            headers = {"Accept:text/plan"}
    )
    ForestResponse simpleOptions();

}
