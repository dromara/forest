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

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.callback.OnLoadCookie;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnRetry;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.http.body.ByteArrayRequestBody;
import com.dtflys.forest.http.body.FileRequestBody;
import com.dtflys.forest.http.body.InputStreamRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;
import com.dtflys.forest.http.body.StringRequestBody;
import com.dtflys.forest.interceptor.InterceptorAttributes;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.multipart.ByteArrayMultipart;
import com.dtflys.forest.multipart.FileMultipart;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.multipart.InputStreamMultipart;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.retryer.ForestRetryer;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.HttpExecutor;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.InterceptorChain;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.TypeReference;
import com.dtflys.forest.utils.URLUtils;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static com.dtflys.forest.mapping.MappingParameter.*;

/**
 * Forest请求对象
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-24
 */
public class ForestRequest<T> {

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
     * 生命周期处理器
     */
    private volatile LifeCycleHandler lifeCycleHandler;

    /**
     * HTTP协议
     * <p>默认为 HTTP 1.1
     */
    private ForestProtocol protocol = ForestProtocol.HTTP_1_1;

    /**
     * URL路径
     */
    private ForestURL url;

    /**
     * URL中的Query参数表
     */
    private ForestQueryMap query = new ForestQueryMap();

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
     * 请求字符集
     */
    private String charset;

    /**
     * 请求响应返回数据的字符编码
     */
    private String responseEncode = "UTF-8";

    /**
     * 是否异步
     */
    private boolean async;

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
    private List<ForestRequestBody> bodyItems = new LinkedList<>();


    /**
     * 请求体集合
     * <p>所有本请求对象中的请求头都在由该请求体集合对象管理
     */
    private ForestHeaderMap headers = new ForestHeaderMap();


    /**
     * 文件上传项列表
     */
    private List<ForestMultipart> multiparts = new LinkedList<>();

    /**
     * 文件名
     * <p>该字段为文件下载时，保存到本地磁盘时用到的文件名
     */
    private String filename;

    /**
     * 参数表
     * <p>接口方法调用时传入的参数表
     */
    private final Object[] arguments;

    /**
     * 回调函数：请求成功时调用
     */
    private OnSuccess onSuccess;

    /**
     * 回调函数：请求失败时调用
     */
    private OnError onError;

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
    private Map<Class, InterceptorAttributes> interceptorAttributes = new HashMap<>();

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
    private Map<String, Object> attachments = new HashMap<>();

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
     * 正向代理
     */
    private ForestProxy proxy;

    public ForestRequest(ForestConfiguration configuration, ForestMethod method, Object[] arguments) {
        this.configuration = configuration;
        this.method = method;
        this.arguments = arguments;
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
     * 获取请求URL
     * <p>不同于 {@link ForestRequest#getUrl()} 方法,
     * <p>此方法返回的URL为 {@link ForestURL} 对象实例
     *
     * @return {@link ForestURL} 对象实例
     */
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
        return url.toString();
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
     * 设置请求的url地址
     * <p>每次设置请求的url地址时，都会解析传入的url参数字符串
     * <p>然后从url中解析出Query参数，并将其替换调用原来的Query参数，或新增成新的Query参数
     *
     * @param url url地址字符串
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setUrl(String url) {
        if (StringUtils.isBlank(url)) {
            throw new ForestRuntimeException("[Forest] Request url cannot be empty!");
        }
        String srcUrl = StringUtils.trimBegin(url);
        String query = "";

        if (!this.query.isEmpty()) {
            this.query.clearQueriesFromUrl();
        }

        try {
            URL u = new URL(srcUrl);
            this.url = new ForestURL(u);
            query = u.getQuery();
            if (StringUtils.isNotEmpty(query)) {
                String[] params = query.split("&");
                for (int i = 0; i < params.length; i++) {
                    String p = params[i];
                    String[] nameValue = p.split("=", 2);
                    String name = nameValue[0];
                    RequestNameValue requestNameValue = new RequestNameValue(name, TARGET_QUERY);
                    if (nameValue.length > 1) {
                        StringBuilder valueBuilder = new StringBuilder();
                        valueBuilder.append(nameValue[1]);
                        if (nameValue.length > 2) {
                            for (int j = 2; j < nameValue.length; j++) {
                                valueBuilder.append("=");
                                valueBuilder.append(nameValue[j]);
                            }
                        }
                        String value = valueBuilder.toString();
                        requestNameValue.setValue(value);
                    }

                    addQuery(new ForestQueryParameter(
                            requestNameValue.getName(), requestNameValue.getValue(), true, false, null));
                }
            }

        } catch (MalformedURLException e) {
            throw new ForestRuntimeException(e);
        }
        return this;
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
     * @param hostAddress Forest主机地址信息
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setHostAddress(ForestHostAddress hostAddress) {
        return setHost(hostAddress.getHost()).setPort(hostAddress.getPort());
    }

    /**
     * 设置请求的主机地址(主机名/ip地址 + 端口号)
     *
     * @param host 主机名/ip地址
     * @param port 端口号
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setHostAddress(String host, int port) {
        return setHost(host).setPort(port);
    }


    /**
     * 设置请求的主机地址(主机名/ip地址 + 端口号)
     * <p>同 {@link ForestRequest#setHostAddress(ForestHostAddress)} 方法
     *
     * @param hostAddress Forest主机地址信息
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setHostAddress(ForestHostAddress)
     */
    public ForestRequest<T> hostAddress(ForestHostAddress hostAddress) {
        return setHost(hostAddress.getHost()).setPort(hostAddress.getPort());
    }

    /**
     * 设置请求的主机地址(主机名/ip地址 + 端口号)
     * <p>同 {@link ForestRequest#setHostAddress(String, int)} 方法
     *
     * @param host 主机名/ip地址
     * @param port 端口号
     * @return {@link ForestRequest}对象实例
     * @see ForestRequest#setHostAddress(String, int)
     */
    public ForestRequest<T> hostAddress(String host, int port) {
        return setHostAddress(host, port);
    }


    /**
     * 设置URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
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
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @return URL路径
     */
    public String getPath() {
        return this.url.getPath();
    }

    /**
     * 获取Reference, 即URL井号(#)后面的字符串
     *
     * <p>列如：http://localhost:8080/xxx#yyyyy
     * <p>上例中 yyyy 便是 Reference
     *
     * @return URL井号(#)后面的字符串
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
     */
    public ForestRequest<T> setRef(String ref) {
        this.url.setRef(ref);
        return this;
    }

    /**
     * 设置Reference, 即URL井号(#)后面的字符串
     *
     * <p>列如：http://localhost:8080/xxx#yyyyy
     * <p>上例中 yyyy 便是 Reference
     * <p>同 {@link ForestRequest#setRef(String)} 方法
     *
     * @param ref URL井号(#)后面的字符串
     * @see ForestRequest#setRef(String)
     */
    public ForestRequest<T> ref(String ref) {
        this.url.setRef(ref);
        return this;
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
     * @return HTTP后端对象，{@link HttpBackend}接口实例
     */
    public HttpBackend getBackend() {
        return backend;
    }

    /**
     * 设置HTTP后端对象
     *
     * @param backend HTTP后端对象，{@link HttpBackend}接口实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> setBackend(HttpBackend backend) {
        this.backend = backend;
        return this;
    }

    /**
     * 设置HTTP后端对象
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
        StringBuilder builder = new StringBuilder();
        Iterator<ForestQueryParameter> iterator = query.queryValues().iterator();
        while (iterator.hasNext()) {
            ForestQueryParameter query = iterator.next();
            if (query != null) {
                String name = query.getName();
                Object value = query.getValue();
                if (name != null) {
                    builder.append(name);
                    if (value != null) {
                        builder.append("=");
                    }
                }
                if (value != null) {
                    try {
                        String encodedValue = URLUtils.encode(value.toString(), getCharset());
                        builder.append(encodedValue);
                    } catch (UnsupportedEncodingException e) {
                    }
                }
            }
            if (iterator.hasNext()) {
                builder.append("&");
            }
        }
        return builder.toString();
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
     * 添加请求中的Query参数
     *
     * @param queryParameter Query参数，{@link ForestQueryParameter}对象实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQuery(ForestQueryParameter queryParameter) {
        this.query.addQuery(queryParameter);
        return this;
    }

    /**
     * 批量添加请求中的Query参数
     *
     * @param queryParameters Query参数集合，{@link ForestQueryParameter}对象实例集合
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQuery(Collection<ForestQueryParameter> queryParameters) {
        for (ForestQueryParameter queryParameter : queryParameters) {
            addQuery(queryParameter);
        }
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
     * @param queryParameters Query参数数组，{@link ForestQueryParameter}对象实例数组
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> addQuery(ForestQueryParameter[] queryParameters) {
        for (ForestQueryParameter queryParameter : queryParameters) {
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
        ForestJsonConverter jsonConverter = getConfiguration().getJsonConverter();
        Map<String, Object> map = jsonConverter.convertObjectToMap(queryParameters);
        if (map != null && map.size() > 0) {
            map.forEach((key, value) -> addQuery(String.valueOf(key), value));
        }
        return this;
    }


    /**
     * 替换请求中的Query参数值
     *
     * @param queryParameter Query参数，{@link ForestQueryParameter}对象实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> replaceQuery(ForestQueryParameter queryParameter) {
        List<ForestQueryParameter> queryParameters = this.query.getQueries(queryParameter.getName());
        for (ForestQueryParameter parameter : queryParameters) {
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
        List<ForestQueryParameter> queryParameters = this.query.getQueries(name);
        for (ForestQueryParameter parameter : queryParameters) {
            parameter.setValue(value);
        }
        return this;
    }

    /**
     * 替换或添加请求中的Query参数
     * <p>当请求中不存在与该方法调用时传递过来{@link ForestQueryParameter}对象中同名的Query参数时，会将{@link ForestQueryParameter}对象添加成新的Query参数到请求中，</p>
     * <p>若请求中已存在同名Query参数名时，则会替换请求中的所有同名的Query参数值</p>
     *
     * @param queryParameter Query参数，{@link ForestQueryParameter}对象实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest<T> replaceOrAddQuery(ForestQueryParameter queryParameter) {
        List<ForestQueryParameter> queryParameters = this.query.getQueries(queryParameter.getName());
        if (queryParameters.isEmpty()) {
            addQuery(queryParameter);
        } else {
            for (ForestQueryParameter parameter : queryParameters) {
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
        List<ForestQueryParameter> queryParameters = this.query.getQueries(name);
        if (queryParameters.isEmpty()) {
            addQuery(name, value);
        } else {
            for (ForestQueryParameter parameter : queryParameters) {
                parameter.setValue(value);
            }
        }
        return this;
    }

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

    public String getCharset() {
        return charset;
    }

    public ForestRequest<T> setCharset(String charset) {
        this.charset = charset;
        return this;
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
     * 获取请求体对象列表
     *
     * @return 请求体对象列表, 元素为 {@link ForestRequestBody} 其子类实例
     */
    public List<ForestRequestBody> getBody() {
        return bodyItems;
    }

    @Deprecated
    public List getBodyList() {
        return bodyItems;
    }

    @Deprecated
    public void setBodyList(List bodyList) {
        this.bodyItems = bodyList;
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
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置请求超时时间，时间单位为毫秒
     *
     * @param timeout 请求超时时间
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setTimeout(int timeout) {
        this.timeout = timeout;
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
    public int getRetryCount() {
        return maxRetryCount;
    }

    /**
     * 设置请求失败后的重试次数
     *
     * @param retryCount 重试次数
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setRetryCount(int retryCount) {
        this.maxRetryCount = retryCount;
        return this;
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
        this.bodyItems.add(body);
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
            return addBody(nameValue);
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
        this.bodyItems.clear();
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
        this.bodyItems.clear();
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

    public List<ForestQueryParameter> getQueryValues() {
        return query.queryValues();
    }

    public List<RequestNameValue> getDataNameValueList() {
        List<RequestNameValue> nameValueList = new ArrayList<>();
        for (ForestRequestBody item : bodyItems) {
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
        List<RequestNameValue> nameValueList = new ArrayList<RequestNameValue>();
        for (Iterator<ForestHeader> iterator = headers.headerIterator(); iterator.hasNext(); ) {
            ForestHeader header = iterator.next();
            RequestNameValue nameValue = new RequestNameValue(header.getName(), header.getValue(), TARGET_HEADER);
            nameValueList.add(nameValue);
        }
        return nameValueList;
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
     * 获取该请求对应方法的所有参数值
     *
     * @return 参数值列表
     */
    public Object[] getArguments() {
        return arguments;
    }


    /**
     * 获取该请求的所有请求头信息
     *
     * @return 请求头表，{@link ForestHeaderMap}类实例
     */
    public ForestHeaderMap getHeaders() {
        return headers;
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
     * 根据请求头名称获取该请求的请求头的值
     *
     * @param name 请求头名称
     * @return 请求头的值
     */
    public String getHeaderValue(String name) {
        return headers.getValue(name);
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
        this.headers.setHeader(name, String.valueOf(value));
        return this;
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

    public List<ForestMultipart> getMultiparts() {
        return multiparts;
    }

    /**
     * 设置 Multiparts 列表
     *
     * @param multiparts {@link ForestMultipart} 对象列表
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> setMultiparts(List<ForestMultipart> multiparts) {
        this.multiparts = multiparts;
        return this;
    }

    /**
     * 添加 Multipart
     *
     * @param multipart {@link ForestMultipart} 对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest<T> addMultipart(ForestMultipart multipart) {
        if (this.multiparts == null) {
            this.multiparts = new LinkedList<>();
        }
        this.multiparts.add(multipart);
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
     * 获取OnSuccess回调函数，该回调函数在请求成功时被调用
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
        if (conditionClass != null) {
            try {
                SuccessWhen condition = conditionClass.newInstance();
                setSuccessWhen(condition);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
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
        if (conditionClass != null) {
            try {
                RetryWhen condition = conditionClass.newInstance();
                setRetryWhen(condition);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
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
            attributes = new InterceptorAttributes(interceptorClass, new HashMap<>());
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
        return attachments.get(name);
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
            }
        }
        return null;
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
     * @throws Throwable
     */
    public final ForestRetryException canRetry(ForestResponse<?> response, ForestRetryException ex) throws Throwable {
        if (ex == null) {
            ex = new ForestRetryException(this, maxRetryCount, getCurrentRetryCount());
        }
        if (response == null || !retryEnabled) {
            throw ex.getCause();
        }
        HttpExecutor executor = backend.createExecutor(this, lifeCycleHandler);
        if (executor != null) {
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
     * 执行请求发送过程
     *
     * @param backend HTTP后端，{@link HttpBackend}接口实例
     * @param lifeCycleHandler 生命周期处理器，{@link LifeCycleHandler}接口实例
     * @return 接受到请求响应后，其响应内容反序列化成对象的结果
     */
    public Object execute(HttpBackend backend, LifeCycleHandler lifeCycleHandler) {
        setLifeCycleHandler(lifeCycleHandler);
        if (interceptorChain.beforeExecute(this)) {
            HttpExecutor executor  = backend.createExecutor(this, lifeCycleHandler);
            if (executor != null) {
                try {
                    executor.execute(lifeCycleHandler);
                } catch (ForestRuntimeException e) {
                    throw e;
                } finally {
                    executor.close();
                }
            }
        }
        return getMethodReturnValue();
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
