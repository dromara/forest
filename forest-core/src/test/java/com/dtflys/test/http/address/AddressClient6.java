package com.dtflys.test.http.address;

import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.test.annotation.MyHost;

@MyHost("127.0.0.1")
public interface AddressClient6 {

    @Post("/aaa")
    ForestRequest<String> testLocalHost();

}
