package org.dromara.forest.backend.httpclient;

import org.dromara.forest.http.ForestCookie;

import java.time.Duration;
import java.util.Date;

public class HttpclientCookie extends ForestCookie {

    public HttpclientCookie(org.apache.http.cookie.Cookie httpCookie) {
        super(httpCookie.getName(), httpCookie.getValue());
        long currentTime = System.currentTimeMillis();
        Date expiresDate = httpCookie.getExpiryDate();
        long maxAge;
        if (expiresDate != null) {
            long expiresAt = expiresDate.getTime();
            if (expiresAt > currentTime) {
                maxAge = expiresAt - currentTime;
            } else {
                maxAge = 0L;
            }
        } else {
            maxAge = Long.MAX_VALUE;
        }
        Date createTime = new Date(currentTime);
        Duration maxAgeDuration = Duration.ofMillis(maxAge);

        setCreateTime(createTime);
        setMaxAge(maxAgeDuration);
        setDomain(httpCookie.getDomain());
        setPath(httpCookie.getPath());
        setSecure(httpCookie.isSecure());
        setPersistent(httpCookie.isPersistent());
    }
}
