package org.dromara.forest.spring.test.client0;


import org.dromara.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 19:02
 */
public interface BeastshopClient {

    @Request(
            url = "https://www.thebeastshop.com/",
            timeout = 80000,
            keyStore = "keystore1"
    )
    String index();


    @Request(
            url = "https://www.baidu.com/",
            timeout = 80000,
            keyStore = "keystore1"
    )
    String index2();
}
