package com.dtflys.forest.utils;


import java.util.Base64;

/**
 * Base64字符串与字节码转换工具
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-04 19:05
 */

public class Base64Utils {

	private final static Base64.Decoder DECODER = Base64.getDecoder();
	private final static Base64.Encoder ENCODER = Base64.getEncoder();


    public static String encode(String str) {
		return ENCODER.encodeToString(str.getBytes());
    }

    public static byte[] decode(String str) {
		return DECODER.decode(str);
    }

}
