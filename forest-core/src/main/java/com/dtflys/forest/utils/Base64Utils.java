package com.dtflys.forest.utils;

import org.apache.xerces.impl.dv.util.Base64;
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
		return Base64.encode(data);
	}

	public static byte[] simpleDecode(String str) {
		return Base64.decode(str);
	}

    public static String encode(String str){
		return Base64.encode(str.getBytes());
    }

    public static String decode(String str){
		return new String(Base64.decode(str));
    }

}
