package com.dtflys.forest.test.http.address;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;

@Address(host = "127.0.0.1", port = "8888")
public interface AddressClient4 {

    @Post("http://localhost:{port}/")
    String test();

}
