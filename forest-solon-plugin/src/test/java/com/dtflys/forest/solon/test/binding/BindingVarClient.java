package com.dtflys.forest.solon.test.binding;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestRequest;

@ForestClient
public interface BindingVarClient {

    @Get("/")
    @Address(port = "${port}")
    ForestRequest<String> testBindingVar();

}
