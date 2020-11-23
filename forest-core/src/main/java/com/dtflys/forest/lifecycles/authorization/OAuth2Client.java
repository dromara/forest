package com.dtflys.forest.lifecycles.authorization;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.PostRequest;
import com.dtflys.forest.annotation.Query;

import java.util.Map;

/**
 * OAuth2 请求
 *
 * @author HouKunLin
 * @date 2020 /11/23 0023 15:38
 */
public interface OAuth2Client {
    /**
     * 获取 Token
     *
     * @param tokenUri 获取 Token 的地址
     * @param params   GET 参数
     * @param body     POST 参数
     * @return 返回json信息 map
     */
    @PostRequest(url = "${tokenUri}")
    Map<String, Object> token(@DataVariable("tokenUri") String tokenUri, @Query Map<String, Object> params, @Body Map<String, Object> body);
}
