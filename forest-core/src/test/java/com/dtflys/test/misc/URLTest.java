package com.dtflys.test.misc;

import com.dtflys.forest.http.SimpleForestURL;
import com.dtflys.forest.utils.URLUtils;
import junit.framework.TestCase;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class URLTest extends TestCase {

    public void testUrl() {
        assertEquals("http://www.baidu.com", URLUtils.getValidURL("http://www.baidu.com", ""));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("http://www.baidu.com", "xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("http://www.baidu.com/", "xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("http://www.baidu.com/", "/xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("http://www.baidu.com", "/xxx"));

        assertEquals("http://www.baidu.com", URLUtils.getValidURL("www.baidu.com", ""));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("www.baidu.com", "xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("www.baidu.com/", "xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("www.baidu.com/", "/xxx"));
        assertEquals("http://www.baidu.com/xxx", URLUtils.getValidURL("www.baidu.com", "/xxx"));


        assertEquals("http://www.baidu.com", URLUtils.getValidURL("http://www.baidu.com", "http://www.baidu.com"));
        assertEquals("http://www.baidu.com", URLUtils.getValidURL("www.baidu.com", "http://www.baidu.com"));
        assertEquals("http://www.baidu.com", URLUtils.getValidURL("google.com", "http://www.baidu.com"));
        assertEquals("http://www.baidu.com", URLUtils.getValidURL("xxx", "http://www.baidu.com"));
    }

    @Test
    public void testForestURL() throws MalformedURLException {
        SimpleForestURL url = new SimpleForestURL(new URL("http://localhost:8080/xxx/yyy"));
        assertThat(url.getScheme()).isEqualTo("http");
        assertThat(url.isSSL()).isFalse();
        assertThat(url.getHost()).isEqualTo("localhost");
        assertThat(url.getPort()).isEqualTo(8080);
        assertThat(url.getAuthority()).isEqualTo("localhost:8080");
        assertThat(url.getPath()).isEqualTo("/xxx/yyy");
        assertThat(url.toString()).isEqualTo("http://localhost:8080/xxx/yyy");

        assertThat(url.setScheme("https").getScheme()).isEqualTo("https");
        assertThat(url.isSSL()).isTrue();
        assertThat(url.setHost("127.0.0.1").getHost()).isEqualTo("127.0.0.1");
        assertThat(url.setPort(8888).getPort()).isEqualTo(8888);
        assertThat(url.setUserInfo("username:pwd").getUserInfo()).isEqualTo("username:pwd");
        assertThat(url.setPath("zzz").getPath()).isEqualTo("/zzz");
        assertThat(url.toString()).isEqualTo("https://username:pwd@127.0.0.1:8888/zzz");
    }
}
