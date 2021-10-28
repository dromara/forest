package com.dtflys.forest.converter;

import com.dtflys.forest.http.ForestBody;

import java.nio.charset.Charset;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-08 15:12
 */
public interface ForestEncoder {

    String encodeToString(Object obj);

    byte[] encodeRequestBody(ForestBody body, Charset charset);
}
