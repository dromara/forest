package org.dromara.forest.core.test.sub;

import org.dromara.forest.annotation.Get;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-19 3:03
 */
public interface ParentClient2 extends GrandParentClient {

    @Get("/D")
    String testD();
}
