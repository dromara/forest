package com.dtflys.test.http.address;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.http.ForestRequest;

@Address(basePath = "http://localhost:{port}/aaa")
public interface AddressClient5 {

    @Post("/bbb")
    ForestRequest test();

}
