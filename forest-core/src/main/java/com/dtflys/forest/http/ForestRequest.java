/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dtflys.forest.http;

import com.dtflys.forest.auth.BasicAuth;
import com.dtflys.forest.auth.ForestAuthenticator;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.HttpExecutor;
import com.dtflys.forest.callback.OnCanceled;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnLoadCookie;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnRedirection;
import com.dtflys.forest.callback.OnRetry;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestAsyncAbortException;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.body.ByteArrayRequestBody;
import com.dtflys.forest.http.body.FileRequestBody;
import com.dtflys.forest.http.body.InputStreamRequestBody;
import com.dtflys.forest.http.body.MultipartRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;
import com.dtflys.forest.http.body.StringRequestBody;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorAttributes;
import com.dtflys.forest.interceptor.InterceptorChain;
import com.dtflys.forest.lifecycles.file.DownloadLifeCycle;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.mapping.MappingURLTemplate;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.multipart.ByteArrayMultipart;
import com.dtflys.forest.multipart.FileMultipart;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.multipart.InputStreamMultipart;
import com.dtflys.forest.pool.ForestRequestPool;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.retryer.ForestRetryer;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.ssl.SSLSocketFactoryBuilder;
import com.dtflys.forest.ssl.SSLUtils;
import com.dtflys.forest.ssl.TrustAllHostnameVerifier;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.TimeUtils;
import com.dtflys.forest.utils.TypeReference;
import com.dtflys.forest.utils.Validations;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.dtflys.forest.mapping.MappingParameter.TARGET_BODY;
import static com.dtflys.forest.mapping.MappingParameter.TARGET_HEADER;
import static com.dtflys.forest.mapping.MappingParameter.TARGET_QUERY;

/**
 * Forest请求对象
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-24
 */
public class ForestRequest<T> implements HasURL, HasHeaders {

    private final static Object[] EMPTY_RENDER_ARGS = new Object[0];

    /**
     * 默认上传/下载进度监听的步长
     * <p>
     * 每上传/下载一定的比特数，执行一次监听回调函数
     */
    private final static long DEFAULT_PROGRESS_STEP = 1024 * 10;

    /**
     * Forest配置信息对象
     */
    private final ForestConfiguration configuration;

    /**
     * Forest方法
     */
    private final ForestMethod method;

    /**
     * HTTP后端
     */
    private HttpBackend backend;

    /**
     * HTTP 后端 Client 对象或对象工厂
     *
     * @since 1.5.23
     */
    private Object backendClient;

    /**
     * 是否缓存 HTTP 后端 Client 对象
     */
    private boolean cacheBackendClient = true;

    /**
     * HTTP 执行器
     *
     * @since 1.5.27
     */
    private HttpExecutor executor;

    /**
     * 生命周期处理器
     */
    private volatile LifeCycleHandler lifeCycleHandler;

    /**
     * HTTP协议
     * <p>默认为 HTTP 1.1
     */
    private ForestProtocol protocol = ForestProtocol.HTTP_1_1;

    /**
     * 请求是否以取消
     *
     * @since 1.5.27
     */
    private volatile boolean canceled = false;

    /**
     * URL路径
     */
    private ForestURL url;

    /**
     * URL中的Query参数表
     */
    private ForestQueryMap query = new ForestQueryMap(this);

    /**
     * 请求类型
     */
    private ForestRequestType type;

    /**
     * 请求类型变更历史
     */
    private List<ForestRequestType> typeChangeHistory;

    /**
     * 请求日志消息
     */
    private RequestLogMessage requestLogMessage;

    /**
     * 请求参数编码字符集
     */
    private String charset;

    /**
     * 请求响应返回数据的字符编码
     */
    private String responseEncode;

    /**
     * 是否异步
     */
    private boolean async;

    /**
     * 异步请求模式
     * <p>该字段只有在 async = true 时有效</p>
     *
     * @since 1.5.27
     */
    private ForestAsyncMode asyncMode = ForestAsyncMode.PLATFORM;

    /**
     * 请求认证器
     *
     * @since 1.5.28
     */
    private ForestAuthenticator authenticator;

    /**
     * 是否打开自动重定向
     */
    private boolean autoRedirection;

    /**
     * 响应数据类型
     * <p>该类型决定请求响应返回的数据将以何种方式进行反序列化
     */
    private ForestDataType dataType;

    /**
     * 请求超时时间
     */
    private int timeout = 3000;

    /**
     * 请求连接超时时间
     */
    private Integer connectTimeout = -1;

    /**
     * 请求读取超时时间
     */
    private Integer readTimeout = -1;

    /**
     * 是否开启解压GZIP响应内容
     */
    private boolean decompressResponseGzipEnabled = false;

    /**
     * SSL协议
     * <p>该字段在单向HTTPS请求发送时决定哪种SSL协议
     */
    private String sslProtocol;

    /**
     * 请求失败后的重试次数
     */
    private int maxRetryCount = 0;

    /**
     * 最大请重试的时间间隔，时间单位为毫秒
     */
    private long maxRetryInterval = 0;

    /**
     * 请求体
     * <p>
     * 该字段为列表类型，列表每一项为请求体项,
     * 都为 {@link ForestRequestBody} 子类的对象实例
     */
    private final ForestBody body;


    /**
     * 请求体集合
     * <p>所有本请求对象中的请求头都在由该请求体集合对象管理
     */
    private ForestHeaderMap headers = new ForestHeaderMap(this);

    /**
     * 文件名
     * <p>该字段为文件下载时，保存到本地磁盘时用到的文件名
     */
    private String filename;

    /**
     * 参数表
     * <p>接口方法调用时传入的参数表
     */
    private Object[] arguments;

    /**
     * 回调函数：请求成功时调用
     */
    private OnSuccess onSuccess;

    /**
     * 回调函数：请求失败时调用
     */
    private OnError onError;

    /**
     * 回调函数: 请求取消后调用
     */
    private OnCanceled onCanceled;

    /**
     * 回调函数: 请求是否成功
     * <p>该回调函数用于判断请求是否成功
     * <p>如果成功, 执行 onSuccess
     * <p>如果失败, 执行 onError
     */
    private SuccessWhen successWhen;

    /**
     * 回调函数：请求重试时调用
     */
    private OnRetry onRetry;

    /**
     * 回调函数：请求重试触发条件
     */
    private RetryWhen retryWhen;

    /**
     * 回调函数: 在请求重定向时触发
     */
    private OnRedirection onRedirection;

    /**
     * 重定向的上一个请求
     * <p>触发重定向时的第一个请求
     */
    ForestRequest<?> prevRequest;

    /**
     * 重定向的上一个响应
     * <p>触发重定向时的第一个响应，即带有301、302等状态码的响应
     */
    ForestResponse<?> prevResponse;

    /**
     * 进度回调函数：上传/下载进度监听时调用
     * <p>每上传/下载传输 ${progressStep} 个比特数时，执行一次监听回调函数
     */
    private OnProgress onProgress;

    /**
     * 回调函数: 加载Cookie时调用
     */
    private OnLoadCookie onLoadCookie;

    /**
     * 回调函数: 需要保存Cookie时调用
     */
    private OnSaveCookie onSaveCookie;

    /**
     * 是否下载文件
     */
    private boolean isDownloadFile = false;

    /**
     * 上传/下载进度监听的步长
     * <p>每上传/下载一定的比特数，执行一次监听回调函数
     */
    private long progressStep = DEFAULT_PROGRESS_STEP;

    /**
     * 拦截器链对象
     */
    private InterceptorChain interceptorChain = new InterceptorChain();

    /**
     * 拦截器属性
     * <p>这里的属性只能在绑定的拦截器中被访问
     */
    private Map<Class, InterceptorAttributes> interceptorAttributes = new ConcurrentHashMap<>();

    /**
     * 请求重试策略
     * <p>可以通过该字段设定自定义的重试策略
     */
    private ForestRetryer retryer;

    /**
     * 是否开启请求重试
     * <p>{@code true}为开启重试, {@code false}为关闭重试
     */
    private boolean retryEnabled = true;

    /**
     * 附件
     * <p>附件信息不回随请求发送到远端服务器，但在本地的任何地方都可以通过请求对象访问到附件信息
     */
    private Map<String, Object> attachments = new ConcurrentHashMap<>();

    /**
     * 当前正求值的延迟参数堆栈
     *
     * @since 1.5.29
     */
    Stack<Lazy> evaluatingLazyValueStack = new Stack<>();


    /**
     * 反序列化器
     */
    private ForestConverter decoder;

    /**
     * 请求日志配置信息
     */
    private LogConfiguration logConfiguration;


    /**
     * SSL KeyStore信息
     * <p>在双向HTTPS请求中使用的验证信息
     */
    private SSLKeyStore keyStore;

    /**
     * SSL主机名/域名验证器
     */
    private HostnameVerifier hostnameVerifier;

    /**
     * SSL 信任管理器
     */
    private TrustManager trustManager;

    /**
     * SSL Socket 工厂构造器
     */
    private SSLSocketFactoryBuilder sslSocketFactoryBuilder;

    /**
     * 正向代理
     */
    private ForestProxy proxy;


    public ForestRequest(ForestConfiguration configuration, ForestMethod method, ForestBody body, Object[] arguments) {
        this.configuration = configuration;
        this.method = method;
        this.arguments = arguments;
        this.body = body;
    }


    public ForestRequest(ForestConfiguration configuration, ForestMethod method, Object[] arguments) {
        this.configuration = configuration;
        this.method = method;
        this.arguments = arguments;
        this.body = new ForestBody(this);
    }

    public ForestRequest(ForestConfiguration configuration, ForestMethod method) {
        this(configuration, method, new Object[0]);
    }

    public ForestRequest(ForestConfiguration configuration) {
        this(configuration, null, new Object[0]);
    }

    public ForestRequest(ForestConfiguration configuration, Object[] arguments) {
        this(configuration, null, arguments);
    }


    public ForestRequest<T> cond(boolean condition, Consumer<ForestRequest<?>> consumer) {
        if (condition) {
            consumer.accept(this);
        }
        return this;
    }

    public ForestRequest<T> condNull(Object value, Consumer<ForestRequest<?>> consumer) {
        return cond(value == null, consumer);
    }

    public ForestRequest<T> condEmpty(CharSequence value, Consumer<ForestRequest<?>> consumer) {
        return cond(StringUtils.isEmpty(value), consumer);
    }


    public ForestRequest<T> condNotNull(Object value, Consumer<ForestRequest<?>> consumer) {
        return cond(value != null, consumer);
    }

    public ForestRequest<T> condNotEmpty(CharSequence value, Consumer<ForestRequest<?>> consumer) {
        return cond(StringUtils.isNotEmpty(value), consumer);
    }


    public ForestRequestConditionWrapper<T> ifThen(boolean condition, Consumer<ForestRequest<?>> consumer) {
        if (condition) {
            consumer.accept(this);
        }
        return new ForestRequestConditionWrapper<>(this, condition);
    }

    public ForestRequestConditionWrapper<T> ifThen(Function<ForestRequest<?>, Boolean> conditionFunc, Consumer<ForestRequest<?>> consumer) {
        Validations.assertParamNotNull(conditionFunc, "conditionFunc");
        return ifThen(conditionFunc.apply(this), consumer);
    }


    public ForestRequestConditionWrapper<T> ifNullThen(Object value, Consumer<ForestRequest<?>> consumer) {
        return ifThen(value == null, consumer);
    }

    public ForestRequestConditionWrapper<T> ifNotNullThen(Object value, Consumer<ForestRequest<?>> consumer) {
        return ifThen(value != null, consumer);
    }


    public ForestRequestConditionWrapper<T> ifEmptyThen(CharSequence value, Consumer<ForestRequest<?>> consumer) {
        return ifThen(StringUtils.isEmpty(value), consumer);
    }

    public ForestRequestConditionWrapper<T> ifNotEmptyThen(CharSequence value, Consumer<ForestRequest<?>> consumer) {
        return ifThen(StringUtils.isNotEmpty(value), consumer);
    }



    /**
     * 获取该请求的配置对象
     *
     * @return 配置对象
     */
    public ForestConfiguration getConfiguration() {
        return configuration;
    }


    /**
     * 获取请求协议
     *
     * @return 请求协议
     */
    public ForestProtocol getProtocol() {
        return protocol;
    }


    /**
     * 设置请求协议
     *
     * @param protocol 请求协议
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setProtocol(ForestProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * 请求是否以取消
     *
     * @return {@code true}: 请求已被取消; {@code false}: 未被取消
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * 获取请求URL
     * <p>不同于 {@link ForestRequest#getUrl()} 方法,
     * <p>此方法返回的URL为 {@link ForestURL} 对象实例
     *
     * @return {@link ForestURL} 对象实例
     */
    @Override
    public ForestURL url() {
        return this.url;
    }

    /**
     * 设置请求URL
     * <p>不同于 {@link ForestRequest#setUrl(String)} 方法,
     * <p>此方法的输入参数为 {@link ForestURL} 对象实例
     *
     * @param url {@link ForestURL} 对象实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> url(ForestURL url) {
        this.url = url;
        return this;
    }

    /**
     * 设置请求URL
     * <p>同 {@link ForestRequest#setUrl(String)} 方法
     *
     * @param url URL字符串
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setUrl(String)
     */
    public ForestRequest<T> url(String url) {
        return setUrl(url);
    }


    /**
     * 获取请求URL字符串
     *
     * @return URL字符串
     */
    public String getUrl() {
        return url.getOriginalUrl();
    }

    /**
     * 获取请求URL字符串
     * <p>同
     *
     * @return URL字符串
     */
    public String urlString() {
        return url.toString();
    }


    /**
     * 获取请求URI
     *
     * @return {@link URI}类实例
     */
    public URI getURI() {
        return this.url.toURI();
    }

    /**
     * 获取请求的路由
     *
     * @return {@link ForestRoute}对象实例
     * @author gongjun [dt_flys@hotmail.com]
     * @since 1.5.22
     */
    public ForestRoute getRoute() {
        return url.getRoute();
    }

    /**
     * 获取请求的路由
     *
     * @return {@link ForestRoute}对象实例
     * @author gongjun [dt_flys@hotmail.com]
     * @since 1.5.22
     */
    public ForestRoute route() {
        return getRoute();
    }

    /**
     * 设置请求的url地址
     * <p>每次设置请求的url地址时，都会解析传入的url参数字符串
     * <p>然后从url中解析出Query参数，并将其替换调用原来的Query参数，或新增成新的Query参数
     *
     * @param urlTemplate url地址字符串模板
     * @param args 字符串模板渲染参数数组
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setUrl(MappingURLTemplate urlTemplate, Object... args) {
        if (!this.query.isEmpty()) {
            this.query.clearQueriesFromUrl();
        }

        final ForestURL newUrl = urlTemplate.render(args, this.query);
        if (this.url == null) {
            this.url = newUrl;
        } else {
            this.url = newUrl.mergeURLWith(this.url);
        }
        return this;
    }


    /**
     * 设置请求的url地址
     * <p>每次设置请求的url地址时，都会解析传入的url参数字符串
     * <p>然后从url中解析出Query参数，并将其替换调用原来的Query参数，或新增成新的Query参数
     *
     * @param url url地址字符串
     * @param args 字符串模板渲染参数数组
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setUrl(String url, Object... args) {
        if (StringUtils.isBlank(url)) {
            throw new ForestRuntimeException("[Forest] Request url cannot be empty!");
        }
        arguments(args);
        final String srcUrl = StringUtils.trimBegin(url);
        MappingURLTemplate template = method.makeURLTemplate(null, null, srcUrl);
        return setUrl(template, args);
    }

    /**
     * 设置请求的url地址
     * <p>每次设置请求的url地址时，都会解析传入的url参数字符串
     * <p>然后从url中解析出Query参数，并将其替换调用原来的Query参数，或新增成新的Query参数
     *
     * @param url url地址字符串
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setUrl(String url) {
        return setUrl(url, arguments == null ? EMPTY_RENDER_ARGS : arguments);
    }


    /**
     * 获取URL用户验证信息
     *
     * <p>包含在URL中的用户验证信息，比如:
     * <p>URL http://xxx:yyy@localhost:8080 中 xxx:yyy 的部分为用户信息
     * <p>其中，xxx为用户名，yyy为用户密码
     *
     * @return URL用户验证信息
     */
    public String getUserInfo() {
        return this.url.getUserInfo();
    }

    /**
     * 获取URL用户验证信息
     *
     * <p>包含在URL中的用户验证信息，比如:
     * <p>URL http://xxx:yyy@localhost:8080 中 xxx:yyy 的部分为用户信息
     * <p>其中，xxx为用户名，yyy为用户密码
     *
     * @return URL用户验证信息
     */
    public String userInfo() {
        return getUserInfo();
    }

    /**
     * 设置URL用户验证信息
     *
     * <p>包含在URL中的用户验证信息，比如:
     * <p>URL http://xxx:yyy@localhost:8080 中 xxx:yyy 的部分为用户信息
     * <p>其中，xxx为用户名，yyy为用户密码
     *
     * @param userInfo URL用户验证信息
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setUserInfo(String userInfo) {
        this.url.setUserInfo(userInfo);
        return this;
    }

    /**
     * 设置URL用户验证信息
     * <p>同 {@link ForestRequest#setUserInfo(String)}
     *
     * @param userInfo URL用户验证信息
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setUserInfo(String)
     */
    public ForestRequest<T> userInfo(String userInfo) {
        return setUserInfo(userInfo);
    }

    /**
     * 设置URL地址的HTTP协议头
     *
     * @param scheme HTTP协议头
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setScheme(String scheme) {
        this.url.setScheme(scheme);
        return this;
    }

    /**
     * 设置URL地址的HTTP协议头
     * <p>同 {@link ForestRequest#setScheme(String)}
     *
     * @param scheme HTTP协议头
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setScheme(String)
     */
    public ForestRequest<T> scheme(String scheme) {
        return setScheme(scheme);
    }


    /**
     * 获取URL地址的HTTP协议头
     *
     * @return {@link ForestRequest}对象实例
     */
    public String getScheme() {
        return this.url.getScheme();
    }

    /**
     * 获取URL地址的HTTP协议头
     * <p>同 {@link ForestRequest#getScheme()}
     *
     * @return HTTP协议头
     * @see ForestRequest#getScheme()
     */
    public String scheme() {
        return getScheme();
    }


    /**
     * 设置URL主机地址
     *
     * @param host 主机地址
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setHost(String host) {
        this.url.setHost(host);
        return this;
    }

    /**
     * 设置URL主机地址
     * <p>同 {@link ForestRequest#setHost(String)} 方法
     *
     * @param host 主机地址
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setHost(String)
     */
    public ForestRequest<T> host(String host) {
        return setHost(host);
    }


    /**
     * 获取URL主机地址
     *
     * @return 主机地址
     */
    public String getHost() {
        return this.url.getHost();
    }

    /**
     * 获取URL主机地址
     * <p>同 {@link ForestRequest#getHost()} 方法
     *
     * @return 主机地址
     * @see ForestRequest#getHost()
     */
    public String host() {
        return getHost();
    }


    /**
     * 设置URL主机端口
     *
     * @param port 主机端口
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setPort(int port) {
        this.url.setPort(port);
        return this;
    }

    /**
     * 设置URL主机端口
     * <p>同 {@link ForestRequest#setPort(int)} 方法
     *
     * @param port 主机端口
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setPort(int)
     */
    public ForestRequest<T> port(int port) {
        return setPort(port);
    }

    /**
     * 获取URL主机端口
     *
     * @return 主机端口
     */
    public int getPort() {
        return this.url.getPort();
    }

    /**
     * 获取URL主机端口
     * <p>同 {@link ForestRequest#getPort()} 方法
     *
     * @return 主机端口
     * @see ForestRequest#getPort()
     */
    public int port() {
        return getPort();
    }




    /**
     * 设置请求的主机地址(主机名/ip地址 + 端口号)
     *
     * @param address Forest主机地址信息
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setAddress(ForestAddress address) {
        this.url.setAddress(address);
        return this;
    }

    /**
     * 设置请求的主机地址(主机名/ip地址 + 端口号)
     *
     * @param address Forest主机地址信息
     * @param forced 是否强制修改, {@code true}: 强制修改, {@code false}: 非强制，如果URL已设置host、port等信息则不会修改
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setAddress(ForestAddress address, boolean forced) {
        this.url.setAddress(address, forced);
        return this;
    }


    /**
     * 设置请求的主机地址(主机名/ip地址 + 端口号)
     *
     * @param host 主机名/ip地址
     * @param port 端口号
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setAddress(String host, int port) {
        return setAddress(new ForestAddress(host, port));
    }


    /**
     * 设置请求的主机地址(主机名/ip地址 + 端口号)
     * <p>同 {@link ForestRequest#setAddress(ForestAddress)} 方法
     *
     * @param address Forest主机地址信息
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setAddress(ForestAddress)
     */
    public ForestRequest<T> address(ForestAddress address) {
        this.url.setAddress(address);
        return this;
    }

    /**
     * 设置请求的主机地址(主机名/ip地址 + 端口号)
     * <p>同 {@link ForestRequest#setAddress(ForestAddress)} 方法
     *
     * @param address Forest主机地址信息
     * @param forced 是否强制修改, {@code true}: 强制修改, {@code false}: 非强制，如果URL已设置host、port等信息则不会修改
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setAddress(ForestAddress)
     */
    public ForestRequest<T> address(ForestAddress address, boolean forced) {
        this.url.setAddress(address, forced);
        return this;
    }


    /**
     * 设置请求的主机地址(主机名/ip地址 + 端口号)
     * <p>同 {@link ForestRequest#setAddress(String, int)} 方法
     *
     * @param host 主机名/ip地址
     * @param port 端口号
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setAddress(String, int)
     */
    public ForestRequest<T> address(String host, int port) {
        return setAddress(host, port);
    }

    /**
     * 设置URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @param path URL根路径
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setBasePath(String path) {
        this.url.setBasePath(path);
        return this;
    }


    /**
     * 设置URL根路径
     * <p>同 {@link ForestRequest#setBasePath(String)}方法
     *
     * @param path URL根路径
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setBasePath(String)
     */
    public ForestRequest<T> basePath(String path) {
        return setBasePath(path);
    }

    /**
     * 获取URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @return URL根路径
     */
    public String getBasePath() {
        return this.url.normalizeBasePath();
    }

    /**
     * 获取URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     * <p>同 {@link ForestRequest#getPath()}
     *
     * @return URL根路径
     * @see ForestRequest#getPath()
     */
    public String basePath() {
        return getBasePath();
    }


    /**
     * 设置URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     *
     * @param path URL路径
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setPath(String path) {
        this.url.setPath(path);
        return this;
    }

    /**
     * 设置URL路径
     * <p>同 {@link ForestRequest#setPath(String)} 方法
     *
     * @param path URL路径
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setPath(String)
     */
    public ForestRequest<T> path(String path) {
        return setPath(path);
    }


    /**
     * 获取URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     *
     * @return URL路径
     */
    public String getPath() {
        return this.url.getPath();
    }

    /**
     * 获取URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     * <p>同 {@link ForestRequest#getPath()}
     *
     * @return URL路径
     * @see ForestRequest#getPath()
     */
    public String path() {
        return getPath();
    }


    /**
     * 获取Reference, 即URL井号后面的字符串
     *
     * <p>列如：http://localhost:8080/xxx#yyyyy
     * <p>上例中 yyyy 便是 Reference
     *
     * @return URL井号后面的字符串
     */
    public String getRef() {
        return url.getRef();
    }

    /**
     * 设置Reference, 即URL井号(#)后面的字符串
     *
     * <p>列如：http://localhost:8080/xxx#yyyyy
     * <p>上例中 yyyy 便是 Reference
     *
     * @param ref URL井号(#)后面的字符串
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setRef(String ref) {
        this.url.setRef(ref);
        return this;
    }

    /**
     * 设置Reference, 即URL井号后面的字符串
     *
     * <p>列如：http://localhost:8080/xxx#yyyyy
     * <p>上例中 yyyy 便是 Reference
     * <p>同 {@link ForestRequest#setRef(String)} 方法
     *
     * @param ref URL井号后面的字符串
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setRef(String)
     */
    public ForestRequest<T> ref(String ref) {
        this.url.setRef(ref);
        return this;
    }

    /**
     * 获取Reference, 即URL井号后面的字符串
     * <p>同 {@link ForestRequest#getRef()}
     *
     * <p>列如：http://localhost:8080/xxx#yyyyy
     * <p>上例中 yyyy 便是 Reference
     *
     * @return URL井号后面的字符串
     * @see ForestRequest#getRef()
     */
    public String ref() {
        return getRef();
    }


    /**
     * 获取请求对应的Forest方法
     *
     * @return Forest方法对象
     */
    public ForestMethod getMethod() {
        return method;
    }

    /**
     * 获取HTTP后端对象
     *
     * @return HTTP后端框架对象，{@link HttpBackend}接口实例
     */
    public HttpBackend getBackend() {
        return backend;
    }

    /**
     * 获取HTTP后端对象
     * <p>同 {@link ForestRequest#getBackend()}
     *
     * @return HTTP后端框架对象，{@link HttpBackend}接口实例
     * @see ForestRequest#getBackend()
     */
    public HttpBackend backend() {
        return getBackend();
    }


    /**
     * 设置HTTP后端框架
     *
     * @param backend HTTP后端框架对象，{@link HttpBackend}接口实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setBackend(HttpBackend backend) {
        this.backend = backend;
        return this;
    }

    /**
     * 设置HTTP后端框架
     * <P>同 {@link ForestRequest#setBackend(HttpBackend)} 方法
     *
     * @param backend HTTP后端对象，{@link HttpBackend}接口实例
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setBackend(HttpBackend)
     */
    public ForestRequest<T> backend(HttpBackend backend) {
        this.backend = backend;
        return this;
    }

    /**
     * 设置HTTP后端框架
     *
     * @param backendName Forest后端框架名称
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setBackend(String backendName) {
        HttpBackend backend = configuration.getBackendSelector().select(backendName);
        if (backend != null) {
            backend.init(configuration);
            this.backend = backend;
        }
        return this;
    }

    /**
     * 设置 HTTP 后端框架
     * <p>同 {@link ForestRequest#setBackend(String)}
     *
     * @param backendName Forest后端框架名称
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setBackend(String)
     */
    public ForestRequest<T> backend(String backendName) {
        return setBackend(backendName);
    }


    /**
     * 获取 HTTP 后端 Client 对象或对象工厂
     *
     * @return Client 对象或对象工厂
     * @since 1.5.23
     */
    public Object getBackendClient() {
        return backendClient;
    }

    /**
     * 设置 HTTP 后端 Client 对象或对象工厂
     *
     * @param backendClient 后端 Client 对象或对象工厂
     * @return {@link ForestRequest}对象实例
     * @since 1.5.23
     */
    public ForestRequest<T> setBackendClient(Object backendClient) {
        this.backendClient = backendClient;
        return this;
    }

    /**
     * 获取 HTTP 后端 Client 对象或对象工厂
     *
     * @return Client 对象或对象工厂
     * @since 1.5.23
     */
    public Object backendClient() {
        return getBackendClient();
    }

    /**
     * 设置 HTTP 后端 Client 对象或对象工厂
     *
     * @param backendClient 后端 Client 对象或对象工厂
     * @return {@link ForestRequest}对象实例
     * @since 1.5.23
     */
    public ForestRequest<T> backendClient(Object backendClient) {
        return setBackendClient(backendClient);
    }

    /**
     * 是否缓存 HTTP 后端 Client 对象
     *
     * @return {@code true}: 缓存 Client 对象, {@code false}: 不缓存
     * @since 1.5.23
     */
    public boolean cacheBackendClient() {
        return cacheBackendClient;
    }

    /**
     * 设置是否缓存 HTTP 后端 Client 对象
     *
     * @param cacheBackendClient {@code true}: 缓存 Client 对象, {@code false}: 不缓存
     * @return {@link ForestRequest}对象实例
     * @since 1.5.23
     */
    public ForestRequest<T> setCacheBackendClient(boolean cacheBackendClient) {
        this.cacheBackendClient = cacheBackendClient;
        return this;
    }

    /**
     * 设置是否缓存 HTTP 后端 Client 对象
     *
     * @param cacheBackendClient {@code true}: 缓存 CLient 对象, {@code false}: 不缓存
     * @return {@link ForestRequest}对象实例
     * @since 1.5.23
     */
    public ForestRequest<T> cacheBackendClient(boolean cacheBackendClient) {
        return setCacheBackendClient(cacheBackendClient);
    }

    /**
     * 获取生命周期处理器
     * @return 生命周期处理器，{@link LifeCycleHandler}接口实例
     */
    public LifeCycleHandler getLifeCycleHandler() {
        return lifeCycleHandler;
    }

    /**
     * 设置生命周期处理器
     * @param lifeCycleHandler 生命周期处理器，{@link LifeCycleHandler}接口实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest setLifeCycleHandler(LifeCycleHandler lifeCycleHandler) {
        this.lifeCycleHandler = lifeCycleHandler;
        return this;
    }

    /**
     * 获取 Content-Type 请求头的 boundary 字符串
     * <p>
     * 如果 Content-Type 请求中没有 boundary 字符串,
     * 则生成一个新的随机字符串作为 boundary 字符串并返回
     *
     * @return 如果 Content-Type 请求头为空则返回 null, 否则返回 boundary 字符串
     */
    public String getBoundary() {
        String contentType = getContentType();
        if (StringUtils.isEmpty(contentType)) {
            return null;
        }
        String[] group = contentType.split(";");
        if (group.length > 1) {
            for (int i = 1; i < group.length; i++) {
                String subContentType = group[i];
                String[] nvPair = subContentType.split("=");
                if (nvPair.length > 1) {
                    String name = nvPair[0].trim();
                    String value = nvPair[1].trim();
                    if ("boundary".equalsIgnoreCase(name)) {
                        if (StringUtils.isBlank(value)) {
                            setBoundary(StringUtils.generateBoundary());
                            return getBoundary();
                        }
                        return value;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 设置 Content-Type 请求头中的 boundary 字符串
     *
     * @param boundary boundary 字符串
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setBoundary(String boundary) {
        String contentType = getContentType();
        if (StringUtils.isBlank(contentType)) {
            throw new ForestRuntimeException(
                    "boundary cannot be add to request, due to the header 'Content-Type' is empty");
        }
        String[] group = contentType.split(";");
        int idx = -1;
        String boundaryName = "boundary";
        if (group.length > 1) {
            for (int i = 1; i < group.length; i++) {
                String subContentType = group[i];
                String[] nvPair = subContentType.split("=");
                if (nvPair.length > 1) {
                    String name = nvPair[0].trim();
                    if ("boundary".equalsIgnoreCase(name)) {
                        idx = i;
                        boundaryName = name;
                        break;
                    }
                }
            }
            if (idx > 0) {
                StringBuilder builder = new StringBuilder(group[0]);
                for (int i = 1; i < group.length; i++) {
                    builder.append("; ");
                    if (i == idx) {
                        builder.append(boundaryName)
                                .append("=")
                                .append(boundary);
                    } else {
                        builder.append(group[i]);
                    }
                }
                this.setContentType(builder.toString());
            }
        } else {
            this.setContentType(contentType + "; boundary=" + boundary);
        }
        return this;
    }


    /**
     * 获取请求的Query参数表
     *
     * @return Query参数表
     */
    public ForestQueryMap getQuery() {
        return query;
    }

    /**
     * 根据名称获取请求的Query参数值
     *
     * @param name Query参数名称
     * @return Query参数值
     */
    public Object getQuery(String name) {
        return query.get(name);
    }

    /**
     * 动态获取请求的URL Query参数字符串
     *
     * @return Query参数字符串
     */
    public String getQueryString() {
        return query.toQueryString();
    }

    /**
     * 动态获取请求的URL Query参数字符串
     *
     * @return Query参数字符串
     */
    public String queryString() {
        return getQueryString();
    }



    /**
     * 添加请求中的Query参数
     *
     * @param name Query参数名
     * @param value Query参数值
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQuery(String name, Object value) {
        this.query.addQuery(name, value);
        return this;
    }

    /**
     * 添加请求中延迟求值的Query参数
     *
     * @param name Query参数名
     * @param lazyValue 延迟求值的Query参数值
     * @return
     */
    public ForestRequest<T> addQuery(String name, Lazy lazyValue) {
        this.query.addQuery(name, lazyValue);
        return this;
    }

    /**
     * 添加请求中的Query参数
     *
     * @param name Query参数名
     * @param value Query参数值
     * @param isUrlEncode 是否进行编码
     * @param charset 编码字符集
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQuery(String name, Object value, boolean isUrlEncode, String charset) {
        this.query.addQuery(name, value, isUrlEncode, charset);
        return this;
    }


    /**
     * 添加请求中的集合类Query参数 (重复 Query 参数名)
     *
     * @param name Query参数名
     * @param collection 集合对象
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addQuery(String name, Collection collection) {
        this.query.addQuery(name, collection);
        return this;
    }

    /**
     * 添加请求中的集合类Query参数 (重复 Query 参数名)
     * <p>用于传递列表或数组类 Query 参数
     *
     * @param name Query参数名
     * @param collection 集合对象
     * @param isUrlEncode 是否进行编码
     * @param charset 编码字符集
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addQuery(String name, Collection collection, boolean isUrlEncode, String charset) {
        this.query.addQuery(name, collection, isUrlEncode, charset);
        return this;
    }

    /**
     * 添加请求中的数组类Query参数 (重复 Query 参数名)
     *
     * @param name Query参数名
     * @param array 数组
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addQuery(String name, Object... array) {
        return addQuery(name, array, false, null);
    }


    /**
     * 添加请求中的数组类Query参数 (重复 Query 参数名)
     *
     * @param name Query参数名
     * @param array 数组
     * @param isUrlEncode 是否进行编码
     * @param charset 编码字符集
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addQuery(String name, Object[] array, boolean isUrlEncode, String charset) {
        this.query.addQuery(name, array, isUrlEncode, charset);
        return this;
    }


    /**
     * 添加 Map 类 Query 参数
     * <p>将 Map 的 key 作为 Query 参数名
     * <p>Map 的 value 作为 Query 参数值
     * <p>批量插入到请求的 Query 参数中
     *
     * @param queryMap {@link Map}对象
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addQuery(Map queryMap) {
        this.query.addQuery(queryMap);
        return this;
    }


    /**
     * 添加 Map 类 Query 参数
     * <p>将 Map 的 key 作为 Query 参数名
     * <p>Map 的 value 作为 Query 参数值
     * <p>批量插入到请求的 Query 参数中
     *
     * @param queryMap {@link Map}对象
     * @param isUrlEncode 是否进行编码
     * @param charset 编码字符集
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addQuery(Map queryMap, boolean isUrlEncode, String charset) {
        this.query.addQuery(queryMap, isUrlEncode, charset);
        return this;
    }

    /**
     * 添加请求中的集合类Query参数 (带数组下标的 Query 参数名)
     * <p>用于传递列表或数组类 Query 参数
     *
     * @param name Query参数名
     * @param collection 集合对象
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addArrayQuery(String name, Collection collection) {
        return addArrayQuery(name, collection, false, null);
    }


    /**
     * 添加请求中的集合类Query参数 (带数组下标的 Query 参数名)
     * <p>用于传递列表或数组类 Query 参数
     *
     * @param name Query参数名
     * @param collection 集合对象
     * @param isUrlEncode 是否进行编码
     * @param charset 编码字符集
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addArrayQuery(String name, Collection collection, boolean isUrlEncode, String charset) {
        this.query.addArrayQuery(name, collection, isUrlEncode, charset);
        return this;
    }

    /**
     * 添加请求中的数组类Query参数 (带数组方括号[]的 Query 参数名)
     * <p>用于传递列表或数组类 Query 参数
     *
     * @param name Query参数名
     * @param array 数组
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addArrayQuery(String name, Object... array) {
        return addArrayQuery(name, array, false, null);
    }


    /**
     * 添加请求中的数组类Query参数 (带数组方括号[]的 Query 参数名)
     * <p>用于传递列表或数组类 Query 参数
     *
     * @param name Query参数名
     * @param array 数组
     * @param isUrlEncode 是否进行编码
     * @param charset 编码字符集
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addArrayQuery(String name, Object[] array, boolean isUrlEncode, String charset) {
        this.query.addArrayQuery(name, array, isUrlEncode, charset);
        return this;
    }



    /**
     * 添加 JSON Query 参数
     *
     * @param name Query参数名
     * @param value Query参数值
     * @return {@link ForestRequest}对象实例
     * @since 1.5.4
     */
    public ForestRequest<T> addJSONQuery(String name, Object value) {
        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
        String json = jsonConverter.encodeToString(value);
        this.query.addQuery(name, json);
        return this;
    }


    /**
     * 添加请求中的Query参数
     *
     * @param queryParameter Query参数，{@link SimpleQueryParameter}对象实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQuery(SimpleQueryParameter queryParameter) {
        this.query.addQuery(queryParameter);
        return this;
    }

    /**
     * 批量添加请求中的Query参数
     *
     * @param queryParameters Query参数集合，{@link SimpleQueryParameter}对象实例集合
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQuery(Collection<SimpleQueryParameter> queryParameters) {
        for (SimpleQueryParameter queryParameter : queryParameters) {
            addQuery(queryParameter);
        }
        return this;
    }

    /**
     * 批量添加请求中的Query参数
     *
     * @param queries Query参数集合，{@link SimpleQueryParameter}对象实例集合
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addAllQuery(ForestQueryMap queries) {
        this.query.addAllQueries(queries);
        return this;
    }


    /**
     * 批量添加请求中的同名Query参数
     *
     * @param name Query参数名
     * @param queryValues Query参数值集合
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQueryValues(String name, Collection queryValues) {
        for (Object queryValue : queryValues) {
            addQuery(name, queryValue);
        }
        return this;
    }

    /**
     * 批量添加请求中的Query参数
     *
     * @param queryParameters Query参数数组，{@link SimpleQueryParameter}对象实例数组
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQuery(SimpleQueryParameter[] queryParameters) {
        for (SimpleQueryParameter queryParameter : queryParameters) {
            addQuery(queryParameter);
        }
        return this;
    }

    /**
     * 批量添加请求中的Query参数
     *
     * @param queryParameters Query参数, 接受所有Java对象类型
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQuery(Object queryParameters) {
        final ForestJsonConverter jsonConverter = getConfiguration().getJsonConverter();
        final ConvertOptions options = ConvertOptions.defaultOptions().evaluateLazyValue(false);
        final Map<String, Object> map = jsonConverter.convertObjectToMap(queryParameters, this, options);
        if (map != null && map.size() > 0) {
            map.forEach((key, value) -> {
                if (value != null) {
                    addQuery(String.valueOf(key), value);
                }
            });
        }
        return this;
    }


    /**
     * 替换请求中的Query参数值
     *
     * @param queryParameter Query参数，{@link SimpleQueryParameter}对象实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> replaceQuery(SimpleQueryParameter queryParameter) {
        List<SimpleQueryParameter> queryParameters = this.query.getQueries(queryParameter.getName());
        for (SimpleQueryParameter parameter : queryParameters) {
            parameter.setValue(queryParameter.getValue());
        }
        return this;
    }



    /**
     * 替换请求中的Query参数值
     *
     * @param name Query参数名
     * @param value Query参数值
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> replaceQuery(String name, Object value) {
        List<SimpleQueryParameter> queryParameters = this.query.getQueries(name);
        for (SimpleQueryParameter parameter : queryParameters) {
            parameter.setValue(value);
        }
        return this;
    }

    /**
     * 替换或添加请求中的Query参数
     * <p>当请求中不存在与该方法调用时传递过来{@link SimpleQueryParameter}对象中同名的Query参数时，会将{@link SimpleQueryParameter}对象添加成新的Query参数到请求中，</p>
     * <p>若请求中已存在同名Query参数名时，则会替换请求中的所有同名的Query参数值</p>
     *
     * @param queryParameter Query参数，{@link SimpleQueryParameter}对象实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> replaceOrAddQuery(SimpleQueryParameter queryParameter) {
        List<SimpleQueryParameter> queryParameters = this.query.getQueries(queryParameter.getName());
        if (queryParameters.isEmpty()) {
            addQuery(queryParameter);
        } else {
            for (SimpleQueryParameter parameter : queryParameters) {
                parameter.setValue(queryParameter.getValue());
            }
        }
        return this;
    }

    /**
     * 替换或添加请求中的Query参数
     * <p>当请求中不存在与该方法调用时传递过来{@code name}参数同名的Query参数时，会将{@code name}参数和{@code value}参数添加成新的Query参数到请求中，
     * <p>若请求中已存在同名Query参数名时，则会替换请求中的所有同名的Query参数值
     *
     * @param name Query参数名
     * @param value Query参数值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> replaceOrAddQuery(String name, String value) {
        List<SimpleQueryParameter> queryParameters = this.query.getQueries(name);
        if (queryParameters.isEmpty()) {
            addQuery(name, value);
        } else {
            for (SimpleQueryParameter parameter : queryParameters) {
                parameter.setValue(value);
            }
        }
        return this;
    }

    /**
     * 根据查询名删除URL查询参数
     *
     * @param name 查询名称
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> removeQuery(String name) {
         this.query.removeQueries(name);
         return this;
    }

    /**
     * 删除请求的所有URL查询参数
     *
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> clearQueries() {
        this.query.clear();
        return this;
    }

    /**
     * 获取请求体类型
     *
     * @return 求体类型, {@link ForestDataType}枚举对象
     */
    public ForestDataType getBodyType() {
        return body.getBodyType();
    }

    /**
     * 设置请求体类型
     *
     * @param bodyType 求体类型, {@link ForestDataType}枚举对象
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setBodyType(ForestDataType bodyType) {
        this.body.setBodyType(bodyType);
        return this;
    }

    /**
     * 获取请求体类型
     * <p>同{@link ForestRequest#getBodyType()}方法
     *
     * @return 求体类型, {@link ForestDataType}枚举对象
     * @see ForestRequest#getBodyType()
     */
    public ForestDataType bodyType() {
        return getBodyType();
    }

    /**
     * 设置请求体类型
     * <p>同{@link ForestRequest#setBodyType(ForestDataType)}
     *
     * @param bodyType 求体类型, {@link ForestDataType}枚举对象
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setBodyType(ForestDataType)
     */
    public ForestRequest<T> bodyType(ForestDataType bodyType) {
        return setBodyType(bodyType);
    }

    /**
     * 获取请求类型
     *
     * @return 请求类型, 即 HTTP 方法
     */
    public ForestRequestType getType() {
        return type;
    }

    /**
     * 设置请求类型
     *
     * @param type 请求类型, 即 HTTP 方法
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setType(ForestRequestType type) {
        if (this.type != null) {
            if (this.typeChangeHistory == null) {
                this.typeChangeHistory = new LinkedList<>();
            }
            this.typeChangeHistory.add(this.type);
        }
        this.type = type;
        return this;
    }

    /**
     * 设置请求类型
     * <p>设置请求类型的同事，并情况请求类型变更记录
     *
     * @param type 请求类型, 即 HTTP 方法
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> type(ForestRequestType type) {
        return setType(null)
                .clearTypeChangeHistory()
                .setType(type);
    }

    /**
     * 获取请求类型
     * <p>同{@link ForestRequest#getType()}
     *
     * @return 请求类型, 即 HTTP 方法
     * @see ForestRequest#getType()
     */
    public ForestRequestType type() {
        return getType();
    }

    /**
     * 获取请求类型变更历史列表
     *
     * @return 请求类型列表
     */
    public List<ForestRequestType> getTypeChangeHistory() {
        return typeChangeHistory;
    }

    /**
     * 清空请求类型变更历史列表
     *
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> clearTypeChangeHistory() {
        typeChangeHistory = null;
        return this;
    }

    /**
     * 获取请求类型变更历史字符串列表
     *
     * @return 字符串列表
     */
    public List<String> getTypeChangeHistoryString() {
        if (typeChangeHistory == null) {
            return null;
        }
        List<String> results = new LinkedList<>();
        for (ForestRequestType type : typeChangeHistory) {
            results.add(type.getName());
        }
        return results;
    }


    public RequestLogMessage getRequestLogMessage() {
        return requestLogMessage;
    }

    public void setRequestLogMessage(RequestLogMessage requestLogMessage) {
        this.requestLogMessage = requestLogMessage;
    }

    public String getFilename() {
        if (filename == null) {
            synchronized (this) {
                if (filename == null) {
                    String[] strs = getUrl().split("/");
                    filename = strs[strs.length - 1];
                }
            }
        }
        return filename;
    }

    public String getContentEncoding() {
        return headers.getValue("Content-Encoding");
    }

    public ForestRequest<T> setContentEncoding(String contentEncoding) {
        addHeader("Content-Encoding", contentEncoding);
        return this;
    }

    public String getUserAgent() {
        return headers.getValue("User-Agent");
    }

    public ForestRequest<T> setUserAgent(String userAgent) {
        addHeader("User-Agent", userAgent);
        return this;
    }

    /**
     * 获取请求参数编码字符集
     *
     * @return 请求参数编码字符集
     */
    public String getCharset() {
        return this.charset;
    }

    /**
     * 设置请求参数编码字符集
     *
     * @param charset 请求参数编码字符集
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public Charset mineCharset() {
        if (StringUtils.isNotEmpty(this.charset)) {
            return Charset.forName(this.charset);
        }
        if (StringUtils.isNotEmpty(this.configuration.getCharset())) {
            return Charset.forName(this.configuration.getCharset());
        }
        ContentType mineType = this.mineContentType();
        if (mineType != null && mineType.getCharset() != null) {
            return mineType.getCharset();
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * 获取请求参数编码字符集
     * <p>同{@link ForestRequest#getCharset()}
     *
     * @return 请求参数编码字符集
     * @see ForestRequest#getCharset()
     */
    public String charset() {
        return getCharset();
    }

    /**
     * 设置请求参数编码字符集
     * <p>同{@link ForestRequest#setCharset(String)}
     *
     * @param charset 请求参数编码字符集
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setCharset(String)
     */
    public ForestRequest<T> charset(String charset) {
        return setCharset(charset);
    }


    public String getResponseEncode() {
        return responseEncode;
    }

    public ForestRequest<T> setResponseEncode(String responseEncode) {
        this.responseEncode = responseEncode;
        return this;
    }

    /**
     * 是否异步
     *
     * @return {@code true}: 异步, {@code false}: 同步
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * 设置是否异步
     *
     * @param async {@code true}: 异步, {@code false}: 同步
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setAsync(boolean async) {
        this.async = async;
        return this;
    }

    /**
     * 获取异步请求模式
     * <p>该字段只有在 async = true 时有效</p>
     *
     * @return {@link ForestAsyncMode}枚举实例
     * @since 1.5.27
     */
    public ForestAsyncMode getAsyncMode() {
        return asyncMode;
    }

    /**
     * 设置异步请求模式
     * <p>该字段只有在 async = true 时有效</p>
     *
     * @param asyncMode {@link ForestAsyncMode}枚举实例
     * @return {@link ForestRequest}类实例
     * @since 1.5.27
     */
    public ForestRequest<T> setAsyncMode(ForestAsyncMode asyncMode) {
        this.asyncMode = asyncMode;
        return this;
    }

    /**
     * 获取异步请求模式
     * <p>该字段只有在 async = true 时有效</p>
     *
     * @return {@link ForestAsyncMode}枚举实例
     * @since 1.5.27
     */
    public ForestAsyncMode asyncMode() {
        return getAsyncMode();
    }

    /**
     * 设置异步请求模式
     * <p>该字段只有在 async = true 时有效</p>
     *
     * @param asyncMode 异步模式
     * @return {@link ForestRequest}类实例
     * @since 1.5.27
     */
    public ForestRequest<T> asyncMode(ForestAsyncMode asyncMode) {
        return setAsyncMode(asyncMode);
    }


    /**
     * 获取请求认证器
     *
     * @return 请求认证器, {@link ForestAuthenticator}接口实例
     * @since 1.5.28
     */
    public ForestAuthenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * 设置请求认证器
     *
     * @param authenticator 请求认证器, {@link ForestAuthenticator}接口实例
     * @return {@link ForestRequest}类实例
     * @since 1.5.28
     */
    public ForestRequest<T> setAuthenticator(ForestAuthenticator authenticator) {
        this.authenticator = authenticator;
        return this;
    }

    /**
     * 获取请求认证器
     *
     * @return 请求认证器, {@link ForestAuthenticator}接口实例
     * @since 1.5.28
     */
    public ForestAuthenticator authenticator() {
        return authenticator;
    }

    /**
     * 设置请求认证器
     *
     * @param authenticator 请求认证器, {@link ForestAuthenticator}接口实例
     * @return {@link ForestRequest}类实例
     * @since 1.5.28
     */
    public ForestRequest<T> authenticator(ForestAuthenticator authenticator) {
        this.authenticator = authenticator;
        return this;
    }


    /**
     * 是否打开自动重定向
     *
     * @return {@code true}: 打开, {@code false}: 禁止
     */
    public boolean isAutoRedirection() {
        return autoRedirection;
    }

    /**
     * 获取重定向上一个的请求对象
     * <p>触发重定向时的第一个请求
     *
     * @return 定向上一个的请求对象
     */
    public ForestRequest<?> getPrevRequest() {
        return prevRequest;
    }

    /**
     * 获取重定向的上一个响应
     * <p>触发重定向时的第一个响应，即带有301、302等状态码的响应
     *
     * @return 重定向的上一个响应
     */
    public ForestResponse<?> getPrevResponse() {
        return prevResponse;
    }

    /**
     * 是否为重定向请求
     * <p>即是否为接受到 301, 302 等重定向状态码后进行地址转移的那个请求
     *
     * @return {@code true}: 是重定向请求，{@code false}: 不是
     */
    public boolean isRedirection() {
        return this.prevRequest != null && this.prevResponse != null;
    }

    /**
     * 设置是否打开自动重定向
     *
     * @param autoRedirection {@code true}: 打开, {@code false}: 禁止
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setAutoRedirection(boolean autoRedirection) {
        this.autoRedirection = autoRedirection;
        return this;
    }

    /**
     * 设置是否打开自动重定向
     * <p>同 {@link ForestRequest#setAutoRedirection(boolean)}
     *
     * @param autoRedirects {@code true}: 打开, {@code false}: 禁止
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setAutoRedirection(boolean)
     */
    public ForestRequest<T> autoRedirects(boolean autoRedirects) {
        return setAutoRedirection(autoRedirects);
    }


    /**
     * 设置为同步
     *
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setAsync(boolean)
     */
    public ForestRequest<T> sync() {
        return setAsync(false);
    }

    /**
     * 设置为异步
     *
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setAsync(boolean)
     */
    public ForestRequest<T> async() {
        return setAsync(true);
    }



    /**
     * 获取请求体对象列表
     *
     * @return 请求体对象列表, 元素为 {@link ForestRequestBody} 其子类实例
     */
    public ForestBody getBody() {
        return body;
    }

    /**
     * 获取请求体对象列表
     * <p>同{@link ForestRequest#getBody()}
     *
     * @return 请求体对象列表, 元素为 {@link ForestRequestBody} 其子类实例
     * @see ForestRequest#getBody()
     */
    public ForestBody body() {
        return getBody();
    }

    @Deprecated
    public List getBodyList() {
        return body;
    }

    @Deprecated
    public void setBodyList(ForestBody body) {
    }

    public ForestDataType getDataType() {
        return dataType;
    }

    public ForestRequest<T> setDataType(ForestDataType dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * 获取请求头 Content-Type 的值
     *
     * @return 请求头 Content-Type 的值
     */
    public String getContentType() {
        return headers.getValue(ForestHeader.CONTENT_TYPE);
    }

    /**
     * 设置请求头 Content-Type 的值
     *
     * @param contentType 请求头 Content-Type 的值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setContentType(String contentType) {
        addHeader(ForestHeader.CONTENT_TYPE, contentType);
        return this;
    }

    /**
     * 设置请求头 Content-Type 的值
     * <p>同 {@link ForestRequest#setContentType(String)}
     *
     * @param contentType 请求头 Content-Type 的值
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setContentType(String)
     */
    public ForestRequest<T> contentType(String contentType) {
        return setContentType(contentType);
    }


    /**
     * 获取请求头 Content-Type 的 MINE 对象
     *
     * @return 请求头 Content-Type 的 MINE 对象
     * @since 1.5.29
     */
    public ContentType mineContentType() {
        String contentType = getContentType();

        if (StringUtils.isEmpty(contentType)) {
            contentType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
        }

        String[] typeGroup = contentType.split(";[ ]*charset=");
        String mineType = typeGroup[0];
        boolean hasDefinedCharset = typeGroup.length > 1;

        if (StringUtils.isEmpty(mineType)) {
            mineType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
        }

        Charset mineCharset = null;
        String strCharset = this.getCharset();
        if (StringUtils.isEmpty(strCharset)) {
            strCharset = this.configuration.getCharset();
        }
        if (StringUtils.isEmpty(strCharset)) {
            mineCharset = StandardCharsets.UTF_8;
        } else {
            mineCharset = Charset.forName(strCharset);
        }
        ContentType mineContentType = new ContentType(mineType, mineCharset);
        if (hasDefinedCharset) {
            mineContentType.definedCharsetName(typeGroup[1]);
        }
        return mineContentType;
    }


    /**
     * 设置请求头 Content-Type 的值为 application/x-www-form-urlencoded
     *
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> contentFormUrlEncoded() {
        return setContentType(ContentType.APPLICATION_X_WWW_FORM_URLENCODED);
    }

    /**
     * 设置请求头 Content-Type 的值为 application/json
     *
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> contentTypeJson() {
        return setContentType(ContentType.APPLICATION_JSON);
    }

    /**
     * 设置请求头 Content-Type 的值为 application/json
     *
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> contentTypeXml() {
        return setContentType(ContentType.APPLICATION_XML);
    }

    /**
     * 设置请求头 Content-Type 的值为 application/octet-stream
     *
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> contentTypeOctetStream() {
        return setContentType(ContentType.APPLICATION_OCTET_STREAM);
    }

    /**
     * 设置请求头 Content-Type 的值为 multipart/form-data
     *
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> contentTypeMultipartFormData() {
        return setContentType(ContentType.MULTIPART_FORM_DATA);
    }


    /**
     * 获取请求超时时间，时间单位为毫秒
     *
     * @return 请求超时时间
     */
    @Deprecated
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置请求超时时间，时间单位为毫秒
     *
     * @param timeout 请求超时时间
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest<T> setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 获取连接超时时间，时间单位为毫秒
     *
     * @return 连接超时时间
     */
    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * 设置连接超时时间，时间单位为毫秒
     *
     * @param connectTimeout 连接超时时间
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 获取连接超时时间，时间单位为毫秒
     * <p>同{@link ForestRequest#getConnectTimeout()}
     *
     * @return 连接超时时间
     * @see ForestRequest#getConnectTimeout()
     * @since 1.5.6
     */
    public Integer connectTimeout() {
        return getConnectTimeout();
    }

    /**
     * 设置连接超时时间，时间单位为毫秒
     * <p>同{@link ForestRequest#setConnectTimeout(int)}
     *
     * @param connectTimeout 连接超时时间
     * @see ForestRequest#setConnectTimeout(int)
     * @return {@link ForestRequest}类实例
     * @since 1.5.6
     */
    public ForestRequest<T> connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * 设置连接超时时间
     *
     * @param connectTimeout 连接超时时间, 整数
     * @param timeUnit 时间单位
     * @return {@link ForestRequest}类实例
     * @since 1.5.6
     */
    public ForestRequest<T> connectTimeout(int connectTimeout, TimeUnit timeUnit) {
        this.connectTimeout = TimeUtils.toMillis("connect timeout", connectTimeout, timeUnit);
        return this;
    }

    /**
     * 设置连接超时时间
     *
     * @param connectTimeout 连接超时时间, {@link Duration}对象
     * @return {@link ForestRequest}类实例
     * @since 1.5.6
     */
    public ForestRequest<T> connectTimeout(Duration connectTimeout) {
        this.connectTimeout = TimeUtils.toMillis("connect timeout", connectTimeout);
        return this;
    }


    /**
     * 获取读取超时时间，时间单位为毫秒
     *
     * @return 读取超时时间
     * @since 1.5.6
     */
    public Integer getReadTimeout() {
        return readTimeout;
    }

    /**
     * 设置读取超时时间，时间单位为毫秒
     *
     * @param readTimeout 读取超时时间
     * @return {@link ForestRequest}类实例
     * @since 1.5.6
     */
    public ForestRequest<T> setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * 获取读取超时时间，时间单位为毫秒
     * <p>同{@link ForestRequest#getReadTimeout()}
     *
     * @return 读取超时时间
     * @see ForestRequest#getReadTimeout()
     * @since 1.5.6
     */
    public Integer readTimeout() {
        return getReadTimeout();
    }

    /**
     * 设置读取超时时间，时间单位为毫秒
     * <p>同{@link ForestRequest#setReadTimeout(int)}
     *
     * @param readTimeout 读取超时时间
     * @see ForestRequest#setReadTimeout(int)
     * @return {@link ForestRequest}类实例
     * @since 1.5.6
     */
    public ForestRequest<T> readTimeout(int readTimeout) {
        return setReadTimeout(readTimeout);
    }

    /**
     * 设置读取超时时间
     *
     * @param readTimeout 读取超时时间
     * @param timeUnit 时间单位
     * @return {@link ForestRequest}类实例
     * @since 1.5.6
     */
    public ForestRequest<T> readTimeout(int readTimeout, TimeUnit timeUnit) {
        this.readTimeout = TimeUtils.toMillis("read timeout", readTimeout, timeUnit);
        return this;
    }

    /**
     * 设置读取超时时间
     *
     * @param readTimeout 读取超时时间, {@link Duration}对象
     * @return {@link ForestRequest}类实例
     * @since 1.5.6
     */
    public ForestRequest<T> readTimeout(Duration readTimeout) {
        this.readTimeout = TimeUtils.toMillis("read timeout", readTimeout);
        return this;
    }


    /**
     * 是否开启解压GZIP响应内容
     *
     * @return {@code true}为开启，否则不开启
     */
    public boolean isDecompressResponseGzipEnabled() {
        return decompressResponseGzipEnabled;
    }

    /**
     * 设置是否开启解压GZIP响应内容
     *
     * @param decompressResponseGzipEnabled {@code true}为开启，否则不开启
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setDecompressResponseGzipEnabled(boolean decompressResponseGzipEnabled) {
        this.decompressResponseGzipEnabled = decompressResponseGzipEnabled;
        return this;
    }

    /**
     * 获取SSL协议
     *
     * @return SSL协议字符串
     */
    public String getSslProtocol() {
        return sslProtocol;
    }

    /**
     * 设置SSL协议
     *
     * @param sslProtocol SSL协议字符串
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
        return this;
    }

    /**
     * 获取SSL主机名/域名验证器
     *
     * @return SSL域名验证器, 即{@link HostnameVerifier}接口实例
     */
    public HostnameVerifier getHostnameVerifier() {
        if (hostnameVerifier != null) {
            return hostnameVerifier;
        }
        if (keyStore == null) {
            return TrustAllHostnameVerifier.DEFAULT;
        }
        HostnameVerifier hostnameVerifier = keyStore.getHostnameVerifier();
        if (hostnameVerifier == null) {
            hostnameVerifier = TrustAllHostnameVerifier.DEFAULT;
        }
        return hostnameVerifier;
    }

    /**
     * 设置SSL主机名/域名验证器
     *
     * @param hostnameVerifier SSL主机名/域名验证器
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    /**
     * 获取SSL主机名/域名验证器
     * <p>同{@link ForestRequest#getHostnameVerifier()}方法
     *
     * @return SSL域名验证器, 即{@link HostnameVerifier}接口实例
     */
    public HostnameVerifier hostnameVerifier() {
        return getHostnameVerifier();
    }

    /**
     * 设置SSL主机名/域名验证器
     * <p>同{@link ForestRequest#setHostnameVerifier(HostnameVerifier)}方法
     *
     * @param hostnameVerifier SSL主机名/域名验证器
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> hostnameVerifier(HostnameVerifier hostnameVerifier) {
        return this.setHostnameVerifier(hostnameVerifier);
    }

    /**
     * 获取 SSL Socket 工厂构造器
     *
     * @return SSL Socket 工厂构造器
     */
    public SSLSocketFactoryBuilder getSslSocketFactoryBuilder() {
        return sslSocketFactoryBuilder;
    }

    /**
     * 设置 SSL Socket 工厂构造器
     *
     * @param sslSocketFactoryBuilder SSL Socket 工厂构造器
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setSslSocketFactoryBuilder(SSLSocketFactoryBuilder sslSocketFactoryBuilder) {
        this.sslSocketFactoryBuilder = sslSocketFactoryBuilder;
        return this;
    }

    /**
     * 获取 SSL Socket 工厂构造器
     *
     * @return SSL Socket 工厂构造器
     */
    public SSLSocketFactoryBuilder sslSocketFactoryBuilder() {
        return getSslSocketFactoryBuilder();
    }

    /**
     * 设置 SSL Socket 工厂构造器
     *
     * @param sslSocketFactoryBuilder SSL Socket 工厂构造器
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> sslSocketFactoryBuilder(SSLSocketFactoryBuilder sslSocketFactoryBuilder) {
        return setSslSocketFactoryBuilder(sslSocketFactoryBuilder);
    }

    /**
     * 后端客户端对象缓存 Key
     *
     * @return 缓存 Key 字符串
     * @since 1.5.23
     */
    public String clientKey() {
        int timeout = getTimeout();
        Integer connectTimeout = connectTimeout();
        Integer readTimeout = readTimeout();
        if (TimeUtils.isNone(connectTimeout)) {
            connectTimeout = timeout;
        }
        if (TimeUtils.isNone(readTimeout)) {
            readTimeout = timeout;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(getProtocol())
                .append("/")
                .append(getHost())
                .append(":")
                .append(getPort())
                .append("-t=")
                .append(connectTimeout)
                .append("-")
                .append(readTimeout);

        if (isSSL()) {
            if (sslSocketFactoryBuilder != null) {
                builder.append(",-sf=").append(Objects.hash(sslSocketFactoryBuilder));
            }
            if (keyStore != null) {
                builder.append(",-ks=").append(keyStore.getId());
            } else if (sslProtocol != null) {
                builder.append(",-sp=").append(sslProtocol);
            }
        }

        if (proxy != null) {
            builder.append(proxy.cacheKey());
        }

        return builder.toString();
    }


    /**
     * 获取 SSL Socket 工厂
     *
     * @return SSL Socket 工厂
     */
    public SSLSocketFactory getSSLSocketFactory() {
        if (sslSocketFactoryBuilder != null) {
            try {
                return sslSocketFactoryBuilder.getSSLSocketFactory(this, getSslProtocol());
            } catch (Exception e) {
                throw new ForestRuntimeException(e);
            }
        }
        SSLKeyStore keyStore = this.getKeyStore();
        if (keyStore == null) {
            return SSLUtils.getDefaultSSLSocketFactory(this, getSslProtocol());
        }
        SSLSocketFactoryBuilder sslSocketFactoryBuilder = keyStore.getSslSocketFactoryBuilder();
        if (sslSocketFactoryBuilder == null) {
            return SSLUtils.getDefaultSSLSocketFactory(this, getSslProtocol());
        }
        try {
            SSLSocketFactory sslSocketFactory = sslSocketFactoryBuilder.getSSLSocketFactory(this, getSslProtocol());
            if (sslSocketFactory == null) {
                return SSLUtils.getDefaultSSLSocketFactory(this, getSslProtocol());
            }
            return sslSocketFactory;
        } catch (Exception e) {
            throw new ForestRuntimeException(e);
        }
    }


    /**
     * 是否为HTTPS请求
     * @return {@code true}: 是HTTPS请求，{@code false}: 不是
     */
    public boolean isSSL() {
        return url.isSSL();
    }

    /**
     * 获取请求失败后的重试次数
     *
     * @return 重试次数
     */
    @Deprecated
    public int getRetryCount() {
        return maxRetryCount;
    }

    /**
     * 设置请求失败后的重试次数
     *
     * @param retryCount 重试次数
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest<T> setRetryCount(int retryCount) {
        this.maxRetryCount = retryCount;
        return this;
    }

    /**
     * 获取请求失败后的重试次数
     *
     * @return 重试次数
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    /**
     * 设置请求失败后的重试次数
     *
     * @param retryCount 重试次数
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setMaxRetryCount(int retryCount) {
        this.maxRetryCount = retryCount;
        return this;
    }

    /**
     * 设置请求失败后的重试次数
     * <p> {@link ForestRequest#getMaxRetryCount()}
     *
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#getMaxRetryCount()
     */
    public int maxRetryCount() {
        return getMaxRetryCount();
    }


    /**
     * 设置请求失败后的重试次数
     * <p> {@link ForestRequest#setMaxRetryCount(int)}
     *
     * @param retryCount 重试次数
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setMaxRetryCount(int)
     */
    public ForestRequest<T> maxRetryCount(int retryCount) {
        return setMaxRetryCount(retryCount);
    }

    /**
     * 获取请求当前重试次数
     *
     * @return 当前重试次数
     */
    public int getCurrentRetryCount() {
        return retryer.getCurrentRetryCount();
    }


    /**
     * 获取最大请重试的时间间隔，时间单位为毫秒
     *
     * @return 最大请重试的时间间隔
     */
    public long getMaxRetryInterval() {
        return maxRetryInterval;
    }

    /**
     * 是否开启请求重试
     *
     * @return {@code true}为开启重试, {@code false}为关闭重试
     */
    public boolean isRetryEnabled() {
        return retryEnabled;
    }

    /**
     * 设置是否开启请求重试
     *
     * @param retryEnabled {@code true}为开启重试, {@code false}为关闭重试
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setRetryEnabled(boolean retryEnabled) {
        this.retryEnabled = retryEnabled;
        return this;
    }

    /**
     * 设置最大请重试的时间间隔，时间单位为毫秒
     *
     * @param maxRetryInterval 最大请重试的时间间隔
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setMaxRetryInterval(long maxRetryInterval) {
        this.maxRetryInterval = maxRetryInterval;
        return this;
    }

    /**
     * 设置最大请重试的时间间隔，时间单位为毫秒
     * <p>同 {@link ForestRequest#setMaxRetryInterval(long)}
     *
     * @param maxRetryInterval 最大请重试的时间间隔
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setMaxRetryInterval(long)
     */
    public ForestRequest<T> maxRetryInterval(long maxRetryInterval) {
        return setMaxRetryInterval(maxRetryInterval);
    }


    /**
     * 旧的获取Body数据的方法 [已不建议使用]
     *
     * @return 请求中的数据，{@link Map}对象实例
     */
    @Deprecated
    public Map<String, Object> getData() {
        return null;
    }

    /**
     * 添加Body数据
     *
     * @param body Forest请求体，{@link ForestRequestBody}类实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addBody(ForestRequestBody body) {
        this.body.add(body);
        return this;
    }


    /**
     * 添加字符串Body数据
     *
     * @param stringBody 请求体字符串内容
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addBody(String stringBody) {
        return addBody(new StringRequestBody(stringBody));
    }

    /**
     * 添加字节数组Body数据
     *
     * @param byteArrayBody 请求体字节数组内容
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addBody(byte[] byteArrayBody) {
        return addBody(new ByteArrayRequestBody(byteArrayBody));
    }

    /**
     * 添加文件Body数据
     *
     * @param fileBody 请求体文件内容
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addBody(File fileBody) {
        return addBody(new FileRequestBody(fileBody));
    }

    /**
     * 添加输入流Body数据
     *
     * @param inputStreamBody 请求体输入流内容
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addBody(InputStream inputStreamBody) {
        return addBody(new InputStreamRequestBody(inputStreamBody));
    }

    /**
     * 添加输入对象Body数据
     *
     * @param obj 请求体对象内容
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addBody(Object obj) {
        return addBody(new ObjectRequestBody(obj));
    }

    /**
     * 添加键值对类型Body数据
     *
     * @param name 字段名
     * @param value 字段值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addBody(String name, Object value) {
        return addBody(new NameValueRequestBody(name, value));
    }

    /**
     * 添加延迟求值的键值对类型Body数据
     *
     * @param name 字段名
     * @param value 延迟求值的字段值
     * @return {@link ForestRequest}类实例
     * @since 1.5.29
     */
    public ForestRequest<T> addBody(String name, Lazy value) {
        return addBody(new NameValueRequestBody(name, value));
    }


    /**
     * 添加 Map 类型 Body 数据
     * <p>将 Map 的 key 作为键值对的 key
     * <p>Map 的 value 作为键值对的 value
     * <p>批量插入到请求的请求体中
     *
     * @param bodyMap 字段名
     * @param contentType 该请求体项的Content-Type
     * @return {@link ForestRequest}类实例
     * @since 1.5.4
     */
    public ForestRequest<T> addBody(Map bodyMap, String contentType) {
        if (bodyMap == null) {
            return this;
        }
        if (bodyMap.isEmpty()) {
            addBody(new ObjectRequestBody(bodyMap));
            return this;
        }
        for (Object key : bodyMap.keySet()) {
            Object value = bodyMap.get(key);
            addBody(String.valueOf(key), contentType, value);
        }
        return this;
    }


    /**
     * 添加键值对类型Body数据
     *
     * @param name 字段名
     * @param contentType 该请求体项的Content-Type
     * @param value 字段值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addBody(String name, String contentType, Object value) {
        return addBody(new NameValueRequestBody(name, contentType, value));
    }

    /**
     * 添加键值对类型Body数据
     *
     * @param nameValue 请求键值对对象
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest<T> addBody(RequestNameValue nameValue) {
        return addBody(new NameValueRequestBody(nameValue.getName(), nameValue.getValue()));
    }

    /**
     * 批量添加键值对类型Body数据
     *
     * @param nameValueList 请求键值对对象列表
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest<T> addBody(List<RequestNameValue> nameValueList) {
        for (RequestNameValue nameValue : nameValueList) {
            addBody(nameValue);
        }
        return this;
    }

    /**
     * 添加键值对类型Body数据, 已不再建议使用
     *
     * @param name 键值对名称
     * @param value 键值对的值
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest<T> addData(String name, Object value) {
        return addBody(name, value);
    }

    /**
     * 添加键值对类型Body数据, 已不再建议使用
     *
     * @param nameValue 请求键值对对象
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest<T> addData(RequestNameValue nameValue) {
        return addNameValue(nameValue);
    }

    /**
     * 批量添加键值对类型Body数据, 已不再建议使用
     *
     * @param data 请求键值对对象列表
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest<T> addData(List<RequestNameValue> data) {
        return addNameValue(data);
    }

    /**
     * 添加键值对
     *
     * @param nameValue 键值对对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addNameValue(RequestNameValue nameValue) {
        if (nameValue.isInHeader()) {
            this.addHeader(nameValue.getName(), nameValue.getValue());
        } else if (nameValue.isInQuery()) {
            this.addQuery(nameValue.getName(), nameValue.getValue());
        } else if (nameValue.isInBody()) {
            this.addBody(nameValue.getName(), nameValue.getPartContentType(), nameValue.getValue());
        }
        return this;
    }

    /**
     * 添加键值对列表
     *
     * @param nameValueList 键值对列表
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addNameValue(List<RequestNameValue> nameValueList) {
        for (RequestNameValue nameValue : nameValueList) {
            addNameValue(nameValue);
        }
        return this;
    }

    /**
     * 替换Body数据，原有的Body数据将被清空
     *
     * @param body 请求体对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> replaceBody(ForestRequestBody body) {
        this.body.clear();
        this.addBody(body);
        return this;
    }

    /**
     * 替换Body为新的字符串数据，原有的Body数据将被清空
     *
     * @param stringbody 字符串请求体
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> replaceBody(String stringbody) {
        this.body.clear();
        this.addBody(stringbody);
        return this;
    }

    @Deprecated
    public List<RequestNameValue> getQueryNameValueList() {
        List<RequestNameValue> nameValueList = new ArrayList<>();
        for (Iterator<Map.Entry<String, Object>> iterator = query.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            String name = entry.getKey();
            Object value = entry.getValue();
            RequestNameValue nameValue = new RequestNameValue(name, value, TARGET_QUERY);
            nameValueList.add(nameValue);
        }
        return nameValueList;
    }

    public List<SimpleQueryParameter> getQueryValues() {
        return query.queryValues();
    }

    public List<RequestNameValue> getDataNameValueList() {
        List<RequestNameValue> nameValueList = new ArrayList<>();
        for (ForestRequestBody item : body) {
            if (item instanceof NameValueRequestBody) {
                NameValueRequestBody nameValueRequestBody = (NameValueRequestBody) item;
                String name = nameValueRequestBody.getName();
                Object value = nameValueRequestBody.getValue();
                RequestNameValue nameValue = new RequestNameValue(name, value, TARGET_BODY, nameValueRequestBody.getContentType());
                nameValueList.add(nameValue);
            }
        }
        return nameValueList;
    }


    public List<RequestNameValue> getHeaderNameValueList() {
        final List<RequestNameValue> nameValueList = new LinkedList<>();
        boolean hasUserAgent = false;
        for (Iterator<ForestHeader> iterator = headers.headerIterator(); iterator.hasNext(); ) {
            final ForestHeader header = iterator.next();
            final String name = header.getName();
            final String value = header.getValue();
            RequestNameValue nameValue = new RequestNameValue(name, value, TARGET_HEADER);
            nameValueList.add(nameValue);
            if (!hasUserAgent && ForestHeader.USER_AGENT.equalsIgnoreCase(name)) {
                hasUserAgent = true;
            }
        }
        if (!hasUserAgent) {
            nameValueList.add(0, new RequestNameValue(
                    ForestHeader.USER_AGENT,
                    ForestHeader.DEFAULT_USER_AGENT_VALUE,
                    TARGET_HEADER));
        }
        return nameValueList;
    }

    private ForestRequest<T> arguments(Object... args) {
        this.arguments = new Object[args.length];
        for (int i = 0; i < this.arguments.length; i++) {
            this.arguments[i] = args[i];
        }
        return this;
    }

    /**
     * 根据参数下标获取该请求对应方法的参数值
     *
     * @param index 对应方法的参数下标
     * @return 参数值
     */
    public Object getArgument(int index) {
        return arguments[index];
    }

    /**
     * 根据参数下标获取该请求对应方法的参数值
     * <p>同 {@link ForestRequest#getArgument(int)}}
     *
     * @param index 对应方法的参数下标
     * @return 参数值
     * @see ForestRequest#getArgument(int)
     */
    public Object argument(int index) {
        return arguments[index];
    }


    /**
     * 获取该请求对应方法的所有参数值
     *
     * @return 参数值列表
     */
    public Object[] getArguments() {
        return arguments;
    }

    /**
     * 获取该请求对应方法的所有参数值
     * <p>同 {@link ForestRequest#getArguments()}
     *
     * @return 参数值列表
     * @see ForestRequest#getArguments()
     */
    public Object[] arguments() {
        return getArguments();
    }


    /**
     * 获取该请求的所有请求头信息
     *
     * @return 请求头表，{@link ForestHeaderMap}类实例
     */
    @Override
    public ForestHeaderMap getHeaders() {
        return headers;
    }

    /**
     * 获取该请求的所有请求头信息
     * <p>同 {@link ForestRequest#getHeaders()}
     *
     * @return 请求头表，{@link ForestHeaderMap}类实例
     * @see ForestRequest#getHeaders()
     */
    public ForestHeaderMap headers() {
        return getHeaders();
    }


    /**
     * 根据请求头名称获取该请求的请求头信息
     *
     * @param name 请求头名称
     * @return 请求头，{@link ForestHeader}类实例
     */
    public ForestHeader getHeader(String name) {
        return headers.getHeader(name);
    }

    /**
     * 根据请求头名称获取该请求的请求头信息
     * <p>同 {@link ForestRequest#getHeader(String)}
     *
     * @param name 请求头名称
     * @return 请求头，{@link ForestHeader}类实例
     * @see ForestRequest#getHeader(String)
     */
    public ForestHeader header(String name) {
        return getHeader(name);
    }


    /**
     * 根据请求头名称获取该请求的请求头的值
     *
     * @param name 请求头名称
     * @return 请求头的值
     */
    public String getHeaderValue(String name) {
        return headers.getValue(name);
    }

    /**
     * 根据请求头名称获取该请求的请求头的值
     * <p>同 {@link ForestRequest#getHeaderValue(String)}
     *
     * @param name 请求头名称
     * @return 请求头的值
     * @see ForestRequest#getHeaderValue(String)
     */
    public String headerValue(String name) {
        return getHeaderValue(name);
    }

    /**
     * 通过 Map 批量添加请求头到该请求中
     *
     * @param headerMap 请求头表，{@link Map}对象
     * @return {@link ForestRequest}类实例
     * @since 1.5.4
     */
    public ForestRequest<T> addHeader(Map headerMap) {
        this.headers.setHeader(headerMap);
        return this;
    }


    /**
     * 添加请求头到该请求中
     *
     * @param name 请求头名称
     * @param value 请求头的值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addHeader(String name, Object value) {
        if (StringUtils.isEmpty(name)) {
            return this;
        }
        if (value instanceof Lazy) {
            return addHeader(name, (Lazy) value);
        }
        this.headers.setHeader(name, String.valueOf(value));
        return this;
    }

    /**
     * 添加延迟求值的请求头到该请求中
     *
     * @param name 请求头名称
     * @param value 延迟求值的 Lambda
     * @return {@link ForestRequest}类实例
     * @since 1.5.29
     */
    public ForestRequest<T> addHeader(String name, Lazy<?> value) {
        if (value == null) {
            return this;
        }
        this.headers.setHeader(name, value);
        return this;
    }


    /**
     * 添加请求头到该请求中
     *
     * @param nameValue 请求头键值对，{@link RequestNameValue}类实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequestConditionWrapper<T> ifThenHeader(boolean condition, RequestNameValue nameValue) {
        return ifThen(condition, q -> q.addHeader(nameValue));
    }

    /**
     * 添加请求头到该请求中
     *
     * @param nameValue 请求头键值对，{@link RequestNameValue}类实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addHeader(RequestNameValue nameValue) {
        this.addHeader(nameValue.getName(), nameValue.getValue());
        return this;
    }

    /**
     * 批量添加请求头到该请求中
     *
     * @param nameValues 请求头键值对列表
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addHeaders(List<RequestNameValue> nameValues) {
        for (RequestNameValue nameValue : nameValues) {
            this.addHeader(nameValue.getName(), nameValue.getValue());
        }
        return this;
    }

    /**
     * 添加 Cookie 到请求中 (默认严格匹配)
     *
     * @param cookie {@link ForestCookie}对象实例
     * @return {@link ForestRequest}类实例
     * @since 1.5.23
     */
    public ForestRequest<T> addCookie(ForestCookie cookie) {
        this.headers.addCookie(cookie);
        return this;
    }

    /**
     * 添加 Cookie 到请求中
     *
     * @param cookie {@link ForestCookie}对象实例
     * @param strict 是否严格匹配（只有匹配域名，以及没过期的 Cookie 才能添加）
     * @return {@link ForestRequest}类实例
     * @since 1.5.25
     */
    public ForestRequest<T> addCookie(ForestCookie cookie, boolean strict) {
        this.headers.addCookie(cookie, strict);
        return this;
    }


    /**
     * 批量添加 Cookie 到请求中 (默认严格匹配)
     *
     * @param cookies {@link ForestCookie}对象列表
     * @return {@link ForestRequest}类实例
     * @since 1.5.23
     */
    public ForestRequest<T> addCookies(List<ForestCookie> cookies) {
        this.headers.addCookies(cookies);
        return this;
    }

    /**
     * 批量添加 Cookie 到请求中
     *
     * @param cookies {@link ForestCookie}对象列表
     * @param strict 是否严格匹配（只有匹配域名，以及没过期的 Cookie 才能添加）
     * @return 1.5.25
     */
    public ForestRequest<T> addCookies(List<ForestCookie> cookies, boolean strict) {
        this.headers.addCookies(cookies, strict);
        return this;
    }


    /**
     * 批量添加 Cookie 到请求中
     *
     * @param cookies {@link ForestCookies}对象实例
     * @return {@link ForestRequest}类实例
     * @since 1.5.23
     */
    public ForestRequest<T> addCookies(ForestCookies cookies) {
        this.headers.addCookies(cookies);
        return this;
    }


    public List<ForestMultipart> getMultiparts() {
        return this.body.getMultipartItems().stream()
                .map(body -> body.getMultipart())
                .collect(Collectors.toList());
    }

    /**
     * 设置 Multiparts 列表
     *
     * @param multiparts {@link ForestMultipart} 对象列表
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setMultiparts(List<ForestMultipart> multiparts) {
        this.body.remove(MultipartRequestBody.class);
        return addMultipart(multiparts);
    }

    /**
     * 添加 Multipart
     *
     * @param multipart {@link ForestMultipart} 对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addMultipart(ForestMultipart multipart) {
        this.body.add(new MultipartRequestBody(multipart));
        return this;
    }

    /**
     * 批量添加 Multipart
     *
     * @param multiparts {@link ForestMultipart} 对象列表
     * @return {@link ForestRequest}类实例
     * @since 1.5.29
     */
    public ForestRequest<T> addMultipart(List<ForestMultipart> multiparts) {
        for (ForestMultipart multipart : multiparts) {
            this.body.add(new MultipartRequestBody(multipart));
        }
        return this;
    }


    /**
     * 添加文件 Multipart
     *
     * @param multipart 文件, {@link FileMultipart} 对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addFile(ForestMultipart multipart) {
        return addMultipart(multipart);
    }


    /**
     * 批量添加文件 Multipart
     *
     * @param multiparts {@link ForestMultipart} 对象列表
     * @return {@link ForestRequest}类实例
     * @since 1.5.29
     */
    public ForestRequest<T> addFile(List<ForestMultipart> multiparts) {
        return addMultipart(multiparts);
    }

    /**
     * 添加文件 Multipart
     *
     * @param name Multipart 参数名称
     * @param file 文件, {@link FileMultipart} 对象
     * @param filename 文件名
     * @param contentType 文件内容类型
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addFile(String name, File file, String filename, String contentType) {
        return addMultipart(new FileMultipart()
                .setName(name)
                .setContentType(contentType)
                .setData(file)
                .setFileName(file.getName()));
    }

    /**
     * 添加文件 Multipart
     *
     * @param name Multipart 参数名称
     * @param file 文件, {@link FileMultipart} 对象
     * @param filename 文件名
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addFile(String name, File file, String filename) {
        return addFile(name, file, filename, null);
    }

    /**
     * 添加文件 Multipart
     *
     * @param name Multipart 参数名称
     * @param file 文件, {@link FileMultipart} 对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addFile(String name, File file) {
        return addFile(name, file, file.getName(), null);
    }

    /**
     * 添加文件 Multipart
     *
     * @param name Multipart 参数名称
     * @param inputStream 文件输入流对象, {@link InputStream} 接口实例
     * @param filename 文件名
     * @param contentType 文件内容类型
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addFile(String name, InputStream inputStream, String filename, String contentType) {
        return addMultipart(new InputStreamMultipart()
                .setName(name)
                .setData(inputStream)
                .setFileName(filename)
                .setContentType(contentType));
    }

    /**
     * 添加文件 Multipart
     *
     * @param name Multipart 参数名称
     * @param inputStream 文件输入流对象, {@link InputStream} 接口实例
     * @param filename 文件名
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addFile(String name, InputStream inputStream, String filename) {
        return addFile(name, inputStream, filename, null);
    }


    /**
     * 添加文件 Multipart
     *
     * @param name Multipart 参数名称
     * @param bytes 文件字节数组
     * @param filename 文件名
     * @param contentType 文件内容类型
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addFile(String name, byte[] bytes, String filename, String contentType) {
        return addFile(new ByteArrayMultipart()
                .setName(name)
                .setData(bytes)
                .setFileName(filename)
                .setContentType(contentType));
    }

    /**
     * 添加文件 Multipart
     *
     * @param name Multipart 参数名称
     * @param bytes 文件字节数组
     * @param filename 文件名
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addFile(String name, byte[] bytes, String filename) {
        return addFile(name, bytes, filename, null);
    }


    /**
     * 获取OnSuccess回调函数，该回调函数在请求成功时被调用
     *
     * @return {@link OnSuccess}接口实例
     */
    public OnSuccess getOnSuccess() {
        return onSuccess;
    }

    /**
     * 设置OnSuccess回调函数，该回调函数在请求成功时被调用
     *
     * @param onSuccess {@link OnSuccess}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setOnSuccess(OnSuccess onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    /**
     * 设置OnSuccess回调函数，该回调函数在请求成功时被调用
     * <p>同 {@link ForestRequest#setOnSuccess(OnSuccess)}
     *
     * @param onSuccess {@link OnSuccess}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setOnSuccess(OnSuccess)
     */
    public ForestRequest<T> onSuccess(OnSuccess onSuccess) {
        return setOnSuccess(onSuccess);
    }


    /**
     * 获取OnError回调函数，该回调函数在请求失败时被调用
     *
     * @return {@link OnError}接口实例
     */
    public OnError getOnError() {
        return onError;
    }

    /**
     * 设置OnError回调函数，该回调函数在请求失败时被调用
     *
     * @param onError {@link OnError}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setOnError(OnError onError) {
        this.onError = onError;
        return this;
    }

    /**
     * 设置OnError回调函数，该回调函数在请求失败时被调用
     * <p>同 {@link ForestRequest#setOnError(OnError)}
     *
     * @param onError {@link OnError}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setOnError(OnError)
     */
    public ForestRequest<T> onError(OnError onError) {
        return setOnError(onError);
    }

    /**
     * 获取OnCanceled回调函数，该回调函数在请求取消后被调用
     *
     * @return {@link OnCanceled}接口实例
     * @since 1.5.27
     */
    public OnCanceled getOnCanceled() {
        return onCanceled;
    }

    /**
     * 设置OnCanceled回调函数，该回调函数在请求取消后被调用
     *
     * @param onCanceled {@link OnCanceled}接口实例
     * @return {@link ForestRequest}类实例
     * @since 1.5.27
     */
    public ForestRequest<T> setOnCanceled(OnCanceled onCanceled) {
        this.onCanceled = onCanceled;
        return this;
    }

    /**
     * 设置OnCanceled回调函数，该回调函数在请求取消后被调用
     *
     * @param onCanceled {@link OnCanceled}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setOnCanceled(OnCanceled)
     * @since 1.5.27
     */
    public ForestRequest<T> onCanceled(OnCanceled onCanceled) {
        return setOnCanceled(onCanceled);
    }


    /**
     * 获取SuccessWhen回调函数，该回调函数用于判断请求是否成功
     * <p>如果成功, 执行 onSuccess
     * <p>如果失败, 执行 onError
     *
     * @return {@link SuccessWhen}接口实例
     */
    public SuccessWhen getSuccessWhen() {
        return successWhen;
    }

    /**
     * 设置SuccessWhen回调函数，该回调函数用于判断请求是否成功
     * <p>如果成功, 执行 onSuccess
     * <p>如果失败, 执行 onError
     *
     * @param successWhen {@link SuccessWhen}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setSuccessWhen(SuccessWhen successWhen) {
        this.successWhen = successWhen;
        return this;
    }

    /**
     * 设置SuccessWhen回调函数，该回调函数用于判断请求是否成功
     * <p>如果成功, 执行 onSuccess
     * <p>如果失败, 执行 onError
     * <p>输入参数为 {@link SuccessWhen} 实现类的 {@link Class} 对象
     * <p>方法会自动将其实例化，并设置为请求成功条件回调函数
     *
     * @param conditionClass  {@link SuccessWhen} 实现类的 {@link Class} 对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setSuccessWhen(Class<? extends SuccessWhen> conditionClass) {
        if (conditionClass != null && !conditionClass.isInterface()) {
            SuccessWhen condition = configuration.getForestObject(conditionClass);
            setSuccessWhen(condition);
        }
        return this;
    }

    /**
     * 设置SuccessWhen回调函数，该回调函数用于判断请求是否成功
     * <p>如果成功, 执行 onSuccess
     * <p>如果失败, 执行 onError
     * <p>输入参数为 {@link SuccessWhen} 实现类的 {@link Class} 对象
     * <p>方法会自动将其实例化，并设置为请求成功条件回调函数
     * <p>同 {@link ForestRequest#setSuccessWhen(SuccessWhen)}
     *
     * @param successWhen {@link SuccessWhen}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setSuccessWhen(SuccessWhen)
     */
    public ForestRequest<T> successWhen(SuccessWhen successWhen) {
        return setSuccessWhen(successWhen);
    }

    /**
     * 设置SuccessWhen回调函数，该回调函数用于判断请求是否成功
     * <p>如果成功, 执行 onSuccess
     * <p>如果失败, 执行 onError
     * <p>同 {@link ForestRequest#setSuccessWhen(SuccessWhen)}
     *
     * @param conditionClass  {@link SuccessWhen} 实现类的 {@link Class} 对象
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setSuccessWhen(SuccessWhen)
     */
    public ForestRequest<T> successWhen(Class<? extends SuccessWhen> conditionClass) {
        return setSuccessWhen(conditionClass);
    }



    /**
     * 获取OnRetry回调函数，该回调函数在请求重试时被调用
     *
     * @return {@link OnRetry}接口实例
     */
    public OnRetry getOnRetry() {
        return onRetry;
    }

    /**
     * 设置OnRetry回调函数，该回调函数在请求重试时被调用
     *
     * @param onRetry {@link OnRetry}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setOnRetry(OnRetry onRetry) {
        this.onRetry = onRetry;
        return this;
    }

    /**
     * 设置OnRetry回调函数，该回调函数在请求重试时被调用
     * <p>同 {@link ForestRequest#setOnRetry(OnRetry)}
     *
     * @param onRetry {@link OnRetry}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setOnRetry(OnRetry)
     */
    public ForestRequest<T> onRetry(OnRetry onRetry) {
        return setOnRetry(onRetry);
    }


    /**
     * 获取RetryWhen回调函数, 回调函数为请求重试的触发条件
     *
     * @return {@link RetryWhen}接口实例
     */
    public RetryWhen getRetryWhen() {
        return retryWhen;
    }

    /**
     * 设置RetryWhen回调函数, 回调函数为请求重试的触发条件
     *
     * @param retryWhen {@link RetryWhen}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setRetryWhen(RetryWhen retryWhen) {
        this.retryWhen = retryWhen;
        return this;
    }

    /**
     * 设置RetryWhen回调函数, 回调函数为请求重试的触发条件
     * <p>同 {@link ForestRequest#setRetryWhen(RetryWhen)}
     *
     * @param retryWhen {@link RetryWhen}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setRetryWhen(RetryWhen)
     */
    public ForestRequest<T> retryWhen(RetryWhen retryWhen) {
        return setRetryWhen(retryWhen);
    }


    /**
     * 设置RetryWhen回调函数, 回调函数为请求重试的触发条件
     * <p>输入参数为 {@link RetryWhen} 实现类的 {@link Class} 对象
     * <p>方法会自动将其实例化，并设置为请求触发条件
     *
     * @param conditionClass  {@link RetryWhen} 实现类的 {@link Class} 对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setRetryWhen(Class<? extends RetryWhen> conditionClass) {
        if (conditionClass != null && !conditionClass.isInterface()) {
            RetryWhen condition = configuration.getForestObject(conditionClass);
            setRetryWhen(condition);
        }
        return this;
    }

    /**
     * 设置RetryWhen回调函数, 回调函数为请求重试的触发条件
     * <p>输入参数为 {@link RetryWhen} 实现类的 {@link Class} 对象
     * <p>方法会自动将其实例化，并设置为请求触发条件
     * <p>同 {@link ForestRequest#setRetryWhen(Class)}
     *
     * @param conditionClass  {@link RetryWhen} 实现类的 {@link Class} 对象
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setRetryWhen(Class)
     */
    public ForestRequest<T> retryWhen(Class<? extends RetryWhen> conditionClass) {
        return setRetryWhen(conditionClass);
    }

    /**
     * 该请求是否下载文件
     *
     * @return {@code true}: 下载文件，{@code false}: 不下载文件
     */
    public boolean isDownloadFile() {
        return isDownloadFile;
    }

    /**
     * 设置该请求是否下载文件
     *
     * @param downloadFile {@code true}: 下载文件，{@code false}: 不下载文件
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setDownloadFile(boolean downloadFile) {
        isDownloadFile = downloadFile;
        return this;
    }


    /**
     * 设置该请求是否下载文件
     *
     * @param dir      文件下载目录
     * @param filename 文件名
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setDownloadFile(String dir, String filename) {
        if (StringUtils.isNotBlank(dir)) {
            this.addInterceptor(DownloadLifeCycle.class);
            // 当map达到当前最大容量的0.75时会扩容,大小设置为4且塞入两个键值对不会进行扩容
            Map<String, Object> downloadFileMap = new HashMap<>(4);
            downloadFileMap.put("dir", dir);
            downloadFileMap.put("filename", filename != null ? filename : "");
            InterceptorAttributes attributes = new InterceptorAttributes(DownloadLifeCycle.class, downloadFileMap);
            attributes.render(new Object[0]);
            this.addInterceptorAttributes(DownloadLifeCycle.class, attributes);
        }
        return this;
    }


    /**
     * 获取上传/下载进度监听的步长
     * <p>
     * 每上传/下载一定的比特数，执行一次监听回调函数
     *
     * @return 进度监听的步长，{@code long}类型数值
     */
    public long getProgressStep() {
        return progressStep;
    }

    /**
     * 设置获取上传/下载进度监听的步长
     * <p>
     * 每上传/下载一定的比特数，执行一次监听回调函数
     *
     * @param progressStep 进度监听的步长，{@code long}类型数值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setProgressStep(long progressStep) {
        this.progressStep = progressStep;
        return this;
    }

    /**
     * 获取重定向回调函数: 在请求重定向时触发
     *
     * @return 重定向回调函数， {@link OnRedirection}接口实例
     */
    public OnRedirection getOnRedirection() {
        return onRedirection;
    }

    /**
     * 设置重定向回调函数: 在请求重定向时触发
     *
     * @param onRedirection 重定向回调函数， {@link OnRedirection}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setOnRedirection(OnRedirection onRedirection) {
        this.onRedirection = onRedirection;
        return this;
    }

    /**
     * 设置重定向回调函数: 在请求重定向时触发
     * <p>同 {@link ForestRequest#setOnRedirection(OnRedirection)}方法
     *
     * @param onRedirection 重定向回调函数， {@link OnRedirection}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setOnRedirection(OnRedirection)
     */
    public ForestRequest<T> onRedirection(OnRedirection onRedirection) {
        this.onRedirection = onRedirection;
        return this;
    }


    /**
     * 获取进度回调函数：上传/下载进度监听时调用
     * <p>
     * 每上传/下载传输 ${progressStep} 个比特数时，执行一次监听回调函数
     *
     * @return 进度回调函数，{@link OnProgress}接口实例
     */
    public OnProgress getOnProgress() {
        return onProgress;
    }

    /**
     * 设置进度回调函数：上传/下载进度监听时调用
     * <p>每上传/下载传输 ${progressStep} 个比特数时，执行一次监听回调函数
     *
     * @param onProgress 进度回调函数，{@link OnProgress}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setOnProgress(OnProgress onProgress) {
        this.onProgress = onProgress;
        return this;
    }

    /**
     * 设置进度回调函数：上传/下载进度监听时调用
     * <p>每上传/下载传输 ${progressStep} 个比特数时，执行一次监听回调函数
     * <p>同 {@link ForestRequest#setOnProgress(OnProgress)}
     *
     * @param onProgress 进度回调函数，{@link OnProgress}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setOnProgress(OnProgress)
     */
    public ForestRequest<T> onProgress(OnProgress onProgress) {
        return setOnProgress(onProgress);
    }


    /**
     * 获取回调函数: 加载Cookie时调用
     *
     * @return {@link OnLoadCookie}接口实例
     */
    public OnLoadCookie getOnLoadCookie() {
        return onLoadCookie;
    }

    /**
     * 设置回调函数: 加载Cookie时调用
     *
     * @param onLoadCookie {@link OnLoadCookie}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setOnLoadCookie(OnLoadCookie onLoadCookie) {
        this.onLoadCookie = onLoadCookie;
        return this;
    }

    /**
     * 设置回调函数: 加载Cookie时调用
     * <p>同 {@link ForestRequest#setOnLoadCookie(OnLoadCookie)}
     *
     * @param onLoadCookie {@link OnLoadCookie}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setOnLoadCookie(OnLoadCookie)
     */
    public ForestRequest<T> onLoadCookie(OnLoadCookie onLoadCookie) {
        return setOnLoadCookie(onLoadCookie);
    }


    /**
     * 获取回调函数: 需要保存Cookie时调用
     *
     * @return {@link OnSaveCookie}接口实例
     */
    public OnSaveCookie getOnSaveCookie() {
        return onSaveCookie;
    }

    /**
     * 设置回调函数: 需要保存Cookie时调用
     *
     * @param onSaveCookie {@link OnSaveCookie}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setOnSaveCookie(OnSaveCookie onSaveCookie) {
        this.onSaveCookie = onSaveCookie;
        return this;
    }

    /**
     * 设置回调函数: 需要保存Cookie时调用
     * <p>同 {@link ForestRequest#setOnSaveCookie(OnSaveCookie)}
     *
     * @param onSaveCookie {@link OnSaveCookie}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setOnSaveCookie(OnSaveCookie)
     */
    public ForestRequest<T> onSaveCookie(OnSaveCookie onSaveCookie) {
        return setOnSaveCookie(onSaveCookie);
    }

    /**
     * 获取请求所绑定到的变量值
     *
     * @param name 变量名
     * @return 变量值
     */
    public Object getVariableValue(String name) {
        MappingVariable variable = method.getVariable(name);
        if (variable != null) {
            return getArgument(variable.getIndex());
        }
        if (!method.isVariableDefined(name)) {
            throw new ForestVariableUndefinedException(name);
        }
        return method.getVariableValue(name, method);
    }

    /**
     * 获取请求所绑定到的变量值
     *
     * @param name 变量名
     * @param clazz 返回类型
     * @return 变量名
     * @param <R> 返回类型
     */
    public <R> R getVariableValue(String name, Class<R> clazz) {
        final Object result = getVariableValue(name);
        if (result == null) {
            return null;
        }
        return clazz.cast(result);
    }

    /**
     * 获取请求所绑定到的变量值
     * <p>同 {@link ForestRequest#getVariableValue(String)}
     *
     * @param name 变量名
     * @return 变量值
     * @see ForestRequest#getVariableValue(String)
     */
    public Object variableValue(String name) {
        return getVariableValue(name);
    }


    /**
     * 添加拦截器到该请求中
     * <p>拦截器在请求的初始化、发送请求前、发送成功、发送失败等生命周期中都会被调用
     *
     * @param interceptorClass 拦截器类，{@link Interceptor}接口类
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addInterceptor(Class<? extends Interceptor> interceptorClass) {
        if (!Interceptor.class.isAssignableFrom(interceptorClass) || interceptorClass.isInterface()) {
            throw new ForestRuntimeException("Class [" + interceptorClass.getName() + "] is not a implement of [" +
                    Interceptor.class.getName() + "] interface.");
        }
        Interceptor interceptor = configuration.getInterceptorFactory().getInterceptor(interceptorClass);
        interceptorChain.addInterceptor(interceptor);
        return this;
    }


    /**
     * 添加拦截器到该请求中
     * <p>拦截器在请求的初始化、发送请求前、发送成功、发送失败等生命周期中都会被调用
     *
     * @param interceptor 拦截器，{@link Interceptor}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addInterceptor(Interceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
        return this;
    }

    /**
     * 获取拦截器链对象
     *
     * @return 拦截器链对象，{@link InterceptorChain}类实例
     */
    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    /**
     * 添加拦截器属性到该请求
     * <p>被添加的属性会被对应的请求所绑定，同时也会绑定到拦截器类，并且按不同的拦截器进行隔离。
     * <p>而且这些属性值不能通过网络请求传递到远端服务器。
     * <p>拦截器属性有两个特性：
     * <ul>
     *     <li>1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。</li>
     *     <li>2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。
     *     <p>也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。</li>
     * </ul>
     * @param interceptorClass 要绑定的拦截器类
     * @param attributes 拦截器属性，{@link InterceptorAttributes}类实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addInterceptorAttributes(Class interceptorClass, InterceptorAttributes attributes) {
        InterceptorAttributes oldAttributes = interceptorAttributes.get(interceptorClass);
        if (oldAttributes != null) {
            for (Map.Entry<String, Object> entry : attributes.getAttributeTemplates().entrySet()) {
                oldAttributes.addAttribute(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String, Object> entry : attributes.getAttributes().entrySet()) {
                oldAttributes.addAttribute(entry.getKey(), entry.getValue());
            }
        } else {
            interceptorAttributes.put(interceptorClass, attributes);
        }
        return this;
    }

    /**
     * 添加拦截器属性到该请求
     * <p>被添加的属性会被对应的请求所绑定，同时也会绑定到拦截器类，并且按不同的拦截器进行隔离。
     * <p>而且这些属性值不能通过网络请求传递到远端服务器。
     * <p>拦截器属性有两个特性：
     * <ul>
     *     <li>1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。</li>
     *     <li>2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。
     *     <p>也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。</li>
     * </ul>
     *
     * @param interceptorClass 要绑定的拦截器类
     * @param attributeName 拦截器属性名
     * @param attributeValue 拦截器属性值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addInterceptorAttribute(Class interceptorClass, String attributeName, Object attributeValue) {
        InterceptorAttributes attributes = getInterceptorAttributes(interceptorClass);
        if (attributes == null) {
            attributes = new InterceptorAttributes(interceptorClass, new ConcurrentHashMap<>());
            addInterceptorAttributes(interceptorClass, attributes);
        }
        attributes.addAttribute(attributeName, attributeValue);
        return this;
    }

    /**
     * 获取拦截器属性表
     * <p>拦截器属性有两个特性：
     * <ul>
     *     <li>1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。</li>
     *     <li>2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。
     *     <p>也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。</li>
     * </ul>
     *
     * @return {@link Map}映射，Key: 拦截器类，Value: 拦截器属性集合对象，{@link InterceptorAttributes}类实例
     */
    public Map<Class, InterceptorAttributes> getInterceptorAttributes() {
        return interceptorAttributes;
    }

    /**
     * 根据拦截器类获取拦截器属性集合对象
     * <p>拦截器属性有两个特性：
     * <ul>
     *     <li>1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。</li>
     *     <li>2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。
     *     <p>也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。</li>
     * </ul>
     *
     * @param interceptorClass 拦截器类
     * @return 拦截器属性集合对象，{@link InterceptorAttributes}类实例
     */
    public InterceptorAttributes getInterceptorAttributes(Class interceptorClass) {
        return interceptorAttributes.get(interceptorClass);
    }

    /**
     * 根据拦截器类和拦截器属性名获取拦截器属性值
     * <p>拦截器属性有两个特性：
     * <ul>
     *     <li>1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。</li>
     *     <li>2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。
     *     <p>也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。</li>
     * </ul>
     *
     * @param interceptorClass 拦截器类
     * @param attributeName 拦截器属性名
     * @return 拦截器属性值
     */
    public Object getInterceptorAttribute(Class interceptorClass, String attributeName) {
        InterceptorAttributes attributes = interceptorAttributes.get(interceptorClass);
        if (attributes == null) {
            return null;
        }
        return attributes.getAttribute(attributeName);
    }

    /**
     * 获取Forest请求重试器
     *
     * @return Forest请求重试器，{@link ForestRetryer}接口实例
     */
    public ForestRetryer getRetryer() {
        return retryer;
    }

    /**
     * 设置Forest请求重试器
     *
     * @param retryer Forest请求重试器，{@link ForestRetryer}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setRetryer(ForestRetryer retryer) {
        this.retryer = retryer;
        return this;
    }

    /**
     * 设置Forest请求重试器
     * <p>同 {@link ForestRequest#setRetryer(ForestRetryer)}
     *
     * @param retryer Forest请求重试器，{@link ForestRetryer}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setRetryer(ForestRetryer)
     */
    public ForestRequest<T> retryer(ForestRetryer retryer) {
        return setRetryer(retryer);
    }


    /**
     * 设置Forest请求重试器
     * <p>输入参数为 {@link ForestRetryer} 的子类 {@link Class} 对象
     * <p>方法会自动将其实例化并设置为请求的重试器
     *
     * @param retryerClass {@link ForestRetryer} 的子类 {@link Class} 对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setRetryer(Class<? extends ForestRetryer> retryerClass) {
        try {
            Constructor constructor = retryerClass.getConstructor(ForestRequest.class);
            ForestRetryer retryer = (ForestRetryer) constructor.newInstance(this);
            this.setRetryer(retryer);
        } catch (NoSuchMethodException e) {
            throw new ForestRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        } catch (InstantiationException e) {
            throw new ForestRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ForestRuntimeException(e);
        }
        return this;
    }

    /**
     * 设置Forest请求重试器
     * <p>输入参数为 {@link ForestRetryer} 的子类 {@link Class} 对象
     * <p>方法会自动将其实例化并设置为请求的重试器
     * <p>同 {@link ForestRequest#setRetryer(Class)}}
     *
     * @param retryerClass {@link ForestRetryer} 的子类 {@link Class} 对象
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setRetryer(Class)
     */
    public ForestRequest<T> retryer(Class<? extends ForestRetryer> retryerClass) {
        return setRetryer(retryerClass);
    }


    /**
     * 添加附件到请求中
     * <p>Attachment 是和请求绑定的附件属性值，这些值不能通过网络请求传递到远端服务器
     * <p>不同请求的附件相互独立，即使名称相同，也互不影响
     *
     * @param name 附件名
     * @param value 附件值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addAttachment(String name, Object value) {
        attachments.put(name, value);
        return this;
    }

    /**
     * 根据名称获取该请求中的附件
     * <p>Attachment 是和请求绑定的附件属性值，这些值不能通过网络请求传递到远端服务器。</p>
     * <p>不同请求的附件相互独立，即使名称相同，也互不影响。</p>
     *
     * @param name 附件名
     * @return 附件值
     */
    public Object getAttachment(String name) {
        Validations.assertParamNotNull(name, "name");
        return attachments.get(name);
    }


        /**
     * 根据名称获取该请求中的附件
     * <p>Attachment 是和请求绑定的附件属性值，这些值不能通过网络请求传递到远端服务器。</p>
     * <p>不同请求的附件相互独立，即使名称相同，也互不影响。</p>
     *
     * @param name 附件名
     * @return 附件值
     */
    public <R> R getAttachment(String name, Class<R> clazz) {
        Validations.assertParamNotNull(name, "name");
        Validations.assertParamNotNull(clazz, "clazz");
        final Object result = attachments.get(name);
        if (result == null) {
            return null;
        }
        return clazz.cast(result);
    }

    /**
     * 获取序列化器
     *
     * @return 序列化器，{@link ForestEncoder}接口实例
     */
    public ForestEncoder getEncoder() {
        return this.body.getEncoder();
    }

    /**
     * 设置序列化器
     *
     * @param encoder 序列化器，{@link ForestEncoder}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setEncoder(ForestEncoder encoder) {
        this.body.setEncoder(encoder);
        return this;
    }

    /**
     * 获取反序列化器
     * @return 反序列化器，{@link ForestConverter}接口实例
     */
    public ForestConverter getDecoder() {
        return decoder;
    }

    /**
     * 设置反序列化器
     *
     * @param decoder 反序列化器，{@link ForestConverter}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setDecoder(ForestConverter decoder) {
        this.decoder = decoder;
        return this;
    }

    /**
     * 设置反序列化器
     * <p>同 {@link ForestRequest#setDecoder(ForestConverter)}}
     *
     * @param decoder 反序列化器，{@link ForestConverter}接口实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setDecoder(ForestConverter)
     */
    public ForestRequest<T> decoder(ForestConverter decoder) {
        return setDecoder(decoder);
    }

    /**
     * 是否允许打印请求/响应日志
     *
     * @return {@code true}：允许，{@code false}：不允许
     */
    @Deprecated
    public boolean isLogEnable() {
        if (logConfiguration == null) {
            return true;
        }
        return logConfiguration.isLogEnabled();
    }

    /**
     * 获取请求日志配置信息
     *
     * @return 请求日志配置信息，{@link LogConfiguration}类实例
     */
    public LogConfiguration getLogConfiguration() {
        return logConfiguration;
    }

    public ForestRequest<T> setLogConfiguration(LogConfiguration logConfiguration) {
        this.logConfiguration = logConfiguration;
        return this;
    }


    /**
     * 获取SSL KeyStore信息
     * <p>在双向HTTPS请求中使用的验证信息</p>
     *
     * @return SSL KeyStore信息，{@link SSLKeyStore}类实例
     */
    public SSLKeyStore getKeyStore() {
        return keyStore;
    }

    /**
     * 设置SSL KeyStore信息
     * <p>在双向HTTPS请求中使用的验证信息
     *
     * @param keyStore SSL KeyStore信息，{@link SSLKeyStore}类实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setKeyStore(SSLKeyStore keyStore) {
        this.keyStore = keyStore;
        return this;
    }

    /**
     * 设置SSL KeyStore信息
     * <p>在双向HTTPS请求中使用的验证信息
     * <p>同 {@link ForestRequest#setKeyStore(SSLKeyStore)}}
     *
     * @param keyStore SSL KeyStore信息，{@link SSLKeyStore}类实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setKeyStore(SSLKeyStore)
     */
    public ForestRequest<T> keyStore(SSLKeyStore keyStore) {
        return setKeyStore(keyStore);
    }


    /**
     * 获取正向代理
     *
     * @return 正向代理，{@link ForestProxy}类实例
     */
    public ForestProxy getProxy() {
        return proxy;
    }

    /**
     * 设置正向代理
     *
     * @param proxy 正向代理，{@link ForestProxy}类实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setProxy(ForestProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * 通过代理 URL 字符串设置正向代理
     *
     * @param proxyUrl 代理 URL
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> proxy(String proxyUrl) {
        this.proxy = ForestProxy.url(proxyUrl);
        return this;
    }

    /**
     * 设置正向代理
     * <p>同 {@link ForestRequest#setProxy(ForestProxy)}}
     *
     * @param proxy 正向代理，{@link ForestProxy}类实例
     * @return {@link ForestRequest}类实例
     * @see ForestRequest#setProxy(ForestProxy)
     */
    public ForestRequest<T> proxy(ForestProxy proxy) {
        return setProxy(proxy);
    }


    /**
     * 设置该请求对应的方法返回值
     *
     * @param result 方法返回值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> methodReturn(T result) {
        if (this.lifeCycleHandler != null) {
            this.lifeCycleHandler.handleResult(result);
        }
        return this;
    }

    /**
     * 获取该请求对应的方法返回值
     *
     * @return 方法返回值
     */
    public Object getMethodReturnValue() {
        if (this.lifeCycleHandler != null) {
            if (this.lifeCycleHandler instanceof MethodLifeCycleHandler) {
                Object resultData = ((MethodLifeCycleHandler) lifeCycleHandler).getResultData();
                return resultData;
            }
        }
        return null;
    }

    /**
     * 获取该请求所返回的响应对象
     *
     * @return Forest响应对象, {@link ForestResponse}实例
     */
    private ForestResponse<T> getResponse() {
        if (this.lifeCycleHandler != null) {
            if (this.lifeCycleHandler instanceof MethodLifeCycleHandler) {
                return ((MethodLifeCycleHandler) lifeCycleHandler).getResponse();
            }
        }
        return null;

    }

    /**
     * 执行 {@code retryWhen} 回调函数，判断是否触发请求重试条件
     * <p>判断过程如下：
     * <ul>
     *     <li>1. 如果有设置请求上的 {@code retryWhen} 属性，则执行该属性的回调函数并返回判断结果</li>
     *     <li>2. 如果有设置全局的 {@code retryWhen} 回调函数，则执行全局的回调函数并返回判断结果</li>
     * </ul>
     *
     * @param response Forest响应对象
     * @return
     */
    private boolean doRetryWhen(ForestResponse<?> response) {
        if (this.retryWhen != null) {
             return this.retryWhen.retryWhen(this, response);
        }
        RetryWhen globalRetryWhen = configuration.getRetryWhen();
        if (globalRetryWhen != null) {
            return globalRetryWhen.retryWhen(this, response);
        }
        return response.isError();
    }

    /**
     * 是否能够重试
     *
     * @param response Forest响应对象
     * @return 返回 {@code null} 时，不进行重试; 返回 {@link ForestRetryException} 时，进行重试
     * @see ForestRequest#canRetry(ForestResponse, ForestRetryException)
     */
    public ForestRetryException canRetry(ForestResponse<?> response) {
        try {
            return canRetry(response, null);
        } catch (ForestRetryException rex) {
            return rex;
        } catch (Throwable ex) {
            if (ex instanceof ForestRuntimeException) {
                throw (ForestRuntimeException) ex;
            } else {
                throw new ForestRuntimeException(ex);
            }
        }
    }

    /**
     * 是否能重试
     * <p>根据以下条件判断请求是否能够重试:
     * <ul>
     *     <li>1. {@code retryEnabled} 属性是否为 {@code true}</li>
     *     <li>2. {@code retryWhen} 回调函数结果是否为 {@code true}</li>
     *     <li>3. {@code Retryer} 的 {@code canRetry} 方法结果是否为 {@code true}</li>
     * </ul>
     * 当以上结果全部为 {@code true} 时，便能够重试，并返回Forest重试异常 {@link ForestRetryException} 对象.
     * <p>如有一个条不满足，则不能重试，并抛出 {@link ForestRetryException} 异常
     *
     * @param response Forest响应对象
     * @param ex 当重试条件不能满足时，抛出 Forest重试异常对象
     * @return 需要重试时，会返回 {@link ForestRetryException} 异常对象
     * @throws Throwable 当重试条件不满足时所抛出的异常类型
     */
    public final ForestRetryException canRetry(ForestResponse<?> response, ForestRetryException ex) throws Throwable {
        if (ex == null) {
            ex = new ForestRetryException(this, maxRetryCount, getCurrentRetryCount());
        }
        if (response == null || !retryEnabled) {
            throw ex.getCause();
        }
        HttpExecutor retryExecutor = backend.createExecutor(this, lifeCycleHandler);
        if (retryExecutor != null) {
            if (!doRetryWhen(response)) {
                throw ex.getCause();
            }
            try {
                retryer.canRetry(ex);
            } catch (Throwable e) {
                throw e;
            }
            interceptorChain.onRetry(this, response);
            if (onRetry != null) {
                onRetry.onRetry(this, response);
            }
            return ex;
        }
        return null;
    }

    /**
     * 获取请求池
     *
     * @return Forest请求池
     */
    public ForestRequestPool pool() {
        return this.configuration.getPool();
    }

    /**
     * 克隆Forest请求对象
     *
     * @return 新的Forest请求对象
     */
    @Override
    public ForestRequest<T> clone() {
        ForestRequest<T> newRequest = new ForestRequest<>(
                this.configuration,
                this.method,
                this.arguments);
        ForestBody newBody = newRequest.body();
        newBody.setBodyType(body.getBodyType());
        for (ForestRequestBody bodyItem : this.body) {
            newBody.add(bodyItem.clone());
        }
        newRequest.backend = this.backend;
        newRequest.lifeCycleHandler = this.lifeCycleHandler;
        newRequest.protocol = this.protocol;
        newRequest.sslProtocol = this.sslProtocol;
        newRequest.url = this.url;
        newRequest.query = this.query.clone(newRequest);
        newRequest.headers = this.headers.clone(newRequest);
        newRequest.timeout = this.timeout;
        newRequest.filename = this.filename;
        newRequest.charset = this.charset;
        newRequest.decoder = this.decoder;
        newRequest.autoRedirection = this.autoRedirection;
        newRequest.authenticator = this.authenticator;
        newRequest.decompressResponseGzipEnabled = this.decompressResponseGzipEnabled;
        newRequest.responseEncode = this.responseEncode;
        newRequest.isDownloadFile = this.isDownloadFile;
        newRequest.proxy = this.proxy;
        newRequest.keyStore = this.keyStore;
        newRequest.async = this.async;
        newRequest.retryer = this.retryer;
        newRequest.maxRetryCount = this.maxRetryCount;
        newRequest.maxRetryInterval = this.maxRetryInterval;
        newRequest.onSuccess = this.onSuccess;
        newRequest.successWhen = this.successWhen;
        newRequest.onError = this.onError;
        newRequest.onRedirection = this.onRedirection;
        newRequest.onLoadCookie = this.onLoadCookie;
        newRequest.onSaveCookie = this.onSaveCookie;
        newRequest.onProgress = this.onProgress;
        newRequest.progressStep = this.progressStep;
        newRequest.onRetry = this.onRetry;
        newRequest.onCanceled = this.onCanceled;
        newRequest.retryWhen = this.retryWhen;
        newRequest.retryEnabled = this.retryEnabled;
        newRequest.type = this.type;
        newRequest.dataType = this.dataType;
        newRequest.interceptorChain = this.interceptorChain;
        newRequest.interceptorAttributes = this.interceptorAttributes;
        newRequest.attachments = this.attachments;
        newRequest.logConfiguration = this.logConfiguration;
        newRequest.requestLogMessage = this.requestLogMessage;
        return newRequest;
    }

    /**
     * 执行请求发送过程
     *
     * @param backend HTTP后端，{@link HttpBackend}接口实例
     * @param lifeCycleHandler 生命周期处理器，{@link LifeCycleHandler}接口实例
     * @return 接受到请求响应后，其响应内容反序列化成对象的结果
     */
    public Object execute(HttpBackend backend, LifeCycleHandler lifeCycleHandler) {
        setLifeCycleHandler(lifeCycleHandler);
        processRedirectionRequest();
        // 执行 beforeExecute
        if (interceptorChain.beforeExecute(this)) {
            // 认证信息增强
            if (this.authenticator != null) {
                for (ForestAuthenticator auth = this.authenticator; auth != null; auth = auth.next()) {
                    auth.enhanceAuthorization(this);
                }
            }
            final String userInfo = getUserInfo();
            if (StringUtils.isNotEmpty(userInfo) && getHeader("Authorization") == null) {
                new BasicAuth(userInfo).enhanceAuthorization(this);
            }

            this.url.mergeAddress().checkAndComplete();
            ForestCookies cookies = new ForestCookies();
            lifeCycleHandler.handleLoadCookie(this, cookies);
            this.addCookies(cookies);
            // 从后端HTTP框架创建HTTP请求执行器
            executor = backend.createExecutor(this, lifeCycleHandler);
            if (executor != null) {
                try {
                    // 执行请求，即发生请求到服务端
                    executor.execute(lifeCycleHandler);
                } catch (ForestRuntimeException e) {
                    if (e instanceof ForestAsyncAbortException) {
                        ((ForestAsyncAbortException) e).setRequest(this);
                        ForestResponseFactory forestResponseFactory = executor.getResponseFactory();
                        ForestResponse response = forestResponseFactory.createResponse(this, null, lifeCycleHandler, e, new Date());
                        executor.getResponseHandler().handleError(response, e);
                    } else {
                        throw e;
                    }
                }
            }
        }
        // 返回结果
        return getMethodReturnValue();
    }

    /**
     * 取消请求执行
     *
     * @since 1.5.27
     */
    public void cancel() {
        if (executor != null) {
            executor.close();
            canceled = true;
        }
    }


    /**
     * 处理重定向请求
     * <p>如果此请求为将要进行重定向的请求，会去执行 onRedirection 回调函数
     */
    private void processRedirectionRequest() {
        if (isRedirection()) {
            if (onRedirection != null) {
                // 执行 onRedirection 对应的回调函数
                onRedirection.onRedirection(this, prevRequest, prevResponse);
            }
            // 执行拦截器中的额 onRedirection 方法
            interceptorChain.onRedirection(this, prevRequest, prevResponse);
        }
    }

    /**
     * 执行请求发送过程
     *
     * @return 接受到请求响应后，其响应内容反序列化成对象的结果
     */
    public Object execute() {
        return execute(getBackend(), getLifeCycleHandler());
    }

    /**
     * 执行请求发送过程
     *
     * @param clazz 结果返回类型, {@link Class} 对象
     * @param <R> 泛型参数: 结果返回类型
     * @return 请求执行响应后返回的结果, 其为 {@code Class<R>} 参数所指定的类型
     */
    public <R> R execute(Class<R> clazz) {
        LifeCycleHandler lifeCycleHandler = getLifeCycleHandler();
        MethodLifeCycleHandler<R> methodLifeCycleHandler = new MethodLifeCycleHandler<>(
                clazz,
                lifeCycleHandler.getOnSuccessClassGenericType());
        Object ret = execute(getBackend(), methodLifeCycleHandler);
        return (R) ret;
    }

    /**
     * 执行请求发送过程，并获取字节数组类型结果
     *
     * @return 请求执行响应后返回的结果, 其为字节数组类型
     */
    public byte[] executeAsByteArray() {
        return execute(byte[].class);
    }


    /**
     * 执行请求发送过程，并获取字节Boolean类型结果
     *
     * @return 请求执行响应后返回的结果, 其为Boolean类型
     */
    public Boolean executeAsBoolean() {
        return execute(Boolean.class);
    }


    /**
     * 执行请求发送过程，并获取整数类型结果
     *
     * @return 请求执行响应后返回的结果, 其为整数类型
     */
    public Integer executeAsInteger() {
        return execute(Integer.class);
    }

    /**
     * 执行请求发送过程，并获取Long类型结果
     *
     * @return 请求执行响应后返回的结果, 其为Long类型
     */
    public Long executeAsLong() {
        return execute(Long.class);
    }

    /**
     * 执行请求发送过程，并获取字符串类型结果
     *
     * @return 请求执行响应后返回的结果, 其为字符串类型
     */
    public String executeAsString() {
        return execute(String.class);
    }

    /**
     * 执行请求发送过程，并获取Map类型结果
     *
     * @param <KEY> 泛型参数: 结果表的键类型
     * @param <VALUE> 泛型参数: 结果表的值类型
     * @return 请求执行响应后返回的结果, 其为Map类型
     */
    public <KEY, VALUE> Map<KEY, VALUE> executeAsMap() {
        return execute(new TypeReference<Map<KEY, VALUE>>() {});
    }

    /**
     * 执行请求发送过程，并获取List类型结果
     *
     * @param <E> 泛型参数: 结果列表项类型
     * @return 请求执行响应后返回的结果, 其为List类型
     */
    public <E> List<E> executeAsList() {
        return execute(new TypeReference<List<E>>() {});
    }

    /**
     * 执行请求发送过程，并获取 Future 类型结果
     *
     * @return 请求执行响应后返回的结果, 其为 {@link Future} 对象实例
     * @since 1.5.27
     */
    public ForestFuture<T> executeAsFuture() {
        return execute(new TypeReference<ForestFuture<T>>() {});
    }


    /**
     * 执行请求发送过程，并获取响应类型结果
     *
     * @return 请求执行响应后返回的结果, 其为 {@link ForestResponse} 对象实例
     * @since 1.5.27
     */
    public ForestResponse executeAsResponse() {
        return execute(ForestResponse.class);
    }


    /**
     * 执行请求发送过程
     *
     * @param type 结果返回类型, {@link Type} 接口实例
     * @param <R> 泛型参数: 结果返回类型
     * @return 请求执行响应后返回的结果, 其为 {@link Type} 参数所指定的类型
     */
    public <R> R execute(Type type) {
        LifeCycleHandler lifeCycleHandler = getLifeCycleHandler();
        MethodLifeCycleHandler<R> methodLifeCycleHandler = new MethodLifeCycleHandler<>(
                type,
                lifeCycleHandler.getOnSuccessClassGenericType());
        Object ret = execute(getBackend(), methodLifeCycleHandler);
        return (R) ret;
    }

    /**
     * 执行请求发送过程
     *
     * @param typeReference 结果返回类型的引用, {@link TypeReference} 对象
     * @param <R> 泛型参数: 结果返回类型
     * @return 请求执行响应后返回的结果, 其为 {@link TypeReference} 的泛型参数所指定的类型
     */
    public <R> R execute(TypeReference<R> typeReference) {
        return execute(typeReference.getType());
    }

}
