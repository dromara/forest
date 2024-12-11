package com.dtflys.forest.test.http.address;

import com.dtflys.forest.annotation.Post;

@MyClient
public interface AddressClient3 {

    @Post("/123")
    String testCustomAnnotation();

}
