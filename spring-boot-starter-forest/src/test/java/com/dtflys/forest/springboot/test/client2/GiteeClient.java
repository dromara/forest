package com.dtflys.forest.springboot.test.client2;

import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.annotation.LogHandler;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.springboot.test.logging.TestLogHandler;
import com.dtflys.forest.springboot.test.logging.TestLogHandler2;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-09-25 18:30
 */
@LogHandler(TestLogHandler2.class)
public interface GiteeClient {

    @Request(
            url = "https://gitee.com/dt_flys/forest",
            timeout = 80000
    )
    @LogHandler(TestLogHandler.class)
    @LogEnabled(logResponseStatus = false)
    ForestRequest<String> index();

    @Request(
            url = "https://gitee.com/dt_flys",
            timeout = 80000
    )
    @LogEnabled(logResponseStatus = false)
    ForestRequest<String> index2();


}
