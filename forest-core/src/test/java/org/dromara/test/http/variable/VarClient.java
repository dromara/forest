package org.dromara.test.http.variable;

import org.dromara.forest.annotation.Get;

public interface VarClient {

    @Get(url = "/var?id=${query.id}")
    String nullValueVar();
}