package com.dtflys.forest.springboot.test.client1;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-09-25 18:29
 */
public interface BaiduClient {

    @Request(
            url = "http://132.21.17.33:2552/xxx/test"
    )
    ForestResponse<String> testTimeout();

}
