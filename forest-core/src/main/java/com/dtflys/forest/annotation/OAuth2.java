package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.authorization.OAuth2LifeCycle;
import com.dtflys.forest.utils.StringUtils;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * OAuth2 请求。自动获取Token、刷新Token，目前只支持 PASSWORD、CLIENT_CREDENTIALS 这样的无回调、无交互的类型。
 *
 * @author HouKunLin
 * @date 2020-11-23 22:43:55
 */
@Documented
@MethodLifeCycle(OAuth2LifeCycle.class)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface OAuth2 {
    /**
     * 请求Token的URL地址
     *
     * @return
     */
    @Nonnull
    String tokenUri();

    /**
     * 客户端ID
     *
     * @return
     */
    @Nonnull
    String clientId();

    /**
     * 客户端Secret
     *
     * @return
     */
    @Nonnull
    String clientSecret();

    /**
     * 类型
     *
     * @return
     */
    @Nonnull
    GrantType grantType();

    /**
     * 范围
     *
     * @return
     */
    String scope() default "";

    /**
     * 在 expires_in 剩余多少秒进行刷新。默认：600秒5分钟。
     * 不会主动触发刷新，只有在Token有效期最后5分钟内有请求的时候才会触发刷新。
     * 假如刷新失败将一直使用旧的Token，直到最后旧的Token失效时才会重新获取Token
     *
     * @return
     */
    int refreshAtExpiresBefore() default 600;

    /**
     * 用户名
     *
     * @return
     */
    String username() default "";

    /**
     * 密码
     *
     * @return
     */
    String password() default "";

    /**
     * 其他的登录参数（在Get内容中）。例如：
     * {
     * "params1:值1",
     * "params2:值2",
     * "other-info:其他参数值"
     * }
     *
     * @return
     */
    String[] params() default {};

    /**
     * 其他的登录参数（在Post内容中）。例如：
     * {
     * "params1:值1",
     * "params2:值2",
     * "other-info:其他参数值"
     * }
     *
     * @return
     */
    String[] body() default {};

    /**
     * Token 信息位置。默认通过Header传输
     *
     * @return
     */
    @Nonnull
    TokenAt tokenAt() default TokenAt.HEADER;

    /**
     * 传输 Token 的参数名，会强制覆盖 tokenAt 的设置。例如：
     * Token在Header的时候默认为 Authorization
     * Token在Body的时候默认为 access_token
     *
     * @return
     */
    String tokenVariable() default "";

    /**
     * Token 前缀，会强制覆盖 tokenAt 的设置。例如：
     * Token在Header的时候默认为 Bearer 前缀
     * Token在Body的时候需默认该项设置为空字符串
     *
     * @return
     */
    String tokenPrefix() default "";

    enum GrantType {
        /**
         * 密码类型
         */
        PASSWORD("password"),
        /**
         * 客户端类型
         */
        CLIENT_CREDENTIALS("client_credentials");
        /**
         * 实际请求中GrantType的请求值
         */
        private final String value;

        GrantType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    enum TokenAt {
        /**
         * 请求头
         */
        HEADER("Authorization", "Bearer"),
        /**
         * 请求内容
         */
        URL("access_token", "");
        /**
         * 实际请求中的请求变量名
         */
        private final String tokenVariable;
        /**
         * 实际请求中的请求值前缀
         */
        private final String tokenPrefix;

        TokenAt(String tokenVariable, String tokenPrefix) {
            this.tokenVariable = tokenVariable;
            this.tokenPrefix = tokenPrefix;
        }

        public String getTokenVariable() {
            return tokenVariable;
        }

        public String getTokenPrefix() {
            return tokenPrefix;
        }

        /**
         * 获取变量名
         *
         * @param defaultTokenVariable 默认的变量名，这个参数传入的应该为 @OAuth2.tokenVariable 的值
         * @return
         */
        public String getTokenVariable(String defaultTokenVariable) {
            if (StringUtils.isBlank(defaultTokenVariable)) {
                // 当 @OAuth2.tokenVariable 未设置值时，使用 TokenAt 默认值
                return tokenVariable;
            }
            return defaultTokenVariable;
        }

        /**
         * 获得 Token 值
         *
         * @param defaultPrefix 默认的前缀，这个参数传入的应该为 @OAuth2.tokenPrefix 的值
         * @param token         实际的 Token 值
         * @return
         */
        public String getTokenValue(String defaultPrefix, String token) {
            // 优先使用 @OAuth2.tokenPrefix 值
            String prefix = defaultPrefix;
            if (StringUtils.isBlank(prefix)) {
                // 当 @OAuth2.tokenPrefix 未设置值时，使用 TokenAt 默认值
                prefix = tokenPrefix;
            }
            if (StringUtils.isBlank(prefix)) {
                return token;
            }
            return prefix + " " + token;
        }
    }

}
