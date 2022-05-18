package com.dtflys.forest.springboot.test.client0;


import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.springboot.test.moudle.TestUser;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 19:02
 */
@BaseRequest(baseURL = "${baseUrl}", sslProtocol = "TLS")
public interface BeastshopClient {

    @Get("#{my-props.base-urls}?myToken=${token}")
    @LogEnabled(logResponseContent = true)
    ForestResponse<String> shops(@Var("token") String param);

    @Request(
            url = "#{my-props.base-url}",
            headers = {
                    "MyName: ${user.name}",
                    "MyPass: ${user.password}",
            },
            timeout = 80000
    )
    @LogEnabled(logResponseContent = true)
    ForestResponse<String> shops();


    @Request(
            url = "${idServiceUrl}",
            logEnabled = true
    )
    String testBug(@DataParam("num") Integer num);

    @Post(
            url = "${idServiceUrl}",
            logEnabled = true
    )
    String testBug2(@Query TestUser user);

    @Request(
            url = "${baseUrl}/shops.htm",
            logEnabled = true,
            timeout = 1
    )
    String testRetry();


}
