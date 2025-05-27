package com.dtflys.forest.test.misc;

import com.dtflys.forest.backend.httpclient.HttpclientCookie;
import com.dtflys.forest.backend.okhttp3.OkHttp3Cookie;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.junit.Test;

import java.time.Duration;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CookieTest {

    @Test
    public void testCookie() throws InterruptedException {
        Date date = new Date();
        Duration maxAge = Duration.ofMillis(1000L);
        String domain = "localhost";
        String path = "/";
        boolean secure = true;
        boolean httpOnly = false;
        boolean hostOnly = true;
        boolean persistent = false;
        ForestCookie cookie = new ForestCookie(
                "foo",
                "bar",
                date,
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                hostOnly,
                persistent
        );
        long expiresTime = date.getTime() + maxAge.toMillis();
        assertThat(cookie.getName()).isEqualTo("foo");
        assertThat(cookie.getValue()).isEqualTo("bar");
        assertThat(cookie.getCreateTime()).isEqualTo(date);
        assertThat(cookie.getMaxAge()).isEqualTo(maxAge);
        assertThat(cookie.getPath()).isEqualTo(path);
        assertThat(cookie.isSecure()).isEqualTo(true);
        assertThat(cookie.isHttpOnly()).isEqualTo(false);
        assertThat(cookie.isHostOnly()).isEqualTo(true);
        assertThat(cookie.isPersistent()).isEqualTo(false);
        assertThat(cookie.getExpiresTime()).isEqualTo(expiresTime);
        assertThat(cookie.isExpired(new Date())).isFalse();
        assertThat(cookie.toString()).isEqualTo("foo=bar; path=/; secure");
        Thread.sleep(1000L);
        assertThat(cookie.isExpired(new Date())).isTrue();

        maxAge = Duration.ofMillis(0L);
        secure = false;
        httpOnly = true;
        hostOnly = false;
        persistent = true;
        cookie = new ForestCookie(
                "foo",
                "bar",
                date,
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                hostOnly,
                persistent
        );
        assertThat(cookie.toString())
                .isEqualTo("foo=bar; max-age=0; domain=localhost; path=/; httponly");

        maxAge = Duration.ofMillis(1000L);
        secure = false;
        httpOnly = false;
        hostOnly = false;
        persistent = true;
        cookie = new ForestCookie(
                "foo",
                "bar",
                date,
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                hostOnly,
                persistent
        );
        assertThat(cookie.toString())
                .isEqualTo("foo=bar; max-age=1; domain=localhost; path=/");
    }

    @Test
    public void testMatchDomain() {
        Date date = new Date();
        Duration maxAge = Duration.ofMillis(1000L);
        String domain = "localhost";
        String path = "/";
        boolean secure = true;
        boolean httpOnly = false;
        boolean hostOnly = false;
        boolean persistent = false;
        ForestCookie cookie = new ForestCookie(
                "foo",
                "bar",
                date,
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                hostOnly,
                persistent
        );
        assertThat(cookie.matchDomain("localhost")).isTrue();
        assertThat(cookie.matchDomain("baidu.com")).isFalse();

        domain = "forest.dtflyx.com";
        cookie = new ForestCookie(
                "foo",
                "bar",
                date,
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                hostOnly,
                persistent
        );
        assertThat(cookie.matchDomain("forest.dtflyx.com")).isTrue();
        assertThat(cookie.matchDomain("dtflyx.com")).isFalse();

        cookie = new ForestCookie(
                "foo",
                "bar",
                date,
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                true,
                persistent
        );
        assertThat(cookie.matchDomain("forest.dtflyx.com")).isTrue();
        assertThat(cookie.matchDomain("dtflyx.com")).isFalse();

    }


    @Test
    public void testMatchPath() {
        Date date = new Date();
        Duration maxAge = Duration.ofMillis(1000L);
        String domain = "localhost";
        String path = "/";
        boolean secure = true;
        boolean httpOnly = false;
        boolean hostOnly = true;
        boolean persistent = false;
        ForestCookie cookie = new ForestCookie(
                "foo",
                "bar",
                date,
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                hostOnly,
                persistent
        );
        assertThat(cookie.matchPath("/")).isTrue();
        assertThat(cookie.matchPath("/foo")).isTrue();
        assertThat(cookie.matchPath("/foo/bar")).isTrue();

        path = "/foo";
        cookie = new ForestCookie(
                "foo",
                "bar",
                date,
                maxAge,
                domain,
                path,
                secure,
                httpOnly,
                hostOnly,
                persistent
        );

        assertThat(cookie.matchPath("/")).isFalse();
        assertThat(cookie.matchPath("/foo")).isTrue();
        assertThat(cookie.matchPath("/foo/bar")).isTrue();
    }



    @Test
    public void testParseCookie() {
        Duration maxAge = Duration.ofSeconds(1L);
        String url = "http://forest.dtflyx.com/docs";
        String setCookie = "foo=bar; max-age=" + maxAge.getSeconds();
        ForestCookie cookie = ForestCookie.parse(url, setCookie);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("foo");
        assertThat(cookie.getValue()).isEqualTo("bar");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(maxAge.getSeconds());
        long expiresTime = cookie.getCreateTime().getTime() + cookie.getMaxAge().toMillis();
        assertThat(cookie.getExpiresTime()).isEqualTo(expiresTime);
        assertThat(cookie.getDomain()).isEqualTo("forest.dtflyx.com");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isPersistent()).isTrue();
        assertThat(cookie.isSecure()).isFalse();
        assertThat(cookie.isHostOnly()).isTrue();
        assertThat(cookie.isHttpOnly()).isFalse();

        setCookie = "foo=bar; max-age=" + maxAge.getSeconds() + "; domain=dtflyx.com; secure";
        cookie = ForestCookie.parse(url, setCookie);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("foo");
        assertThat(cookie.getValue()).isEqualTo("bar");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(maxAge.getSeconds());
        expiresTime = cookie.getCreateTime().getTime() + cookie.getMaxAge().toMillis();
        assertThat(cookie.getExpiresTime()).isEqualTo(expiresTime);
        assertThat(cookie.getDomain()).isEqualTo("dtflyx.com");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isPersistent()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.isHostOnly()).isFalse();
        assertThat(cookie.isHttpOnly()).isFalse();
    }

    @Test
    public void testCreateCookieFromOkHttp() {
        Duration maxAge = Duration.ofSeconds(0L);
        String url = "http://forest.dtflyx.com/docs";
        String setCookie = "foo=bar; max-age=" + maxAge.getSeconds();
        HttpUrl httpUrl = HttpUrl.parse(url);
        long currentTime = System.currentTimeMillis();
        Cookie okCookie = Cookie.parse(httpUrl, setCookie);
        ForestCookie cookie = new OkHttp3Cookie(currentTime, okCookie);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("foo");
        assertThat(cookie.getValue()).isEqualTo("bar");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(0);
        long expiresTime = cookie.getCreateTime().getTime() + cookie.getMaxAge().toMillis();
        assertThat(cookie.getExpiresTime()).isEqualTo(expiresTime);
        assertThat(cookie.getDomain()).isEqualTo("forest.dtflyx.com");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isPersistent()).isTrue();
        assertThat(cookie.isSecure()).isFalse();
        assertThat(cookie.isHostOnly()).isTrue();
        assertThat(cookie.isHttpOnly()).isFalse();
    }


    @Test
    public void testCreateCookieFromHttpclient() {
        Duration maxAge = Duration.ofSeconds(0L);
        long currentTime = System.currentTimeMillis();
        long expiresTime = currentTime + maxAge.toMillis();
        BasicClientCookie2 httpCookie = new BasicClientCookie2("foo", "bar");
        httpCookie.setDomain("forest.dtflyx.com");
        httpCookie.setExpiryDate(new Date(expiresTime));
        httpCookie.setPath("/");
        httpCookie.setSecure(false);
        httpCookie.setDiscard(true);
        ForestCookie cookie = new HttpclientCookie(httpCookie);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("foo");
        assertThat(cookie.getValue()).isEqualTo("bar");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(0);
        assertThat(cookie.getDomain()).isEqualTo("forest.dtflyx.com");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isPersistent()).isFalse();
        assertThat(cookie.isSecure()).isFalse();
    }

    @Test
    public void testCookies() {
        ForestCookies cookies = new ForestCookies();
    }

}
