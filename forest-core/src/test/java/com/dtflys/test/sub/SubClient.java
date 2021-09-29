package com.dtflys.test.sub;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-19 2:52
 */
@Address(host = "127.0.0.1", port = "${port}")
public interface SubClient extends ParentClient, ParentClient2 {

    @Get("/C")
    String testC();

}
