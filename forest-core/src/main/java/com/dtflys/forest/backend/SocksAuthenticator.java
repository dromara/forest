package com.dtflys.forest.backend;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Optional;

public class SocksAuthenticator extends Authenticator {

    private final static SocksAuthenticator instance;
    static {
        instance = new SocksAuthenticator();
        Authenticator.setDefault(instance);
    }

    private ThreadLocal<PasswordAuthentication> passwordAuthenticationThreadLocal = new ThreadLocal<>();

    public static SocksAuthenticator getInstance() {
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
