package com.dtflys.forest.test.misc;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.util.DateUtils;
import com.dtflys.forest.Forest;
import com.dtflys.forest.backend.httpclient.HttpclientCookie;
import com.dtflys.forest.backend.okhttp3.OkHttp3Cookie;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.cookie.ForestCookieStorage;
import com.dtflys.forest.http.cookie.MemoryCookieStorage;
import com.dtflys.forest.test.http.BaseClientTest;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.junit.Rule;
import org.junit.Test;

import javax.mail.Store;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CookieTest extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"1\", \"data\":\"2\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    public CookieTest(String backendName, String jsonConverterName) {
        super(backendName, jsonConverterName, ForestConfiguration.createConfiguration());
    }

    @Test
    public void toStringAndParse() {
        ForestCookie cookie1 = Forest.cookie("Foo", "Bar")
                .setDomain("forest.dtflyx.com")
                .setPath("/")
                .setMaxAge(10000)
                .setSecure(true);
        String cookieString = cookie1.toString();
        ForestCookie cookie1back = ForestCookie.parse(cookieString);

        assertThat(cookie1back).isNotNull();
        assertThat(cookie1back.toString()).isEqualTo(cookieString);
        assertThat(cookie1back.getName()).isEqualTo("Foo");
        assertThat(cookie1back.getValue()).isEqualTo("Bar");
        assertThat(cookie1back.getDomain()).isEqualTo("forest.dtflyx.com");
        assertThat(cookie1back.getPath()).isEqualTo("/");
        assertThat(cookie1back.isPersistent()).isEqualTo(true);
        assertThat(cookie1back.getMaxAge()).isNotNull().isEqualTo(Duration.ofMillis(10000));
        assertThat(cookie1back.isSecure()).isTrue();
    }


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
        assertThat(cookie.toString()).isEqualTo("foo=bar; Path=/; Secure");
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
                .isEqualTo("foo=bar; Max-Age=0; Domain=localhost; Path=/; HttpOnly");

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
                .isEqualTo("foo=bar; Max-Age=1; Domain=localhost; Path=/");
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
    public void testParseCookie() throws ParseException {
        Duration maxAge = Duration.ofSeconds(1L);
        String url = "http://forest.dtflyx.com/docs";

        // test parse Set-Cookie just include name-value and max-age

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

        // test parse Set-Cookie just include max-age and domain

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

        // test parse Set-Cookie including both Max-Age and Expires

        setCookie = "FOO=bar-123-abc; Max-Age=2592000; Expires=Thu, 26 Jun 2025 11:10:51 GMT; Domain=dtflyx.com; Path=/; Secure;HttpOnly;Domain=forest.dtflyx.com";
        cookie = ForestCookie.parse(url, setCookie);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("FOO");
        assertThat(cookie.getValue()).isEqualTo("bar-123-abc");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(2592000);
        expiresTime = cookie.getCreateTime().getTime() + Duration.ofSeconds(2592000).toMillis();
        assertThat(cookie.getExpiresTime()).isEqualTo(expiresTime);
        assertThat(cookie.getDomain()).isEqualTo("forest.dtflyx.com");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isPersistent()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.isHostOnly()).isFalse();
        assertThat(cookie.isHttpOnly()).isTrue();

        // test parse Set-Cookie including Expires

        final long currentTime = System.currentTimeMillis();
        final long expireAt = currentTime + 2593000;
        final Date expires = new Date(expireAt);
        DateFormat expiresFormat = new SimpleDateFormat(DatePattern.HTTP_DATETIME_PATTERN, Locale.ENGLISH);
        final String expiresStr = expiresFormat.format(expires);
        final Date dateAfterDecode = expiresFormat.parse(expiresStr);
        final long expireAtAfterDecode = dateAfterDecode.getTime();
        final long maxAgeAfterDecode = expireAtAfterDecode - currentTime;

        setCookie = "FOO=bar-123-abc; Expires=" + expiresStr + "; Domain=dtflyx.com; Path=/; Secure;HttpOnly;Domain=forest.dtflyx.com";
        cookie = ForestCookie.parse(url, currentTime, setCookie);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("FOO");
        assertThat(cookie.getValue()).isEqualTo("bar-123-abc");
        assertThat(cookie.getMaxAge().toMillis()).isEqualTo(maxAgeAfterDecode);
        expiresTime = cookie.getCreateTime().getTime() + maxAgeAfterDecode;
        assertThat(cookie.getExpiresTime()).isEqualTo(expiresTime);
        assertThat(cookie.getExpires().getTime()).isEqualTo(expiresTime);
        assertThat(cookie.getDomain()).isEqualTo("forest.dtflyx.com");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isPersistent()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.isHostOnly()).isFalse();
        assertThat(cookie.isHttpOnly()).isTrue();

        // test parse Set-Cookie including Version

        setCookie = "FOO=bar-123-abc; Max-Age=2592000; Secure; Version=2; Domain=forest.dtflyx.com";
        cookie = ForestCookie.parse(url, setCookie);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("FOO");
        assertThat(cookie.getValue()).isEqualTo("bar-123-abc");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(2592000);
        expiresTime = cookie.getCreateTime().getTime() + Duration.ofSeconds(2592000).toMillis();
        assertThat(cookie.getExpiresTime()).isEqualTo(expiresTime);
        assertThat(cookie.getDomain()).isEqualTo("forest.dtflyx.com");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getVersion()).isEqualTo(2);
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
    public void testRequestCookies1() {
        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .setResponseCode(200));

        Forest.get("/")
                .host(server.getHostName())
                .port(server.getPort())
                .addHeader("Cookie", "FOO=123-abc; BAR=789-xyz")
                .execute();

        mockRequest(server)
                .assertHeaderEquals("Cookie", "FOO=123-abc; BAR=789-xyz");
    }


    @Test
    public void testRequestCookies2() {
        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .setResponseCode(200));

        Forest.get("/")
                .host(server.getHostName())
                .port(server.getPort())
                .addCookie(Forest.cookie("FOO", "123-abc"))
                .addCookie(Forest.cookie("BAR", "789-xyz"))
                .execute();

        mockRequest(server)
                .assertHeaderEquals("Cookie", "FOO=123-abc; BAR=789-xyz");
    }


    @Test
    public void testRequestCookies3() {
        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .setResponseCode(200));

        ForestCookie cookie1 = ForestCookie.parse(
                "XXX=YYY; Max-Age=2592000; Path=/; Version=1; Domain=" + server.getHostName());

        ForestCookie cookie2 = ForestCookie.parse(
                "OK=NO; Max-Age=2592000; Path=/abc; Version=1; Domain=" + server.getHostName());

        ForestCookie cookie3 = ForestCookie.parse(
                "A=1; Max-Age=2592000; Path=/; Version=1; Domain=www.dtflyx.com");

        ForestCookie cookie4 = ForestCookie.parse(
                "B=2; Max-Age=2592000; Path=/; Version=1; Secure; Domain=" + server.getHostName());

        Forest.get("/")
                .host(server.getHostName())
                .port(server.getPort())
                .addCookie("FOO", "123-abc")
                .addCookie("BAR", "789-xyz")
                .addCookie(cookie1, cookie2, cookie3, cookie4)
                .execute();

        mockRequest(server)
                .assertHeaderEquals("Cookie", "FOO=123-abc; BAR=789-xyz; XXX=YYY");
    }

    @Test
    public void testRequestCookies4() {
        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .setResponseCode(200));

        Forest.get("/")
                .host(server.getHostName())
                .port(server.getPort())
                .addCookie("FOO=123-abc; BAR=789-xyz")
                .execute();

        mockRequest(server)
                .assertHeaderEquals("Cookie", "FOO=123-abc; BAR=789-xyz");
    }



    @Test
    public void testResponseCookies() {
        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .addHeader("Set-Cookie", "FOO=123-abc; Max-Age=2592000; Path=/abc; Secure; Version=1; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "BAR=789-xyz; Max-Age=2592000; Secure; HttpOnly; Version=2; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "A=1; Max-Age=2592000; Comment=XXXX; Secure; HttpOnly; Version=3")
                .setResponseCode(200));

        ForestResponse<?> response = Forest.get("/abc/xxx")
                .logResponseHeaders(true)
                .host(server.getHostName())
                .port(server.getPort())
                .executeAsResponse();

        assertThat(response).isNotNull();
        List<ForestCookie> cookies = response.getCookies();
        assertThat(cookies).isNotNull();
        assertThat(cookies.size()).isEqualTo(3);

        ForestCookie cookieFoo = response.getCookie("FOO");
        assertThat(cookieFoo).isNotNull().isEqualTo(cookies.stream()
                .filter(cookie -> cookie.getName().equals("FOO")).findFirst().orElse(null));

        assertThat(cookieFoo.getValue()).isEqualTo("123-abc");
        assertThat(cookieFoo.getMaxAge().getSeconds()).isEqualTo(2592000);
        assertThat(cookieFoo.getDomain()).isEqualTo(server.getHostName());
        assertThat(cookieFoo.getPath()).isEqualTo("/abc");
        assertThat(cookieFoo.isPersistent()).isTrue();
        assertThat(cookieFoo.isSecure()).isTrue();
        assertThat(cookieFoo.isHostOnly()).isFalse();
        assertThat(cookieFoo.getVersion()).isEqualTo(1);

        ForestCookie cookieBar = response.getCookie("BAR");
        assertThat(cookieBar).isNotNull().isEqualTo(cookies.stream()
                .filter(cookie -> cookie.getName().equals("BAR")).findFirst().orElse(null));
        assertThat(cookieBar.getValue()).isEqualTo("789-xyz");
        assertThat(cookieBar.getMaxAge().getSeconds()).isEqualTo(2592000);
        assertThat(cookieBar.getDomain()).isEqualTo(server.getHostName());
        assertThat(cookieBar.getPath()).isEqualTo("/");
        assertThat(cookieBar.isPersistent()).isTrue();
        assertThat(cookieBar.isSecure()).isTrue();
        assertThat(cookieBar.isHttpOnly()).isTrue();
        assertThat(cookieBar.getVersion()).isEqualTo(2);

        ForestCookie cookieA = response.getCookie("A");
        assertThat(cookieA).isNotNull().isEqualTo(cookies.stream()
                .filter(cookie -> cookie.getName().equals("A")).findFirst().orElse(null));
        assertThat(cookieA.getValue()).isEqualTo("1");
        assertThat(cookieA.getMaxAge().getSeconds()).isEqualTo(2592000);
        assertThat(cookieA.getDomain()).isEqualTo(server.getHostName());
        assertThat(cookieA.getPath()).isEqualTo("/");
        assertThat(cookieA.getComment()).isEqualTo("XXXX");
        assertThat(cookieA.isPersistent()).isTrue();
        assertThat(cookieA.isSecure()).isTrue();
        assertThat(cookieA.isHttpOnly()).isTrue();
        assertThat(cookieA.getVersion()).isEqualTo(3);
    }
    
    
    @Test
    public void testCookieStorage() {
        ForestCookieStorage storage = new MemoryCookieStorage(128);

        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .addHeader("Set-Cookie", "FOO=123-abc.111; Max-Age=2592000; Path=/abc; Secure; Version=1; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "BAR=789-xyz.222; Max-Age=2592000; Secure; HttpOnly; Version=2; Domain=" + server.getHostName())
                .setResponseCode(200));
        
        ForestResponse response = Forest.get("/")
                .logResponseHeaders(true)
                .host(server.getHostName())
                .port(server.getPort())
                .executeAsResponse();
        
        assertThat(response).isNotNull();
        List<ForestCookie> cookies = response.getCookies();

        storage.save(new ForestCookies(cookies));
        
        ForestRequest request = Forest.get("/abc")
                .scheme("https")
                .host(server.getHostName())
                .port(server.getPort());
        
        ForestCookies cookieList = storage.load(request.url());
        assertThat(cookieList).isNotNull();
        assertThat(cookieList.size()).isEqualTo(2);
        
        List<ForestCookie> cookies1 = cookieList.getCookies(server.getHostName(), "/abc", "FOO");
        assertThat(cookies1).isNotNull();
        assertThat(cookies1.size()).isEqualTo(1);
        assertThat(cookies1.get(0)).isEqualTo(response.getCookie("FOO"));
        assertThat(cookies1.get(0).getValue()).isEqualTo("123-abc.111");

        List<ForestCookie> cookies2 = cookieList.getCookies(server.getHostName(), "/", "BAR");
        assertThat(cookies2).isNotNull();
        assertThat(cookies2.size()).isEqualTo(1);
        assertThat(cookies2.get(0)).isEqualTo(response.getCookie("BAR"));
        assertThat(cookies2.get(0).getValue()).isEqualTo("789-xyz.222");
    }


    @Test
    public void testCookieAutoSaveAndLoad() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration()
                .setCookiesStorageMaxSize(16)
                .setAutoCookieSaveAndLoadEnabled(true);

        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .addHeader("Set-Cookie", "FOO=123-abc.111; Max-Age=2592000; Path=/abc; Version=1; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "BAR=789-xyz.222; Max-Age=2592000; HttpOnly; Version=2; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "XXX=111; Max-Age=2592000; Path=/aaa; HttpOnly; Version=2; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "YYY=222; Max-Age=2592000; Path=/abc/yyy; HttpOnly; Version=2; Domain=" + server.getHostName())
                .setResponseCode(200));

        ForestResponse response = configuration.get("/")
                .logResponseHeaders(true)
                .host(server.getHostName())
                .port(server.getPort())
                .executeAsResponse();

        mockRequest(server)
                .assertHeaderEquals("Cookie", null);

        assertThat(response).isNotNull();
        assertThat(response.getCookies().size()).isEqualTo(4);
        assertThat(response.getCookie("FOO").getValue()).isEqualTo("123-abc.111");
        assertThat(response.getCookie("BAR").getValue()).isEqualTo("789-xyz.222");

        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .setResponseCode(200));

        configuration.get("/abc/yyy")
                .host(server.getHostName())
                .port(server.getPort())
                .execute();
        
        mockRequest(server)
                .assertHeaderEquals("Cookie", "FOO=123-abc.111; BAR=789-xyz.222; YYY=222");
    }


}
