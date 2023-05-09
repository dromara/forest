package org.dromara.test.sub;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Get;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-19 2:52
 */
@Address(host = "127.0.0.1", port = "${port}")
public interface SubClient extends ParentClient, ParentClient2 {

    @Get("/C")
    String testC();

}
