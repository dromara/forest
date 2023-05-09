package org.dromara.spring.test.client1;

import org.dromara.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-09-25 18:29
 */
public interface BaiduClient {

    @Request(
            url = "http://www.baidu.com",
            timeout = 80000
    )
    String index();

}
