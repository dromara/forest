package com.dtflys.forest.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;


/**
 * Base64字符串与字节码转换工具
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-04 19:05
 */

public class Base64Utils {

	public static String simpleEncode(byte[] data) {
		return new BASE64Encoder().encode(data);
	}

	public static byte[] simpleDecode(String str) {
		try {
			return new BASE64Decoder().decodeBuffer(str);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

    public static String encode(String str){
    	return new BASE64Encoder().encode(str.getBytes());
    }

    public static String decode(String str){
    	try {
			return new String(new BASE64Decoder().decodeBuffer(str));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    }

}
