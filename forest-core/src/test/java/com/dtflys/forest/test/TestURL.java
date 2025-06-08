package com.dtflys.forest.test;

import com.dtflys.forest.Forest;
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



}
