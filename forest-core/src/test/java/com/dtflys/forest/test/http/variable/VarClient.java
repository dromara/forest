package com.dtflys.forest.test.http.variable;

import com.dtflys.forest.annotation.Get;

public interface VarClient {

    @Get(url = "/var?id=${query.id}")
    String nullValueVar();
}
