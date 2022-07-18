package com.dtflys.forest.backend.okhttp3;

import com.dtflys.forest.http.ForestCookie;
import okhttp3.Cookie;

import java.time.Duration;
import java.util.Date;

public class OkHttp3Cookie extends ForestCookie {

    public OkHttp3Cookie(long currentTime, Cookie okCookie) {
        super(okCookie.name(), okCookie.value());
        long expiresAt = okCookie.expiresAt();
        long maxAge;
        if (expiresAt > currentTime) {
            maxAge = expiresAt - currentTime;
        } else {
            maxAge = 0L;
        }
        Date createTime = new Date(currentTime);
        Duration maxAgeDuration = Duration.ofMillis(maxAge);

        setCreateTime(createTime);
        setMaxAge(maxAgeDuration);
        setDomain(okCookie.domain());
        setPath(okCookie.path());
        setSecure(okCookie.secure());
        setHttpOnly(okCookie.httpOnly());
        setHostOnly(okCookie.hostOnly());
        setPersistent(okCookie.persistent());
    }
}
