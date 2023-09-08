package org.dromara.forest.utils;

import org.apache.commons.io.IOUtils;
import org.dromara.forest.exceptions.ForestRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
     * @param contentEncoding 内容编码
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
     * 压缩字节数组数据
     *
     * @param bytes 字节数组数据
     * @return 压缩过的字节数组
     */
    public static byte[] compressGzip(byte[] bytes) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream outputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.finish();
        } catch (Throwable th) {
            throw new ForestRuntimeException(th);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 压缩字符串数据
     *
     * @param text 字符串数据
     * @param charset 字符集
     * @return 压缩过的字节数组
     */
    public static byte[] compressGzip(String text, Charset charset) {
        return compressGzip(text.getBytes(charset));
    }

    /**
     * 压缩字符串数据
     *
     * @param text 字符串数据
     * @return 压缩过的字节数组
     */
    public static byte[] compressGzip(String text) {
        return compressGzip(text.getBytes(StandardCharsets.UTF_8));
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
