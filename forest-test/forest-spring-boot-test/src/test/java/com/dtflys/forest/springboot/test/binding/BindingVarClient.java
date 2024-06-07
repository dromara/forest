package com.dtflys.forest.springboot.test.binding;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestRequest;

@BindingVar
public interface BindingVarClient {

    @Get("/{testVariables.testName2}")
    @Address(port = "${port}")
    ForestRequest<String> testBindingVar();

    @Get("/self")
    String self();

}
