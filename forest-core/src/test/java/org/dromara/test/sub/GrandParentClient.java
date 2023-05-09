package org.dromara.test.sub;

import org.dromara.forest.annotation.Get;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-19 3:05
 */
public interface GrandParentClient {

    @Get("/E")
    String testE();
}
