package com.dtflys.forest.test;

import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.util.TypeUtils;
import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.test.model.Contact;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestRequest {

    @Test
    public void testUrl() {
        ForestRequest request = Forest.request();
        assertThat(request.getUrl()).isEqualTo("/");
        assertThat(request.host()).isNull();
        assertThat(request.port()).isEqualTo(80);
        assertThat(request.getPath()).isEqualTo("/");
        request.url("http://127.0.0.1:8080/xxx/yyy");
        assertThat(request.urlString()).isEqualTo("http://127.0.0.1:8080/xxx/yyy");
        assertThat(request.host()).isEqualTo("127.0.0.1");
        assertThat(request.port()).isEqualTo(8080);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");
    }

    @Test
    public void testJson2() {
        Contact contact = new Contact();
        contact.setName("Marry");
        contact.setAge(20);
        contact.setPhone("88888888");

        Map<String, Object> map = TypeUtils.cast(contact, new TypeReference<LinkedHashMap<String, Object>>() {}.getType());
        System.out.println(map);

        map = TypeUtils.cast(map, new TypeReference<LinkedHashMap<String, Object>>() {}.getType());
        System.out.println(map);
    }

    @Test
    public void testAddress() {
        ForestRequest request = Forest.get("http://localhost/xxx/yyy");
        assertThat(request.host()).isEqualTo("localhost");
        assertThat(request.port()).isEqualTo(80);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");
        request.address(new ForestAddress("1.1.1.1", 10));
        assertThat(request.host()).isEqualTo("1.1.1.1");
        assertThat(request.port()).isEqualTo(10);

        assertThat(request.getPath()).isEqualTo("/xxx/yyy");
        request.address(new ForestAddress("2.2.2.2", -1));
        assertThat(request.host()).isEqualTo("2.2.2.2");
        assertThat(request.port()).isEqualTo(10);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");

        request.address("3.3.3.3", 8080);
        assertThat(request.host()).isEqualTo("3.3.3.3");
        assertThat(request.port()).isEqualTo(8080);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");

        request.address("4.4.4.4", -1);
        assertThat(request.host()).isEqualTo("4.4.4.4");
        assertThat(request.port()).isEqualTo(8080);
        assertThat(request.getPath()).isEqualTo("/xxx/yyy");
    }

}
