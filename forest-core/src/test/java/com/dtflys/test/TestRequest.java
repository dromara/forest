package com.dtflys.test;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestHostAddress;
import com.dtflys.forest.http.ForestRequest;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestRequest {

    @Test
    public void testUrl() {
        ForestRequest request = Forest.request();
        assertThat(request.getUrl()).isEqualTo("http://localhost/");
        assertThat(request.host()).isEqualTo("localhost");
        assertThat(request.port()).isEqualTo(80);
        assertThat(request.getPath()).isEqualTo("/");
        request.url("http://127.0.0.1:8080/xxx/yyy");
        assertThat(request.urlString()).isEqualTo("http://127.0.0.1:8080/xxx/yyy");
        assertThat(request.host()).isEqualTo("127.0.0.1");
        assertThat(request.port()).isEqualTo(8080);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");
    }

    @Test
    public void testHostAddress() {
        ForestRequest request = Forest.get("http://localhost/xxx/yyy");
        assertThat(request.host()).isEqualTo("localhost");
        assertThat(request.port()).isEqualTo(80);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");
        request.hostAddress(new ForestHostAddress("1.1.1.1", 10));
        assertThat(request.host()).isEqualTo("1.1.1.1");
        assertThat(request.port()).isEqualTo(10);

        assertThat(request.getPath()).isEqualTo("/xxx/yyy");
        request.hostAddress(new ForestHostAddress("2.2.2.2", -1));
        assertThat(request.host()).isEqualTo("2.2.2.2");
        assertThat(request.port()).isEqualTo(80);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");

        request.hostAddress("3.3.3.3", 8080);
        assertThat(request.host()).isEqualTo("3.3.3.3");
        assertThat(request.port()).isEqualTo(8080);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");

        request.hostAddress("4.4.4.4", -1);
        assertThat(request.host()).isEqualTo("4.4.4.4");
        assertThat(request.port()).isEqualTo(80);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");
    }

}
