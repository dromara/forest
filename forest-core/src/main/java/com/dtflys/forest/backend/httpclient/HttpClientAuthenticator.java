package com.dtflys.forest.backend.httpclient;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Optional;

public class HttpClientAuthenticator extends Authenticator {

    private final static HttpClientAuthenticator instance;
    static {
        instance = new HttpClientAuthenticator();
        Authenticator.setDefault(instance);
    }

    private ThreadLocal<PasswordAuthentication> passwordAuthenticationThreadLocal = new ThreadLocal<>();

    public static HttpClientAuthenticator getInstance() {
        return instance;
    }

    public void setPasswordAuthenticator(PasswordAuthentication passwordAuthentication) {
        passwordAuthenticationThreadLocal.set(passwordAuthentication);
    }

    public void setPasswordAuthenticator(String username, String password) {
        setPasswordAuthenticator(new PasswordAuthentication(
                username,
                Optional.of(password).orElse("").toCharArray()));
    }


    public void removePasswordAuthenticator() {
        passwordAuthenticationThreadLocal.remove();
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return passwordAuthenticationThreadLocal.get();
    }
}
