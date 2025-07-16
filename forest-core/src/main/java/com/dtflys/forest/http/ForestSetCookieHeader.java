package com.dtflys.forest.http;

/**
 * Forest 响应中的 Set-Cookie 头
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.23
 */
public class ForestSetCookieHeader extends SimpleHeader {

    private ForestCookie cookie;

    public static ForestSetCookieHeader fromCookie(HasURL hasURL, ForestCookie cookie) {
        return new ForestSetCookieHeader("Cookie", hasURL, cookie);
    }

    public static ForestSetCookieHeader fromSetCookie(HasURL hasURL, ForestCookie cookie) {
        return new ForestSetCookieHeader("Set-Cookie", hasURL, cookie);
    }

    public ForestSetCookieHeader(String headerName, HasURL hasURL, ForestCookie cookie) {
        super(hasURL, headerName, null);
        this.cookie = cookie;
    }

    @Override
    public String getValue() {
        return cookie.toString();
    }

    @Override
    public ForestSetCookieHeader setValue(String value) {
        final ForestCookie newCookie = ForestCookie.parse(hasURL.url().toURLString(), value);
        if (newCookie != null) {
            super.setValue(newCookie.toString());
            this.cookie = newCookie;
        }
        return this;
    }

    public ForestCookie getCookie() {
        return cookie;
    }

    public void setCookie(ForestCookie cookie) {
        this.cookie = cookie;
    }

}
