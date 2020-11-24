package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.GetRequest;
import com.dtflys.forest.extensions.OAuth2;

/**
 * @author HouKunLin
 * @since 1.5.0-BETA9
 */
public interface OAuth2Client {
    /**
     * 测试使用密码登录
     *
     * @return
     */
    @OAuth2(
            tokenUri = "http://127.0.0.1:8081/auth/oauth/token",
            clientId = "password",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.PASSWORD,
            scope = "any",
            username = "root",
            password = "123456"
    )
    @GetRequest(url = "http://127.0.0.1:8081/auth/test/test")
    String testPassword();

    /**
     * 测试使用密码登录，并且把 Token 放在 URL 中
     *
     * @return
     */
    @OAuth2(
            tokenUri = "http://127.0.0.1:8081/auth/oauth/token",
            clientId = "password",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.PASSWORD,
            scope = "any",
            username = "root",
            password = "123456",
            tokenAt = OAuth2.TokenAt.URL
    )
    @GetRequest(url = "http://127.0.0.1:8081/auth/test/test")
    String testPasswordTokenAtURL();

    /**
     * 测试使用 CLIENT_CREDENTIALS 模式调用接口
     *
     * @return
     */
    @OAuth2(
            tokenUri = "http://127.0.0.1:8081/auth/oauth/token",
            clientId = "client_credentials",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.CLIENT_CREDENTIALS,
            scope = "any"
    )
    @GetRequest(url = "http://127.0.0.1:8081/auth/test/test")
    String testClientCredentials();

    /**
     * 测试使用 CLIENT_CREDENTIALS 模式调用接口，把 Token 放在URL中
     *
     * @return
     */
    @OAuth2(
            tokenUri = "http://127.0.0.1:8081/auth/oauth/token",
            clientId = "client_credentials",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.CLIENT_CREDENTIALS,
            scope = "any",
            tokenAt = OAuth2.TokenAt.URL
    )
    @GetRequest(url = "http://127.0.0.1:8081/auth/test/test")
    String testClientCredentialsTokenAtURL();
}
