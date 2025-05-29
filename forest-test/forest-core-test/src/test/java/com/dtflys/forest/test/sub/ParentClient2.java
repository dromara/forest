package com.dtflys.forest.test.sub;

import com.dtflys.forest.annotation.Get;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-19 3:03
 */
public interface ParentClient2 extends GrandParentClient {

    @Get("/D")
    String testD();
}
