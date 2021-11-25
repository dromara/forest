package com.dtflys.forest.converter;

import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.ForestDataType;

import java.nio.charset.Charset;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-08 15:12
 */
public interface ForestEncoder {

    default String encodeToString(Object obj) {
        return "";
    }

    default byte[] encodeRequestBody(ForestBody body, Charset charset) {
        return new byte[0];
    }

    default byte[] encodeRequestBody(ForestRequest request, Charset charset) {
        return encodeRequestBody(request.body(), charset);
    }
}
