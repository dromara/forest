package org.dromara.forest.core.test.http.address;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-17 2:07
 */
@Address(source = MyAddressSource.class)
public interface AddressClient2 {

    @Post("/")
    ForestRequest<String> sendAddressSource(@Var("port") int port);

    @Post("/")
    @Address(source = MyAddressSource2.class)
    ForestRequest<String> sendAddressSource2(@Var("port") int port);


}
