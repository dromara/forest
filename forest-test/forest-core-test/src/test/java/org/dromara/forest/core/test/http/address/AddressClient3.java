package org.dromara.forest.core.test.http.address;

import org.dromara.forest.annotation.Post;

@MyClient
public interface AddressClient3 {

    @Post("/123")
    String testCustomAnnotation();

}
