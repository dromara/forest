package org.dromara.forest.utils;

import org.dromara.forest.exceptions.ForestRuntimeException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

public class URLEncoder {

    /**
     * 空格字符
     */
    private static final char SPACE = ' ';

    /**
     * 十六进制字符的大写字符数组
     */
    private static final char[] HEX_DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * URI用户信息部分中不会被编码的字符集
     */
    private static final char[] USER_INFO_EXCLUDED_CHARACTERS = {'-', '.', '_', '+', '!', '(', ')', '*', ':', '=', '%'};

    /**
     * URI路径中不会被编码的字符集
     */
    private static final char[] PATH_EXCLUDED_CHARACTERS = {'-', '.', '_', '+', '!', '(', ')', '[', ']', '*', '/', ':', '?', '=', '$', '@', '&', '%', '~'};

    /**
     * 查询参数值中不会被编码的字符集
     */
    private static final char[] QUERY_VALUE_EXCLUDED_CHARACTERS = {'-', '.', '_', '+', '!', '(', ')', '[', ']', ',', '*', '/', ':', '?', '=', '%', '~'};

    /**
     * (带不转义大括号的) 查询参数值中不会被编码的字符集
     */
    private static final char[] QUERY_VALUE_EXCLUDED_CHARACTERS_WITH_BRACE = {'-', '.', '_', '+', '!', '{', '}', '(', ')', '[', ']', ',', '*', '/', ':', '?', '=', '%', '~'};


    /**
     * 查询参数值中不会被编码的字符集
     */
    private static final char[] X_WWW_FORM_URLENCODED_VALUE_EXCLUDED_CHARACTERS = {'-', '.', '_', '!', '{', '}', '[', ']', ',', '"', '*', '/', ':', '?', '#', '=', '%'};

    /**
     * 强制全编码中不会被编码的字符集
     */
    private static final char[] ALL_EXCLUDED_CHARACTERS = {'*', '-', '.', '_'};

    /**
     * 用于用户验证信息的编码{@link URLEncoder}
     */
    public static final URLEncoder USER_INFO = createUserInfoUrlEncoder();

    /**
     * 用于URI路径部分的编码{@link URLEncoder}
     */
    public static final URLEncoder PATH = createPathUrlEncoder();

    /**
     * 用于查询参数值部分的编码{@link URLEncoder}
     */
    public static final URLEncoder QUERY_VALUE = createQueryValueUrlEncoder();

    /**
     * 用于 (带不转义大括号的) 查询参数值部分的编码{@link URLEncoder}
     */
    public static final URLEncoder QUERY_VALUE_WITH_BRACE = createQueryValueWithBraceUrlEncoder();


    /**
     * 用于表单参数值部分的编码{@link URLEncoder}
     */
    public static final URLEncoder FORM_VALUE = createXWwwFormUrlEncodedValueUrlEncoder();


    /**
     * 强制全编码的编码{@link URLEncoder}
     */
    public static final URLEncoder ALL = createAllUrlEncoder();


    private static URLEncoder createURLEncoder(final char[] excludedCharacters, final boolean encodeSpaceAsPlus) {
        final URLEncoder encoder = new URLEncoder();
        encoder.setEncodeSpaceAsPlus(encodeSpaceAsPlus);
        final int len = excludedCharacters.length;
        for (int i = 0; i < len; i++) {
            final char ch = excludedCharacters[i];
            encoder.excludeCharacter(ch);
        }
        return encoder;
    }

    /**
     * 创建用于用户验证信息编码的{@link URLEncoder}
     *
     * @return {@link URLEncoder}实例
     */
    public static URLEncoder createUserInfoUrlEncoder() {
        return createURLEncoder(USER_INFO_EXCLUDED_CHARACTERS, false);
    }


    /**
     * 创建用于URI路径编码的{@link URLEncoder}
     *
     * @return {@link URLEncoder}实例
     */
    public static URLEncoder createPathUrlEncoder() {
        return createURLEncoder(PATH_EXCLUDED_CHARACTERS, false);
    }

    /**
     * 创建用于查询参数值编码的{@link URLEncoder}
     *
     * @return {@link URLEncoder}实例
     */
    public static URLEncoder createQueryValueUrlEncoder() {
        return createURLEncoder(QUERY_VALUE_EXCLUDED_CHARACTERS, false);
    }

    /**
     * 创建用于 (带不转义大括号的) 查询参数值编码的{@link URLEncoder}
     *
     * @return {@link URLEncoder}实例
     */
    public static URLEncoder createQueryValueWithBraceUrlEncoder() {
        return createURLEncoder(QUERY_VALUE_EXCLUDED_CHARACTERS_WITH_BRACE, false);
    }


    /**
     * 创建用于表单参数值编码的{@link URLEncoder}
     *
     * @return {@link URLEncoder}实例
     */
    public static URLEncoder createXWwwFormUrlEncodedValueUrlEncoder() {
        return createURLEncoder(X_WWW_FORM_URLENCODED_VALUE_EXCLUDED_CHARACTERS, false);
    }


    /**
     * 创建用于强制编码的{@link URLEncoder}
     *
     * @return {@link URLEncoder}实例
     */
    public static URLEncoder createAllUrlEncoder() {
        return createURLEncoder(ALL_EXCLUDED_CHARACTERS, false);
    }


    /**
     * 安全编码字符集
     */
    private final BitSet excludedCharacters;

    /**
     * 是否将空格编码为+
     */
    private boolean encodeSpaceAsPlus = false;

    /**
     * {@link URLEncoder}构造函数
     */
    public URLEncoder() {
        this(new BitSet(256));
        // 排除所有小写英文字母
        for (char i = 'a'; i <= 'z'; i++) {
            excludeCharacter(i);
        }
        // 排除所有大写英文字母
        for (char i = 'A'; i <= 'Z'; i++) {
            excludeCharacter(i);
        }
        // 排除所有数字
        for (char i = '0'; i <= '9'; i++) {
            excludeCharacter(i);
        }
    }

    /**
     * URLEncoder构造函数
     *
     * @param excludedCharacters 安全字符，安全字符不被编码
     */
    private URLEncoder(final BitSet excludedCharacters) {
        this.excludedCharacters = excludedCharacters;
    }

    /**
     * 排除不被不被编码的字符
     *
     * @param c 字符
     */
    public void excludeCharacter(final char c) {
        excludedCharacters.set(c);
    }

    /**
     * 是否将空格编码为+
     *
     * @param encodeSpaceAsPlus 是否将空格编码为+
     */
    public void setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
        this.encodeSpaceAsPlus = encodeSpaceAsPlus;
    }

    public String encode(final String path, final String charset) {
        if (path == null) {
            return null;
        }
        Charset cs = null;
        if (StringUtils.isEmpty(charset)) {
            cs = StandardCharsets.UTF_8;
        } else {
            try {
                cs = Charset.forName(charset);
            } catch (Throwable th) {
                throw new ForestRuntimeException(th);
            }
        }
        return encode(path, cs);
    }

    private boolean isURLEncoded(final char[] charArray, final int index) {
        if (charArray[index] != '%') {
            return false;
        }
        final int len = charArray.length;
        if (index + 2 < len) {
            final char ch1 = charArray[index + 1];
            final char ch2 = charArray[index + 2];
            return Character.isDigit(ch1) && Character.isDigit(ch2);
        }
        return false;
    }

    /**
     * 将URL中的字符串编码为%形式
     *
     * @param path    需要编码的字符串
     * @param charset 编码
     * @return 编码后的字符串
     */
    public String encode(final String path, final Charset charset) {
        final StringBuilder builder = new StringBuilder(path.length());
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(buf, charset);
        final char[] charArray = path.toCharArray();
        final int len = charArray.length;

        for (int i = 0; i < len; i++) {
            final char ch = charArray[i];
            if (isURLEncoded(charArray, i)) {
                builder.append(ch);
            } else if (excludedCharacters.get(ch)) {
                builder.append(ch);
            } else if (encodeSpaceAsPlus && ch == SPACE) {
                // 处理空格为加号+
                builder.append('+');
            } else {
                try {
                    writer.write(ch);
                    writer.flush();
                } catch (IOException e) {
                    buf.reset();
                    continue;
                }

                final byte[] ba = buf.toByteArray();
                for (final byte toEncode : ba) {
                    builder.append('%');
                    final int high = (toEncode & 0xf0) >>> 4;//高位
                    final int low = toEncode & 0x0f;//低位
                    builder.append(HEX_DIGITS_UPPER[high]);
                    builder.append(HEX_DIGITS_UPPER[low]);
                }
                buf.reset();
            }
        }
        return builder.toString();
    }

}
