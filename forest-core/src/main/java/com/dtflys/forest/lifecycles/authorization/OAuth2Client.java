package com.dtflys.forest.lifecycles.authorization;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.extensions.OAuth2;
import com.dtflys.forest.http.ForestResponse;

import java.util.Map;

/**
 * OAuth2 请求
 *
 * @author HouKunLin
 * @since 1.5.0-BETA9
 * @deprecated 已过时，在引入 {@link OAuth2#forestInterceptor()} 功能后，这个接口用法就不再使用了，该接口在未来将会被删除，如果你的代码中有用到该接口，请及时迁移
 */
@Deprecated
public interface OAuth2Client {
    /**
     * 获取 Token
     *
     * @param tokenUri 获取 Token 的地址
     * @param headers  HEADER 参数
     * @param query    GET 参数
     * @param body     POST 参数
     * @return 返回json信息 {@link OAuth2Token}类实例
     * @deprecated 已过时，在引入 {@link OAuth2#forestInterceptor()} 功能后，这个接口用法就不再使用了，该接口在未来将会被删除，如果你的代码中有用到该接口，请及时迁移
     */
    @Deprecated
    @PostRequest(url = "${tokenUri}")
    ForestResponse<String> token(@Var("tokenUri") String tokenUri,
                                 @Header Map<String, Object> headers,
                                 @Query Map<String, Object> query,
                                 @Body Map<String, Object> body);
}
