package com.dtflys.forest.test.http.address;

import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.test.annotation.MyHost;

@MyHost("127.0.0.1")
public interface AddressClient6 {

    @Post("/aaa")
    ForestRequest<String> testLocalHost(@Var("port") int port);

}
