package com.dtflys.forest.lifecycles.authorization;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.extensions.OAuth2;
import com.dtflys.forest.handler.OAuth2DefinitionHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * OAuth2 注解的生命周期.
 *
 * @author HouKunLin
 * @since 1.5.0-BETA9
 */
public class OAuth2LifeCycle implements MethodAnnotationLifeCycle<OAuth2, Object> {
    private final byte[] lock = new byte[0];
    /**
     * Token 缓存
     */
    private final Map<String, TokenCache> cache = new LinkedHashMap<>();
    private OAuth2Client oAuth2Client;

    @Override
    public void onMethodInitialized(ForestMethod method, OAuth2 annotation) {
        oAuth2Client = method.getConfiguration().createInstance(OAuth2Client.class);
    }

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        final TokenCache tokenCache = getTokenCache(request);

        // Token 的传输位置：Header、URL
        final OAuth2.TokenAt tokenAt = (OAuth2.TokenAt) getAttribute(request, "tokenAt");

        // 传输 Token 的变量名
        final String defaultTokenVariable = getAttributeAsString(request, "tokenVariable");
        final String tokenVariable = tokenAt.getTokenVariable(defaultTokenVariable);

        // Token 前缀
        final String defaultPrefix = getAttributeAsString(request, "tokenPrefix");
        final String tokenValue = tokenAt.getTokenValue(defaultPrefix, tokenCache.getAccessToken());

        if (tokenAt == OAuth2.TokenAt.HEADER) {
            request.addHeader(tokenVariable, tokenValue);
        } else {
            request.addQuery(tokenVariable, tokenValue);
        }
        return proceed();
    }

    /**
     * 获得缓存ID。确保相同的配置能够获取到唯一的ID
     *
     * @param request 当前请求
     * @return 缓存ID
     */
    private String getCacheId(ForestRequest request) {
        final String cacheId = getAttributeAsString(request, "cacheId");
        if (StringUtils.isNotBlank(cacheId)) {
            return cacheId;
        }
        // tokenUri/clientId/grantType/scope/username 任何一个变动都可能是不同的帐号权限
        final String tokenUri = getAttributeAsString(request, "tokenUri");
        final String clientId = getAttributeAsString(request, "clientId");
        final Object grantType = getAttribute(request, "grantType");
        final String scope = getAttributeAsString(request, "scope");
        final String username = getAttributeAsString(request, "username");

        return tokenUri + ":"
                + clientId + ":"
                + grantType + ":"
                + scope + ":"
                + username;
    }

    /**
     * 获取一个 Token 缓存对象信息
     *
     * @param request 当前请求对象
     * @return Token 信息
     */
    private TokenCache getTokenCache(ForestRequest request) {
        final String cacheId = getCacheId(request);
        TokenCache tokenCache = this.cache.get(cacheId);
        if (tokenCache == null) {
            return obtainTokenCache(request, cacheId, null);
        }
        // 缓存中存在 TokenCache ，需要判断该 TokenCache 是否处于过期状态，假如已经过期则重新请求 Token，否则返回原来的对象
        tokenCache = obtainTokenCache(request, cacheId, tokenCache);
        // 缓存中存在 TokenCache ，需要判断该 TokenCache 是否处于即将过期状态，假如在即将过期的时间内则刷新 Token，否则返回原来的对象
        tokenCache = obtainRefreshTokenCache(request, cacheId, tokenCache);
        return tokenCache;
    }

    /**
     * 获得 Token 信息，假如 Token 失效将重新请求 Token。处理并发环境下请求Token操作
     *
     * @param request    当前请求
     * @param cacheId    缓存ID
     * @param tokenCache 当前 Token
     * @return Token 信息
     */
    @Nonnull
    private TokenCache obtainTokenCache(ForestRequest request, String cacheId, TokenCache tokenCache) {
        if (tokenCache != null && tokenCache.getExpiresIn() > 0) {
            // Token还未过期，还能继续使用
            return tokenCache;
        }
        // Token过期，防止并发场景重复获取Token
        synchronized (lock) {
            tokenCache = this.cache.get(cacheId);
            if (tokenCache != null && tokenCache.getExpiresIn() > 0) {
                // 可能在并发场景下已经获取过一次Token
                return tokenCache;
            }
            tokenCache = requestToken(request);
            this.cache.put(cacheId, tokenCache);
            return tokenCache;
        }
    }

    /**
     * 获得刷新 Token 信息，假如 Token 即将失效，将刷新 Token 或者重新请求 Token。处理并发环境下刷新Token操作
     *
     * @param request    当前请求
     * @param cacheId    缓存ID
     * @param tokenCache 当前 Token
     * @return Token 信息
     */
    @Nonnull
    private TokenCache obtainRefreshTokenCache(ForestRequest request, String cacheId, @Nonnull TokenCache tokenCache) {
        final int refreshAtExpiresBefore = getAttributeAsInteger(request, "refreshAtExpiresBefore");
        if (tokenCache.getExpiresIn() > refreshAtExpiresBefore) {
            // Token还未到刷新Token的时间，还能继续使用
            return tokenCache;
        }
        // Token 即将过期，防止并发场景重复刷新Token
        synchronized (lock) {
            tokenCache = this.cache.get(cacheId);
            if (tokenCache.getExpiresIn() > refreshAtExpiresBefore) {
                // 可能在并发场景下已经执行过一次刷新Token操作
                return tokenCache;
            }
            if (StringUtils.isBlank(tokenCache.getRefreshToken())) {
                // 不存在刷新Token令牌（未开启 refresh_token），因此不能直接刷新Token，需要重新获取Token
                tokenCache = requestToken(request);
            } else {
                tokenCache = requestRefreshToken(request, tokenCache);
            }
            this.cache.put(cacheId, tokenCache);
            return tokenCache;
        }
    }

    /**
     * 从远程服务器请求 Token
     *
     * @param request 当前请求对象
     * @return 返回新的 Token 信息
     */
    @Nonnull
    private TokenCache requestToken(ForestRequest request) {
        final String clientId = getAttributeAsString(request, "clientId");
        final Map<String, Object> body = createRequestBody(clientId, request, true);
        return executeRequestToken(request, clientId, body);
    }

    /**
     * 从远程服务器刷新 Token
     *
     * @param request    当前请求对象
     * @param tokenCache 缓存的 Token 信息
     * @return 返回新的 Token 信息
     */
    private TokenCache requestRefreshToken(ForestRequest request, TokenCache tokenCache) {
        final String clientId = getAttributeAsString(request, "clientId");
        final Map<String, Object> body = createRequestBody(clientId, request, false);

        body.put("grant_type", "refresh_token");
        body.put("refresh_token", tokenCache.getRefreshToken());

        return executeRequestToken(request, clientId, body);
    }

    /**
     * 执行实际的网络请求
     *
     * @param request  当前请求
     * @param clientId 客户端ID
     * @param body     请求内容
     * @return 返回新的 Token 信息
     */
    private TokenCache executeRequestToken(ForestRequest request, String clientId, Map<String, Object> body) {
        // 加入扩展参数
        final String[] bodyItems = (String[]) getAttribute(request, "body");
        body.putAll(kv2map(bodyItems));

        final Map<String, Object> queryItems = kv2map((String[]) getAttribute(request, "query"));
        final Class<? extends OAuth2DefinitionHandler> handlerClass = getAttribute(request, "OAuth2TokenHandler", Class.class);
        final ForestResponse<String> response = oAuth2Client.token(getAttributeAsString(request, "tokenUri"), queryItems, body);
        OAuth2Token token;
        try {
            final OAuth2DefinitionHandler handler = handlerClass.newInstance();
            final ForestConfiguration configuration = request.getConfiguration();
            final Map map = (Map) configuration
                    .getConverter(ForestDataType.AUTO)
                    .convertToJavaObject(response.getContent(), Map.class);
            token = handler.getOAuth2Token(response, map);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ForestRuntimeException("OAuth2 request OAuth2DefinitionHandler error" + e.getMessage());
        }
        if (token == null) {
            throw new ForestRuntimeException("OAuth2 request OAuth2Token is empty");
        }
        return new TokenCache(clientId, token);
    }

    /**
     * Key-Value 转换成 Map 对象
     *
     * @param values Key-Value 对象
     * @return Map 对象
     */
    private Map<String, Object> kv2map(String[] values) {
        final Map<String, Object> map = new HashMap<>();
        for (String value : values) {
            final int indexOf = value.indexOf(":");
            map.put(value.substring(0, indexOf), StringUtils.trimBegin(value.substring(indexOf + 1)));
        }
        return map;
    }

    /**
     * 创建请求默认参数
     *
     * @param clientId    客户端ID
     * @param request     请求对象
     * @param fillAccount 是否填充帐号信息。该帐号信息在注解中设置
     * @return 返回请求参数
     */
    private Map<String, Object> createRequestBody(String clientId, ForestRequest request, boolean fillAccount) {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", getAttributeAsString(request, "clientSecret"));
        body.put("scope", getAttributeAsString(request, "scope"));

        final OAuth2.GrantType grantType = (OAuth2.GrantType) getAttribute(request, "grantType");
        final String grantTypeValue = grantType.getValue(getAttributeAsString(request, "grantTypeValue"));
        body.put("grant_type", grantTypeValue);

        if (fillAccount && grantType == OAuth2.GrantType.PASSWORD) {
            body.put("username", getAttributeAsString(request, "username"));
            body.put("password", getAttributeAsString(request, "password"));
        }
        return body;
    }

    /**
     * 缓存 Token 信息的对象.
     */
    public static class TokenCache {
        private final String clientId;
        private final String accessToken;
        private final String refreshToken;
        @Deprecated
        private final String tokenType;
        /**
         * 过期时间点
         */
        private final LocalDateTime expiresAt;

        /**
         * 初始化一个 Token 缓存对象.
         *
         * @param clientId 客户端ID
         * @param token    请求服务器端返回的 Token 结果
         */
        public TokenCache(String clientId, OAuth2Token token) {
            if (token.hasError()) {
                // 通常可能不会执行到这里，因为一旦 OAuth2 获取失败会返回一个 HTTP CODE 400 ，这个 HTTP CODE 会直接被程序抛出异常
                // 但是也有一些 OAuth2 服务器它们会返回 HTTP CODE 200 然后程序走到这里，例如微信公众号开发的服务器就会执行到这里
                throw new ForestRuntimeException("OAuth2 request Token failure, response: " + token.getErrorMessage());
            }

            this.clientId = clientId;
            this.accessToken = token.getAccess_token();
            this.refreshToken = token.getRefresh_token();
            this.tokenType = token.getToken_type();
            final Long expires = Optional.ofNullable(token.getExpires_in()).orElse(0L);
            // 设置 Token 到期时间：当前时间 + Token有效期
            this.expiresAt = LocalDateTime.now().plusSeconds(expires);
        }

        /**
         * 判断Token是否有效
         *
         * @return true有效 ，false失效
         */
        public boolean isNoExpires() {
            return LocalDateTime.now().isBefore(this.expiresAt);
        }

        /**
         * Token有效期剩余时间.
         *
         * @return Token有效期剩余时间
         */
        public long getExpiresIn() {
            final Duration between = Duration.between(LocalDateTime.now(), this.expiresAt);
            return between.getSeconds();
        }

        public String getClientId() {
            return clientId;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }
    }
}
