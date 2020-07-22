package com.dtflys.forest.springboot.test.client2;

import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-09-25 18:30
 */
public interface GiteeClient {

    @Request(
            url = "https://gitee.com/dt_flys/forest",
            timeout = 80000
    )
    String index();

}
