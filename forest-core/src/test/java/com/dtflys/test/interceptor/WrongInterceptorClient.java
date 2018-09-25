package com.dtflys.test.interceptor;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 18:10
 */
public interface WrongInterceptorClient {

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            headers = {"Accept:text/plan"},
            interceptor = Forest.class
    )
    String wrongClass();

}
