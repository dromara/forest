package com.dtflys.forest.springboot.test.client0;


import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 19:02
 */
public interface BeastshopClient {

    @Request(
            url = "${baseUrl}/autopage/shops.htm",
            timeout = 80000
    )
    String shops();
}
