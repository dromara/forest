package org.dromara.forest.springboot.test.service;

import org.dromara.forest.http.ForestResponse;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-03-23 23:10
 */
public interface TestService {

    ForestResponse<String> shops();
}