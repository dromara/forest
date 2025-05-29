package com.dtflys.forest.test.utils;

import com.dtflys.forest.utils.Base64Utils;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestBase64Utils {

    @Test
    public void testEncode() {
        String source = "hello world";
        String encoded = Base64Utils.encode(source);
        assertThat(encoded).isEqualTo("aGVsbG8gd29ybGQ=");
    }

    @Test
    public void testEncodeToByteArray() {
        String source = "hello world";
        byte[] bytes = Base64Utils.encodeToByteArray(source);
        String encoded = new String(bytes);
        assertThat(encoded).isEqualTo("aGVsbG8gd29ybGQ=");
    }


    @Test
    public void testDecode() {
        String source = "aGVsbG8gd29ybGQ=";
        byte[] bytes = Base64Utils.decode(source);
        String decoded = new String(bytes);
        assertThat(decoded).isEqualTo("hello world");
    }

}
