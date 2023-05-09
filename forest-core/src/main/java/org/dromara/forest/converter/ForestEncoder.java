package org.dromara.forest.converter;

import org.dromara.forest.http.ForestBody;
import org.dromara.forest.http.ForestRequest;

import java.nio.charset.Charset;

/**
 * Forest 编码器
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-08 15:12
 */
public interface ForestEncoder {

    default String encodeToString(Object obj) {
        return "";
    }

    byte[] encodeRequestBody(final ForestBody body, final Charset charset, final ConvertOptions options);

    default byte[] encodeRequestBody(ForestBody body, Charset charset) {
        return encodeRequestBody(body, charset, ConvertOptions.defaultOptions());
    }

    default byte[] encodeRequestBody(ForestRequest request, Charset charset, ConvertOptions options) {
        return encodeRequestBody(request.body(), charset, options);
    }


    default byte[] encodeRequestBody(ForestRequest request, Charset charset) {
        return encodeRequestBody(request.body(), charset);
    }
}
