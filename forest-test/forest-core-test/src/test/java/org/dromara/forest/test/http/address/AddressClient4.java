package org.dromara.forest.test.http.address;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Post;

@Address(host = "127.0.0.1", port = "8888")
public interface AddressClient4 {

    @Post("http://localhost:{port}/")
    String test();

}
