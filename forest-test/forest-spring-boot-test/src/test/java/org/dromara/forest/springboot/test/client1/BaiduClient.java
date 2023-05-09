package org.dromara.forest.springboot.test.client1;

import org.dromara.forest.annotation.Request;
import org.dromara.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-09-25 18:29
 */
public interface BaiduClient {

    @Request(
            url = "http://132.21.17.33:2552/xxx/test?t=${test($0)}"
    )
//    @LogEnabled(logRequest = true)
    ForestResponse<String> testTimeout(String text);

}
