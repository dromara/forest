package org.dromara.forest.solon.test.binding;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestRequest;

@ForestClient
public interface BindingVarClient {

    @Get("/")
    @Address(port = "${port}")
    ForestRequest<String> testBindingVar();

}
