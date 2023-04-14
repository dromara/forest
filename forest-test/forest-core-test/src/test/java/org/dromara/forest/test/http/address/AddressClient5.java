package org.dromara.forest.test.http.address;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.http.ForestRequest;

@Address(basePath = "http://localhost:{port}/aaa")
public interface AddressClient5 {

    @Post("/bbb")
    ForestRequest test();

}
