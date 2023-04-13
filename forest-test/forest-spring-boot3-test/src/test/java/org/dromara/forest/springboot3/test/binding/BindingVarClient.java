package org.dromara.forest.springboot3.test.binding;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestRequest;

public interface BindingVarClient {

    @Get("/")
    @Address(port = "${port}")
    ForestRequest<String> testBindingVar();

}
