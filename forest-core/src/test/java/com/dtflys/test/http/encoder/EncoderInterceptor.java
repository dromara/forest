package com.dtflys.test.http.encoder;

import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.Interceptor;

import java.nio.charset.StandardCharsets;

public class EncoderInterceptor implements Interceptor<Object> {

    @Override
    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
        String str = new String(encodedData, StandardCharsets.UTF_8);
        return (str + ":encoded").getBytes(StandardCharsets.UTF_8);
    }
}
