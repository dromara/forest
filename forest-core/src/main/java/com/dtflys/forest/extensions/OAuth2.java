package com.dtflys.forest.extensions;

import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.annotation.RequestAttributes;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.authorization.OAuth2LifeCycle;
import com.dtflys.forest.utils.StringUtils;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * OAuth2 请求。自动获取Token、刷新Token，目前只支持 PASSWORD、CLIENT_CREDENTIALS 这样的无回调、无交互的类型。
 *
 * @author HouKunLin
 * @since 1.5.0-BETA9
 */
@Documented
@MethodLifeCycle(OAuth2LifeCycle.class)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface OAuth2 {
    /**
     * 请求Token的URL地址
     */
    @Nonnull
    String tokenUri();

    /**
     * 设置缓存ID，为了确保缓存唯一（防止被其他同类型请求的缓存Token覆盖）建议设置此值。
     * 虽然系统已经预设了一部分缓存key内容，但是也无法保证缓存不被其他配置覆盖，因此可能需要您手动设置该值。
     * 默认使用：tokenUri/clientId/grantType/scope/username 组成一个KEY。
     * 假如设置了 cacheId 将会直接把 cacheId 作为缓冲 KEY。
     * 该参数在我引入了适配微信公众号开发请求 access_token 时发现可能需要这样设置，因为微信公众号传入的是 appid 而不是 clientId，
     * 因此可能会有人把 clientId 设置为空字符串，此时假如系统中管理了两个公众号的接口请求，就可能引起缓存 Token 错乱、被覆盖的可能性。
     */
    String cacheId() default "";

    /**
     * 客户端ID
     */
    @Nonnull
    String clientId();

    /**
     * 客户端Secret
     */
    @Nonnull
    String clientSecret();

    /**
     * grantType 类型
     */
    @Nonnull
    GrantType grantType() default GrantType.CLIENT_CREDENTIALS;

    /**
     * 强制设置 grantType 值，该值将会覆盖 grantType 的设置。
     * 由于一些特殊的情况，grantType 的值跟 GrantType 中预设的不一致，可以通过设置该参数来强制覆盖 GrantType 的参数值。
     * 例如微信公众号开发的 grant_type = client_credential ，而预设中的是 CLIENT_CREDENTIALS("client_credentials") 两者不一致导致的请求失败
     */
    String grantTypeValue() default "";

    /**
     * 范围
     */
    String scope() default "";

    /**
     * 在 expires_in 剩余多少秒进行刷新。默认：600秒5分钟。
     * 不会主动触发刷新，只有在Token有效期最后5分钟内有请求的时候才会触发刷新。
     * 假如刷新失败将一直使用旧的Token，直到最后旧的Token失效时才会重新获取Token
     */
    int refreshAtExpiresBefore() default 600;

    /**
     * 用户名
     */
    String username() default "";

    /**
     * 密码
     */
    String password() default "";

    /**
     * 其他的登录参数（在Get内容中）。例如：
     * <pre>
     * {
     * "名称1:值1",
     * "名称2:值2",
     * "other-info:其他参数值"
     * }
     * </pre>
     */
    String[] query() default {};

    /**
     * 其他的登录参数（在Post内容中）。例如：
     * <pre>
     * {
     * "名称1:值1",
     * "名称2:值2",
     * "other-info:其他参数值"
     * }
     * </pre>
     */
    String[] body() default {};

    /**
     * Token 信息位置。默认通过Header传输
     */
    @Nonnull
    TokenAt tokenAt() default TokenAt.HEADER;

    /**
     * 传输 Token 的参数名，会强制覆盖 tokenAt 的设置。例如：
     * <p>Token在Header的时候默认为 Authorization</p>
     * <p>Token在Body的时候默认为 access_token</p>
     */
    String tokenVariable() default "";

    /**
     * Token 前缀，会强制覆盖 tokenAt 的设置。例如：
     * <p>Token在Header的时候默认为 Bearer 前缀 </p>
     * <p>Token在Body的时候需默认该项设置为空字符串</p>
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

        /**
         * 获取 GrantType 的实际请求参数值
         *
         * @param defaultValue 默认的一个参数值，这个参数传入的应该为 @OAuth2.grantTypeValue 的值
         * @return GrantType 实际请求值
         */
        public String getValue(String defaultValue) {
            if (StringUtils.isBlank(defaultValue)) {
                // 当 @OAuth2.grantTypeValue 未设置值时，使用 @OAuth2.grantType 默认值
                return value;
            }
            return defaultValue;
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
