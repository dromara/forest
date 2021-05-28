package com.dtflys.forest.utils;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * GZIP工具类
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.5.1
 */
public class GzipUtils {

    /**
     * 判断是否为gzip
     * <p>根据Response传回的Content-Encoding来判断是否为gzip压缩的内容</p>
     *
     * @param contentEncoding
     * @return {@code true}为gzip压缩内容, 否则不是
     */
    public static boolean isGzip(String contentEncoding) {
        if (contentEncoding != null) {
            String[] encodes = contentEncoding.split(",");
            if (encodes.length > 0) {
                for (String encode : encodes) {
                    if ("gzip".equalsIgnoreCase(encode.trim())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 将InputStream转换为GZIPInputStream
     *
     * @param in {@link InputStream}实例
     * @return {@link GZIPInputStream}实例
     * @throws IOException IO异常
     */
    public static GZIPInputStream decompressGzipInputStream(InputStream in) throws IOException {
        GZIPInputStream gzipInputStream = new GZIPInputStream(in);
        return gzipInputStream;
    }

    /**
     * 将GZIP输入流解压为字节数组
     *
     * @param gzipIn {@link GZIPInputStream}实例
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] decompressGzipToByteArray(GZIPInputStream gzipIn) throws IOException {
        return IOUtils.toByteArray(gzipIn);
    }

    /**
     * 将GZIP输入流解压为字符串
     *
     * @param gzipIn {@link GZIPInputStream}实例
     * @param encoding 字符串编码
     * @return 字符串
     * @throws IOException IO异常
     */
    public static String decompressGzipToString(GZIPInputStream gzipIn, String encoding) throws IOException {
        return IOUtils.toString(gzipIn, encoding);
    }

    /**
     * 将GZIP输入流解压为字节数组
     *
     * @param in {@link InputStream}实例
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] decompressGzipToByteArray(InputStream in) throws IOException {
        return IOUtils.toByteArray(decompressGzipInputStream(in));
    }

    /**
     * 将GZIP输入流解压为字符串
     *
     * @param in {@link InputStream}实例
     * @param encoding 字符串编码
     * @return 字符串
     * @throws IOException IO异常
     */
    public static String decompressGzipToString(InputStream in, String encoding) throws IOException {
        return IOUtils.toString(decompressGzipInputStream(in), encoding);
    }

    /**
     * 将GZIP输入流解压为字符串
     *
     * @param bytes 字节数组
     * @param encoding 字符串编码
     * @return 字符串
     * @throws IOException IO异常
     */
    public static String decompressGzipToString(byte[] bytes, String encoding) throws IOException {
        return IOUtils.toString(decompressGzipInputStream(new ByteArrayInputStream(bytes)), encoding);
    }


}
