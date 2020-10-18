package com.dtflys.forest.springboot.test.client2;

import com.dtflys.forest.annotation.LogHandler;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.springboot.test.logging.TestLogHandler;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-09-25 18:30
 */
public interface GiteeClient {

    @Request(
            url = "https://gitee.com/dt_flys/forest",
            timeout = 80000
    )
    @LogHandler(TestLogHandler.class)
    String index();

}
