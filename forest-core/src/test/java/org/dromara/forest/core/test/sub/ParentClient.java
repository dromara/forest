package org.dromara.forest.core.test.sub;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Get;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-19 2:51
 */
@Address(port = "12")
public interface ParentClient {

    @Get("/A")
    String testA();

    @Get("/B")
    String testB();

    @Get("/X")
    String testC();
}
