package com.dtflys.test.http.address;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-17 2:07
 */
@BaseRequest(interceptor = AddressInterceptor.class)
@Address(source = MyAddressSource.class)
public interface AddressClient2 {

    @Post("/")
    ForestRequest<String> sendAddressSource(@Var("port") int port);

    @Post("/")
    @Address(source = MyAddressSource2.class)
    ForestRequest<String> sendAddressSource2(@Var("port") int port);


}
