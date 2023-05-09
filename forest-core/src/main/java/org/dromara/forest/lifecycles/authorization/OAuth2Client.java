package org.dromara.forest.lifecycles.authorization;

import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.DataVariable;
import org.dromara.forest.annotation.PostRequest;
import org.dromara.forest.annotation.Query;
import org.dromara.forest.http.ForestResponse;

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
    ForestResponse<String> token(@DataVariable("tokenUri") String tokenUri,
                 @Query Map<String, Object> query,
                 @Body Map<String, Object> body);
}
