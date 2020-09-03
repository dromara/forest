package com.dtflys.forest.utils;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * 通过字符串字节数组获得字符串编码名称
 *
 * @author HouKunLin
 * @date 2020/9/3 0003 10:21
 * @see <a href="https://blog.csdn.net/qq_31384551/article/details/81627840">参考资料</a>
 */
public class GetByteEncode {
    /**
     * 默认的编码名称
     */
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * 通过字符串字节数组获得字符串编码名称
     *
     * @param bytes 字节数组
     * @return 编码名称
     */
    public static String getCharsetName(byte[] bytes) {
        return getCharsetName(bytes, DEFAULT_ENCODING);
    }

    /**
     * 通过字符串字节数组获得字符串编码名称
     *
     * @param bytes              字节数组
     * @param defaultCharsetName 默认的编码名称
     * @return 编码名称
     */
    public static String getCharsetName(byte[] bytes, String defaultCharsetName) {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null) {
            return defaultCharsetName;
        }
        return encoding;
    }

}
