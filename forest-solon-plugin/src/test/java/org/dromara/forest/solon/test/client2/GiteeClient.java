package org.dromara.forest.solon.test.client2;

import org.dromara.forest.annotation.*;
import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.LogEnabled;
import org.dromara.forest.annotation.LogHandler;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.solon.test.logging.TestLogHandler;
import org.dromara.forest.solon.test.logging.TestLogHandler2;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-09-25 18:30
 */
@ForestClient
@LogHandler(TestLogHandler2.class)
@LogEnabled(logResponseStatus = true)
@BaseRequest(baseURL = "#{my-site.base-url}")
public interface GiteeClient {

    @Request(
            url = "/dt_flys/#{test.path}",
            timeout = 80000,
            sslProtocol = "SSL"
    )
    @LogHandler(TestLogHandler.class)
    @LogEnabled(logResponseStatus = true, logResponseContent = true)
    ForestRequest<String> index();

    @Request(
            url = "/dt_flys",
            timeout = 80000,
            keyStore = "keystore1"
    )
    ForestRequest<String> index2();


    @Request(
            url = "https://baidu.com/",
            timeout = 80000,
            keyStore = "keystore1"
    )
    String index3();


}
