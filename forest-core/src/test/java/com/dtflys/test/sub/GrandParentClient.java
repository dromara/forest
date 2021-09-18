package com.dtflys.test.sub;

import com.dtflys.forest.annotation.Get;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-19 3:05
 */
public interface GrandParentClient {

    @Get("/E")
    String testE();
}
