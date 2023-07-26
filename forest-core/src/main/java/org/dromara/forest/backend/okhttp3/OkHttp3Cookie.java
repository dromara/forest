package org.dromara.forest.backend.okhttp3;

import org.dromara.forest.http.ForestCookie;
import okhttp3.Cookie;

import java.time.Duration;
import java.util.Date;

public class OkHttp3Cookie extends ForestCookie {

    public OkHttp3Cookie(long currentTime, Cookie okCookie) {
        super(okCookie.name(), okCookie.value());
        final long expiresAt = okCookie.expiresAt();
        final long maxAge = expiresAt > currentTime ? expiresAt - currentTime : 0L;
        final Date createTime = new Date(currentTime);
        final Duration maxAgeDuration = Duration.ofMillis(maxAge);

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
