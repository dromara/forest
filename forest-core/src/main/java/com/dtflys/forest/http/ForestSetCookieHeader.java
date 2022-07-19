package com.dtflys.forest.http;

/**
 * Forest 响应中的 Set-Cookie 头
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.23
 */
public class ForestSetCookieHeader extends ForestHeader {

    private ForestCookie cookie;

    private HasURL hasURL;

    public static ForestSetCookieHeader fromCookie(HasURL hasURL, ForestCookie cookie) {
        return new ForestSetCookieHeader("Cookie", hasURL, cookie);
    }

    public static ForestSetCookieHeader fromSetCookie(HasURL hasURL, ForestCookie cookie) {
        return new ForestSetCookieHeader("Set-Cookie", hasURL, cookie);
    }

    public ForestSetCookieHeader(String headerName, HasURL hasURL, ForestCookie cookie) {
        super(headerName, null);
        this.cookie = cookie;
        this.hasURL = hasURL;
    }

    @Override
    public String getValue() {
        return cookie.toString();
    }

    @Override
    public void setValue(String value) {
        ForestCookie newCookie = ForestCookie.parse(hasURL.url().toURLString(), value);
        if (newCookie != null) {
            super.setValue(newCookie.toString());
            this.cookie = newCookie;
        }
    }

    public ForestCookie getCookie() {
        return cookie;
    }

    public void setCookie(ForestCookie cookie) {
        this.cookie = cookie;
    }

}
