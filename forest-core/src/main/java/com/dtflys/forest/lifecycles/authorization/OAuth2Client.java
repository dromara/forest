package com.dtflys.forest.lifecycles.authorization;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.PostRequest;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.http.ForestResponse;

import java.util.Map;

/**
 * OAuth2 请求
 *
 * @author HouKunLin
 * @since 1.5.0-BETA9
 */
public interface OAuth2Client {
    /**
     * 获取 Token
     *
     * @param tokenUri 获取 Token 的地址
     * @param query    GET 参数
     * @param body     POST 参数
     * @return 返回json信息 {@link OAuth2Token}类实例
     */
    @PostRequest(url = "${tokenUri}")
    ForestResponse token(@DataVariable("tokenUri") String tokenUri,
                 @Query Map<String, Object> query,
                 @Body Map<String, Object> body);
}
