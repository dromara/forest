package org.dromara.forest.solon.test.client0;


import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.DataParam;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.LogEnabled;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Query;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.solon.test.moudle.TestUser;
import org.dromara.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 19:02
 */
@ForestClient
@BaseRequest(baseURL = "${baseUrl}", sslProtocol = "TLS")
public interface BeastshopClient {

    @Get("#{my-props.base-url}?myToken=${token}")
    @LogEnabled(logRequest = true, logResponseContent = true)
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
