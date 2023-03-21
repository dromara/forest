package com.dtflys.forest.springboot.test.binding;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestRequest;

public interface BindingVarClient {

    @Get("/")
    @Address(port = "${port}")
    ForestRequest<String> testBindingVar();

}
