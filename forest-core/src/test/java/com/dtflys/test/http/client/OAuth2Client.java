package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.GetRequest;
import com.dtflys.forest.extensions.OAuth2;
import com.dtflys.test.handler.OAuth2TokenTestHandler;

/**
 * @author HouKunLin
 * @since 1.5.0-BETA9
 */
public interface OAuth2Client {
    /**
     * 测试使用密码登录
     */
    @OAuth2(
            tokenUri = "http://localhost:${port}/auth/oauth/token",
            clientId = "password",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.PASSWORD,
            scope = "any",
            username = "root",
            password = "123456"
    )
    @GetRequest(url = "http://localhost:${port}/auth/test/password")
    String testPassword();

    /**
     * 测试使用密码登录，并且把 Token 放在 URL 中
     */
    @OAuth2(
            tokenUri = "http://localhost:${port}/auth/oauth/token",
            clientId = "password",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.PASSWORD,
            scope = "any",
            body = {
                    "username: ${0}",
                    "password: ${1}"
            },
            tokenAt = OAuth2.TokenAt.URL
    )
    @GetRequest(url = "http://localhost:${port}/auth/test/password_at_url")
    String testPasswordTokenAtURL(String username, String password);

    /**
     * 测试使用 CLIENT_CREDENTIALS 模式调用接口
     */
    @OAuth2(
            tokenUri = "http://localhost:${port}/auth/oauth/client_credentials/token",
            clientId = "client_credentials",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.CLIENT_CREDENTIALS,
            scope = "any"
    )
    @GetRequest(url = "http://localhost:${port}/auth/test/client_credentials")
    String testClientCredentials();

    /**
     * 测试使用 CLIENT_CREDENTIALS 模式调用接口，把 Token 放在URL中
     */
    @OAuth2(
            tokenUri = "http://localhost:${port}/auth/oauth/client_credentials/token",
            clientId = "client_credentials",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.CLIENT_CREDENTIALS,
            scope = "any",
            tokenAt = OAuth2.TokenAt.URL
    )
    @GetRequest(url = "http://localhost:${port}/auth/test/client_credentials_at_url")
    String testClientCredentialsTokenAtURL();


    /**
     * 测试使用自定义响应体
     */
    @OAuth2(
            tokenUri = "http://localhost:${port}/auth/oauth/token/definition",
            clientId = "password",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.PASSWORD,
            scope = "any",
            username = "root",
            password = "123456",
            OAuth2TokenHandler = OAuth2TokenTestHandler.class
    )
    @GetRequest(url = "http://localhost:${port}/auth/test/password")
    String testDefinitionOAuth2();
}
