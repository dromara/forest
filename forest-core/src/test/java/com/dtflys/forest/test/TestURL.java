package com.dtflys.forest.test;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestDynamicURL;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestURL;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestURL {


    @Test
    public void testURLCreate() {
        ForestURL url = ForestURL.create("https://localhost/xxx?a=1&b=2");
        assertThat(url).isNotNull();
        assertThat(url.getHost()).isEqualTo("localhost");
    }

    @Test
    public void testURLParse() {
        ForestURL url = ForestURL.parse("https://localhost/xxx?a=1&b=2");
        assertThat(url).isNotNull();
    }


    @Test
    public void testURLinRequest() {
        ForestRequest request = Forest.get("/")
                .setVariable("a", "1")
                .setVariable("b", "2")
                .setUrl("http://localhost/{a}/{b}");
        assertThat(request).isNotNull();
        assertThat(request.urlString()).isEqualTo("http://localhost/1/2");
    }


    @Test
    public void testDynamicURL() {
        ForestRequest request = Forest.get("http://localhost:{port}/{path}")
                .setVariable("port", 8080)
                .setVariable("path", "xxx");

        assertThat(request).isNotNull();
        ForestURL url = request.url();
        assertThat(url).isNotNull().isInstanceOf(ForestDynamicURL.class);
        assertThat(url.getPort()).isEqualTo(8080);
        assertThat(url.getPath()).isEqualTo("/xxx");
    }

    @Test
    public void testDynamicURL2() {
        ForestRequest request = Forest.get("http://localhost:{port}/{path}?a={a}&b={b}")
                .setVariable("port", 8080)
                .setVariable("path", "xxx")
                .setVariable("a", "1")
                .setVariable("b", "2");

        assertThat(request).isNotNull();
        ForestURL url = request.url();
        assertThat(url).isNotNull().isInstanceOf(ForestDynamicURL.class);
        assertThat(url.getPort()).isEqualTo(8080);
        assertThat(url.getPath()).isEqualTo("/xxx");
        assertThat(request.getQuery("a")).isEqualTo("1");
        assertThat(request.getQuery("b")).isEqualTo("2");
    }

}
