package org.dromara.forest.annotation;

import org.dromara.forest.lifecycles.base.BaseRequestLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

/**
 * 接口级别请求配置信息注解
 * <p>
 * The annotation must be on an interface.
 * It allows you to make some configurations shared for all the requests in this interface.
 * <p>
 * 该注解必须绑定在某一个接口类上。在该注解中配置的参数信息将会被次注解绑定的接口中所有方法的请求所共享，
 * 它可以覆盖全局级别的请求配置信息，但不能覆盖方法级别的请求参数信息
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-30 16:59
 * @see Request
 */
@Documented
@BaseLifeCycle(BaseRequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface BaseRequest {

    /**
     * 接口级别基础 URL
     * <p>
     *     请求的最终 url = baseUrl + request Url.
     *     如果 request Url 是 http:// 或 https:// 等协议形式开头的就会<b>忽略</b> baseUrl
     * </p>
     * @return 接口级别基础 URL
     * @see Request#url()
     */
    String baseURL() default "";

    /**
     * 接口级别 Content Type
     * <p>
     *     在请求的 {@code Content-Type} 头或 {@code contentType} 属性没有设置，
     *     且此属性不为空字符串的情况下，
     *     此属性作为默认值填到请求的 {@code Content-Type} 头中
     * </p>
     * @return 接口级别 Content Type
     * @see Request#contentType()
     */
    String contentType() default "";

    /**
     * 接口级别 Content Encoding
     * <p>
     *     在请求的 {@code Content-Encoding} 头或 {@code contentEncoding} 属性没有设置，
     *     且此属性不为空字符串的情况下，
     *     此属性作为默认值填到请求的 {@code Content-Encoding} 头中
     * </p>
     * @return 接口级别 Content Encoding
     * @see Request#contentEncoding()
     */
    String contentEncoding() default "";

    /**
     * 接口级别 User Agent
     * <p>
     *     在请求的 {@code User-Agent} 头或 {@code userAgent} 属性没有设置，
     *     且此属性不为空字符串的情况下，
     *     此属性作为默认值填到请求的 {@code User-Agent} 头中
     * </p>
     * @return 接口级别 User Agent
     * @see Request#userAgent()
     */
    String userAgent() default "";

    /**
     * 接口级别请求字符集
     * <p>
     *     在请求的 {@code charset} 属性没有设置，
     *     且此属性不为空字符串的情况下，
     *     此属性作为默认值填到请求的 {@code charset} 头中
     * </p>
     * @return 接口级别请求字符集
     * @see Request#charset()
     */
    String charset() default "";

    /**
     * 接口级别请求头列表
     * <p>
     *     发送请求时，会将此属性中所有请起头信息合并到请求中，
     *     但请求里的头列表中和此属性的请求头中同名的头信息不做合并
     * </p>
     * @return 接口级别请求头列表
     * @see Request#headers()
     */
    String[] headers() default {};

    /**
     * 接口级别拦截器表
     * <p>
     *     发送请求时，会将此属性中所有拦截器合并到请求中，
     *     但请求里的拦截器列表中和此属性的拦截器表中相同的拦截器不做合并
     * </p>
     * @return 接口级别拦截器表
     * @see Request#interceptor()
     */
    Class<?>[] interceptor() default {};

    /**
     * 接口级别超时时间 (单位为毫秒)
     * <p>
     *     在请求的 {@code timeout} 属性没有设置，
     *     且此属性大于{@code -1}情况下，
     *     此属性作为默认值填到请求的 {@code timeout} 属性中
     * </p>
     * @return 接口级别超时时间
     * @see Request#timeout()
     * @deprecated 请使用 {@link #connectTimeout()} 和 {@link #readTimeout()}
     */
    int timeout() default -1;

    /**
     * 请求连接超时时间, 单位为毫秒
     * @return 请求连接超时时间
     */
    int connectTimeout() default -1;

    /**
     * 请求读取超时时间, 单位为毫秒
     * @return 读取超时时间
     */
    int readTimeout() default -1;

    /**
     * 接口级别 SSL 协议
     * <p>
     *     在请求的 {@code sslProtocol} 属性没有设置，
     *     且此属性不为空字符串的情况下，
     *     此属性作为默认值填到请求的 {@code sslProtocol} 属性中
     * </p>
     * @return 接口级别 SSL 协议
     * @see Request#sslProtocol()
     */
    String sslProtocol() default "";

    /**
     * 接口级别重试器
     * <p>
     *     在请求的 {@code retryer} 属性没有设置，
     *     且此属性不为 {@code Object.class}
     *     此属性作为默认值填到请求的 {@code retryer} 属性中
     * </p>
     * @return 接口级别重试器
     * @see Request#retryer()
     */
    Class<?> retryer() default Object.class;


    /**
     * 接口级别最大重试次数
     * <p>
     *     在请求的 {@code retryCount} 属性没有设置，
     *     且此属性大于 {@code -1}
     *     此属性作为默认值填到请求的 {@code retryCount} 属性中
     * </p>
     * @return 接口级别最大重试次数
     * @see Request#retryCount()
     */
    @Deprecated
    int retryCount() default -1;

    /**
     * 接口级别最大重试间隔时间
     * <p>
     *     在请求的 {@code maxRetryInterval} 属性没有设置，
     *     且此属性大于 {@code -1}
     *     此属性作为默认值填到请求的 {@code maxRetryInterval} 属性中
     * </p>
     * @return 接口级别最大重试间隔时间
     * @see Request#maxRetryInterval()
     */
    long maxRetryInterval() default -1;

    /**
     * 接口级别 KeyStore Id
     * <p>
     *     在请求的 {@code keyStore} 属性没有设置，
     *     且此属性不为空字符串
     *     此属性作为默认值填到请求的 {@code keyStore} 属性中
     * </p>
     * @return KeyStore Id
     * @see Request#keyStore()
     */
    String keyStore() default "";

//    boolean[] logEnable() default {};

}
