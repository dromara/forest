package com.dtflys.spring.test.client2;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.backend.ContentType;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-09-25 18:30
 */
public interface GithubClient {

    @Request(
            url = "https://www.github.com",
            timeout = 80000,
            contentType = ContentType.APPLICATION_JSON
    )
    String index();

}
