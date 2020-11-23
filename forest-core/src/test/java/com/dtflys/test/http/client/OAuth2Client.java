package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.GetRequest;
import com.dtflys.forest.extensions.OAuth2;

/**
 * @author HouKunLin
 * @since 1.5.0-BETA9
 */
public interface OAuth2Client {
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

    @OAuth2(
            tokenUri = "http://127.0.0.1:8081/auth/oauth/token",
            clientId = "client_credentials",
            clientSecret = "123456",
            grantType = OAuth2.GrantType.CLIENT_CREDENTIALS,
            scope = "any"
    )
    @GetRequest(url = "http://127.0.0.1:8081/auth/test/test")
    String testClientCredentials();
}
