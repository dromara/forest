package com.dtflys.forest.springboot.test.client0;


import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.springboot.test.moudle.TestUser;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 19:02
 */
public interface BeastshopClient {

    @Request(
            url = "${baseUrl}/autopage/shops.htm",
            headers = {
              "MyName: ${user.name}",
              "MyPass: ${user.password}",
            },
            timeout = 80000
    )
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
            url = "${baseUrl}/autopage/shops.htm",
            logEnabled = true,
            timeout = 1
    )
    String testRetry();


}
