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

import com.dtflys.forest.callback.OnLoadCookie;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.http.body.ByteArrayRequestBody;
import com.dtflys.forest.http.body.FileRequestBody;
import com.dtflys.forest.http.body.InputStreamRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.http.body.StringRequestBody;
import com.dtflys.forest.interceptor.InterceptorAttributes;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.retryer.Retryer;
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
import com.dtflys.forest.utils.URLUtils;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
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
    private LifeCycleHandler lifeCycleHandler;

    /**
     * HTTP协议
     */
    private String protocol;

    /**
     * URL路径
     */
    private String url;

    /**
     * 用户信息
     * 包含在URL中的用户信息，比如：
     * URL http://xxx:yyy@localhost:8080 中 xxx:yyy 的部分为用户信息
     * 其中，xxx为用户名，yyy为用户密码
     */
    private String userInfo;

    /**
     * URL井号(#)后面的字符串
     */
    private String ref;

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
     * 该类型决定请求响应返回的数据将以何种方式进行反序列化
     */
    private ForestDataType dataType;

    /**
     * 请求超时时间
     */
    private int timeout = 3000;

    /**
     * SSL协议
     * 该字段在单向HTTPS请求发送时决定哪种SSL协议
     */
    private String sslProtocol;

    /**
     * 请求失败后的重试次数
     */
    private int retryCount = 0;

    /**
     * 最大请重试的时间间隔，时间单位为毫秒
     */
    private long maxRetryInterval = 0;

//    private Map<String, Object> data = new LinkedHashMap<String, Object>();

    /**
     * 请求体
     * 该字段为列表类型，列表每一项为请求体项
     * 都为ForestRequestBody子类的对象实例
     */
    private List<ForestRequestBody> bodyItems = new LinkedList<>();


    /**
     * 请求体集合
     * 所有本请求对象中的请求头都在由该请求体集合对象管理
     */
    private ForestHeaderMap headers = new ForestHeaderMap();


    /**
     * 文件上传项列表
     */
    private List<ForestMultipart> multiparts = new LinkedList<>();

    /**
     * 文件名
     * 该字段为文件下载时，保存到本地磁盘时用到的文件名
     */
    private String filename;

    /**
     * 参数表
     * 接口方法调用时传入的参数表
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
     * 进度回调函数：上传/下载进度监听时调用
     * 每上传/下载传输 ${progressStep} 个比特数时，执行一次监听回调函数
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
     * 每上传/下载一定的比特数，执行一次监听回调函数
     */
    private long progressStep = DEFAULT_PROGRESS_STEP;

    /**
     * 拦截器链对象
     */
    private InterceptorChain interceptorChain = new InterceptorChain();

    /**
     * 拦截器属性
     * 这里的属性只能在绑定的拦截器中被访问
     */
    private Map<Class, InterceptorAttributes> interceptorAttributes = new HashMap<>();

    /**
     * 请求重试策略
     * 可以通过该字段设定自定义的重试策略
     */
    private Retryer retryer;

    /**
     * 附件
     * 附件信息不回随请求发送到远端服务器，但在本地的任何地方都可以通过请求对象访问到附件信息
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
     * 在双向HTTPS请求中使用的验证信息
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
     * @return 配置对象
     */
    public ForestConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * 获取请求协议
     * @return 请求协议
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * 设置请求协议
     * @param protocol 请求协议
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * 获取请求URL
     * @return URL字符串
     */
    public String getUrl() {
        return url;
    }

    /**
     * 获取请求URI
     * @return {@link URI}类实例
     */
    public URI getURI() {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new ForestRuntimeException(e);
        }
    }

    /**
     * 设置请求的url地址
     * <p>每次设置请求的url地址时，都会解析传入的url参数字符串，</p>
     * <p>然后从url中解析出Query参数，并将其替换调用原来的Query参数，或新增成新的Query参数</p>
     *
     * @param url url地址字符串
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest setUrl(String url) {
        if (StringUtils.isBlank(url)) {
            throw new ForestRuntimeException("[Forest] Request url cannot be empty!");
        }
        String newUrl = null;
        String srcUrl = StringUtils.trimBegin(url);
        String query = "";
        String protocol = "";
        String userInfo = null;
        String ref = null;

        if (!this.query.isEmpty()) {
            this.query.clearQueriesFromUrl();
        }

        try {
            URL u = new URL(srcUrl);
            query = u.getQuery();
            userInfo = u.getUserInfo();
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
                            requestNameValue.getName(), requestNameValue.getValue(), true));
                }
            }

            protocol = u.getProtocol();
            ref = u.getRef();
            if (StringUtils.isNotEmpty(ref)) {
                ref = URLEncoder.encode(ref, "UTF-8");
            }
            int port = u.getPort();
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(protocol).append("://");
            if (userInfo != null) {
                urlBuilder.append(userInfo).append('@');
            }
            urlBuilder.append(u.getHost());
            if (port != 80 && port != 443 && port > -1 ||
                    port == 80 && !protocol.equals("http") ||
                    port == 443 && !protocol.equals("https")) {
                urlBuilder.append(':').append(port);
            }

            String path = u.getPath();
            if (StringUtils.isNotEmpty(path)) {
                urlBuilder.append(path);
            }
            newUrl = urlBuilder.toString();
        } catch (MalformedURLException e) {
            throw new ForestRuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new ForestRuntimeException(e);
        }

        this.url = newUrl;
        if (StringUtils.isNotEmpty(protocol)) {
            this.protocol = protocol;
        }
        if (StringUtils.isNotEmpty(userInfo)) {
            this.userInfo = userInfo;
        }
        if (StringUtils.isNotEmpty(ref)) {
            this.ref = ref;
        }
        return this;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public ForestRequest setUserInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    /**
     * 获取URL井号(#)后面的字符串
     * @return
     */
    public String getRef() {
        return ref;
    }

    /**
     * 设置URL井号(#)后面的字符串
     * @param ref
     */
    public ForestRequest setRef(String ref) {
        this.ref = ref;
        return this;
    }

    /**
     * 获取请求对应的Forest方法
     * @return Forest方法对象
     */
    public ForestMethod getMethod() {
        return method;
    }

    /**
     * 获取HTTP后端对象
     * @return HTTP后端对象，{@link HttpBackend}接口实例
     */
    public HttpBackend getBackend() {
        return backend;
    }

    /**
     * 设置HTTP后端对象
     * @param backend HTTP后端对象，{@link HttpBackend}接口实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest setBackend(HttpBackend backend) {
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
     * 获取请求的Query参数表
     * @return Query参数表
     */
    public ForestQueryMap getQuery() {
        return query;
    }

    /**
     * 根据名称获取请求的Query参数值
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
                        String encodedValue = URLUtils.urlEncoding(value.toString(), getCharset());
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
     * @param name Query参数名
     * @param value Query参数值
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest addQuery(String name, Object value) {
        this.query.addQuery(name, value);
        return this;
    }

    /**
     * 添加请求中的Query参数
     * @param queryParameter Query参数，{@link ForestQueryParameter}对象实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest addQuery(ForestQueryParameter queryParameter) {
        this.query.addQuery(queryParameter);
        return this;
    }

    /**
     * 批量添加请求中的Query参数
     * @param queryParameters Query参数集合，{@link ForestQueryParameter}对象实例集合
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest addQuery(Collection<ForestQueryParameter> queryParameters) {
        for (ForestQueryParameter queryParameter : queryParameters) {
            addQuery(queryParameter);
        }
        return this;
    }

    /**
     * 批量添加请求中的同名Query参数
     * @param name Query参数名
     * @param queryValues Query参数值集合
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest addQueryValues(String name, Collection queryValues) {
        for (Object queryValue : queryValues) {
            addQuery(name, queryValue);
        }
        return this;
    }

    /**
     * 批量添加请求中的Query参数
     * @param queryParameters Query参数数组，{@link ForestQueryParameter}对象实例数组
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest addQuery(ForestQueryParameter[] queryParameters) {
        for (ForestQueryParameter queryParameter : queryParameters) {
            addQuery(queryParameter);
        }
        return this;
    }

    /**
     * 替换请求中的Query参数值
     * @param queryParameter Query参数，{@link ForestQueryParameter}对象实例
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest replaceQuery(ForestQueryParameter queryParameter) {
        List<ForestQueryParameter> queryParameters = this.query.getQueries(queryParameter.getName());
        for (ForestQueryParameter parameter : queryParameters) {
            parameter.setValue(queryParameter.getValue());
        }
        return this;
    }


    /**
     * 替换请求中的Query参数值
     * @param name Query参数名
     * @param value Query参数值
     * @return {@link ForestRequest}对象实例
     */
    public ForestRequest replaceQuery(String name, Object value) {
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
    public ForestRequest replaceOrAddQuery(ForestQueryParameter queryParameter) {
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
     * <p>当请求中不存在与该方法调用时传递过来{@code name}参数同名的Query参数时，会将{@code name}参数和{@code value}参数添加成新的Query参数到请求中，</p>
     * <p>若请求中已存在同名Query参数名时，则会替换请求中的所有同名的Query参数值</p>
     *
     * @param name Query参数名
     * @param value Query参数值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest replaceOrAddQuery(String name, String value) {
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

    public ForestRequest setType(ForestRequestType type) {
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
     * @return 请求类型列表
     */
    public List<ForestRequestType> getTypeChangeHistory() {
        return typeChangeHistory;
    }

    /**
     * 获取请求类型变更历史字符串列表
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

    public ForestRequest setContentEncoding(String contentEncoding) {
        addHeader("Content-Encoding", contentEncoding);
        return this;
    }

    public String getUserAgent() {
        return headers.getValue("User-Agent");
    }

    public ForestRequest setUserAgent(String userAgent) {
        addHeader("User-Agent", userAgent);
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public ForestRequest setCharset(String charset) {
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

    public boolean isAsync() {
        return async;
    }

    public ForestRequest setAsync(boolean async) {
        this.async = async;
        return this;
    }

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

    public ForestRequest setDataType(ForestDataType dataType) {
        this.dataType = dataType;
        return this;
    }

    /**
     * 获取请求头 Content-Type 的值
     * @return 请求头 Content-Type 的值
     */
    public String getContentType() {
        return headers.getValue("Content-Type");
    }

    /**
     * 设置请求头 Content-Type 的值
     * @param contentType 请求头 Content-Type 的值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setContentType(String contentType) {
        addHeader("Content-Type", contentType);
        return this;
    }

    /**
     * 获取请求超时时间，时间单位为毫秒
     * @return 请求超时时间
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置请求超时时间，时间单位为毫秒
     * @param timeout 请求超时时间
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 获取SSL协议
     * @return SSL协议字符串
     */
    public String getSslProtocol() {
        return sslProtocol;
    }

    /**
     * 设置SSL协议
     * @param sslProtocol SSL协议字符串
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
        return this;
    }

    /**
     * 获取请求失败后的重试次数
     * @return 重试次数
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * 设置请求失败后的重试次数
     * @param retryCount 重试次数
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    /**
     * 获取最大请重试的时间间隔，时间单位为毫秒
     * @return 最大请重试的时间间隔
     */
    public long getMaxRetryInterval() {
        return maxRetryInterval;
    }

    /**
     * 设置最大请重试的时间间隔，时间单位为毫秒
     * @param maxRetryInterval 最大请重试的时间间隔
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setMaxRetryInterval(long maxRetryInterval) {
        this.maxRetryInterval = maxRetryInterval;
        return this;
    }

    /**
     * 旧的获取Body数据的方法 [已不建议使用]
     * @return 请求中的数据，{@link Map}对象实例
     */
    @Deprecated
    public Map<String, Object> getData() {
        return null;
    }

    /**
     * 添加Body数据
     * @param body Forest请求体，{@link ForestRequestBody}类实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addBody(ForestRequestBody body) {
        this.bodyItems.add(body);
        return this;
    }


    /**
     * 添加字符串Body数据
     * @param stringBody 请求体字符串内容
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addBody(String stringBody) {
        return addBody(new StringRequestBody(stringBody));
    }

    /**
     * 添加字节数组Body数据
     * @param byteArrayBody 请求体字节数组内容
     * @return
     */
    public ForestRequest addBody(byte[] byteArrayBody) {
        return addBody(new ByteArrayRequestBody(byteArrayBody));
    }

    /**
     * 添加文件Body数据
     * @param fileBody 请求体文件内容
     * @return
     */
    public ForestRequest addBody(File fileBody) {
        return addBody(new FileRequestBody(fileBody));
    }

    /**
     * 添加输入流Body数据
     * @param inputStreamBody 请求体输入流内容
     * @return
     */
    public ForestRequest addBody(InputStream inputStreamBody) {
        return addBody(new InputStreamRequestBody(inputStreamBody));
    }

    /**
     * 添加键值对类型Body数据
     * @param name 字段名
     * @param value 字段值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addBody(String name, Object value) {
        return addBody(new NameValueRequestBody(name, value));
    }

    /**
     * 添加键值对类型Body数据
     * @param name 字段名
     * @param contentType 该请求体项的Content-Type
     * @param value 字段值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addBody(String name, String contentType, Object value) {
        return addBody(new NameValueRequestBody(name, contentType, value));
    }

    /**
     * 添加键值对类型Body数据
     * @param nameValue 请求键值对对象
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest addBody(RequestNameValue nameValue) {
        return addBody(new NameValueRequestBody(nameValue.getName(), nameValue.getValue()));
    }

    /**
     * 批量添加键值对类型Body数据
     * @param nameValueList 请求键值对对象列表
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest addBody(List<RequestNameValue> nameValueList) {
        for (RequestNameValue nameValue : nameValueList) {
            return addBody(nameValue);
        }
        return this;
    }

    /**
     * 添加键值对类型Body数据, 已不再建议使用
     * @param name 键值对名称
     * @param value 键值对的值
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest addData(String name, Object value) {
        return addBody(name, value);
    }

    /**
     * 添加键值对类型Body数据, 已不再建议使用
     * @param nameValue 请求键值对对象
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest addData(RequestNameValue nameValue) {
        return addNameValue(nameValue);
    }

    /**
     * 批量添加键值对类型Body数据, 已不再建议使用
     * @param data 请求键值对对象列表
     * @return {@link ForestRequest}类实例
     */
    @Deprecated
    public ForestRequest addData(List<RequestNameValue> data) {
        return addNameValue(data);
    }

    /**
     * 添加键值对
     * @param nameValue 键值对对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addNameValue(RequestNameValue nameValue) {
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
     * @param nameValueList 键值对列表
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addNameValue(List<RequestNameValue> nameValueList) {
        for (RequestNameValue nameValue : nameValueList) {
            addNameValue(nameValue);
        }
        return this;
    }

    /**
     * 替换Body数据，原有的Body数据将被清空
     * @param body 请求体对象
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest replaceBody(ForestRequestBody body) {
        this.bodyItems.clear();
        this.addBody(body);
        return this;
    }

    /**
     * 替换Body为新的字符串数据，原有的Body数据将被清空
     * @param stringbody 字符串请求体
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest replaceBody(String stringbody) {
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
    public ForestRequest addHeader(String name, Object value) {
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
    public ForestRequest addHeader(RequestNameValue nameValue) {
        this.addHeader(nameValue.getName(), nameValue.getValue());
        return this;
    }

    /**
     * 批量添加请求头到该请求中
     *
     * @param nameValues 请求头键值对列表
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addHeaders(List<RequestNameValue> nameValues) {
        for (RequestNameValue nameValue : nameValues) {
            this.addHeader(nameValue.getName(), nameValue.getValue());
        }
        return this;
    }

    public List<ForestMultipart> getMultiparts() {
        return multiparts;
    }

    public ForestRequest setMultiparts(List<ForestMultipart> multiparts) {
        this.multiparts = multiparts;
        return this;
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
    public ForestRequest setOnSuccess(OnSuccess onSuccess) {
        this.onSuccess = onSuccess;
        return this;
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
     * 设置OnError回调函数，该回调函数在请求成功时被调用
     *
     * @param onError {@link OnError}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setOnError(OnError onError) {
        this.onError = onError;
        return this;
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
    public ForestRequest setDownloadFile(boolean downloadFile) {
        isDownloadFile = downloadFile;
        return this;
    }

    /**
     * 获取上传/下载进度监听的步长
     * <p>每上传/下载一定的比特数，执行一次监听回调函数</p>
     *
     * @return 进度监听的步长，{@code long}类型数值
     */
    public long getProgressStep() {
        return progressStep;
    }

    /**
     * 设置获取上传/下载进度监听的步长
     * <p>每上传/下载一定的比特数，执行一次监听回调函数</p>
     *
     * @param progressStep 进度监听的步长，{@code long}类型数值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setProgressStep(long progressStep) {
        this.progressStep = progressStep;
        return this;
    }

    /**
     * 获取进度回调函数：上传/下载进度监听时调用
     * <p>每上传/下载传输 ${progressStep} 个比特数时，执行一次监听回调函数</p>
     *
     * @return 进度回调函数，{@link OnProgress}接口实例
     */
    public OnProgress getOnProgress() {
        return onProgress;
    }

    /**
     * 设置进度回调函数：上传/下载进度监听时调用
     * <p>每上传/下载传输 ${progressStep} 个比特数时，执行一次监听回调函数</p>
     *
     * @param onProgress 进度回调函数，{@link OnProgress}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setOnProgress(OnProgress onProgress) {
        this.onProgress = onProgress;
        return this;
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
    public ForestRequest setOnLoadCookie(OnLoadCookie onLoadCookie) {
        this.onLoadCookie = onLoadCookie;
        return this;
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
    public ForestRequest setOnSaveCookie(OnSaveCookie onSaveCookie) {
        this.onSaveCookie = onSaveCookie;
        return this;
    }

    /**
     * 添加拦截器到该请求中
     * <p>拦截器在请求的初始化、发送请求前、发送成功、发送失败等生命周期中都会被调用</p>
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
     * <p>被添加的属性会被对应的请求所绑定，同时也会绑定到拦截器类，并且按不同的拦截器进行隔离。</p>
     * <p>而且这些属性值不能通过网络请求传递到远端服务器。</p>
     * <p>
     *     拦截器属性有两个特性：<br>
     *     1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。<br>
     *     2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。<br>
     *     也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。
     * </p>
     * @param interceptorClass 要绑定的拦截器类
     * @param attributes 拦截器属性，{@link InterceptorAttributes}类实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addInterceptorAttributes(Class interceptorClass, InterceptorAttributes attributes) {
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
     * <p>被添加的属性会被对应的请求所绑定，同时也会绑定到拦截器类，并且按不同的拦截器进行隔离。</p>
     * <p>而且这些属性值不能通过网络请求传递到远端服务器。</p>
     * <p>
     *     拦截器属性有两个特性：<br>
     *     1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。<br>
     *     2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。<br>
     *     也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。
     * </p>
     *
     * @param interceptorClass 要绑定的拦截器类
     * @param attributeName 拦截器属性名
     * @param attributeValue 拦截器属性值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addInterceptorAttribute(Class interceptorClass, String attributeName, Object attributeValue) {
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
     * <p>
     *     拦截器属性有两个特性：<br>
     *     1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。<br>
     *     2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。<br>
     *     也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。
     * </p>
     *
     * @return {@link Map}映射，Key: 拦截器类，Value: 拦截器属性集合对象，{@link InterceptorAttributes}类实例
     */
    public Map<Class, InterceptorAttributes> getInterceptorAttributes() {
        return interceptorAttributes;
    }

    /**
     * 根据拦截器类获取拦截器属性集合对象
     * <p>
     *     拦截器属性有两个特性：<br>
     *     1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。<br>
     *     2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。<br>
     *     也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。
     * </p>
     *
     * @param interceptorClass 拦截器类
     * @return 拦截器属性集合对象，{@link InterceptorAttributes}类实例
     */
    public InterceptorAttributes getInterceptorAttributes(Class interceptorClass) {
        return interceptorAttributes.get(interceptorClass);
    }

    /**
     * 根据拦截器类和拦截器属性名获取拦截器属性值
     * <p>
     *     拦截器属性有两个特性：<br>
     *     1. 按请求隔离： 如有请求A和请求B，都有一个名为attr1的拦截器属性，但它们是两个互不影响的独立的属性。<br>
     *     2. 按拦截器隔离：如果有拦截器T1和拦截器T2，同一个请求分别对着两个拦截器绑定了一个属性，都名为 attr1。但它们也是两个独立的互不干涉的属性。<br>
     *     也就是说，在拦截器T1中访问的attr1属性和在拦截器T2中访问的attr1属性是不同的。
     * </p>
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
     * @return Forest请求重试器，{@link Retryer}接口实例
     */
    public Retryer getRetryer() {
        return retryer;
    }

    /**
     * 设置Forest请求重试器
     *
     * @param retryer Forest请求重试器，{@link Retryer}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setRetryer(Retryer retryer) {
        this.retryer = retryer;
        return this;
    }

    /**
     * 添加附件到请求中
     * <p>Attachment 是和请求绑定的附件属性值，这些值不能通过网络请求传递到远端服务器。</p>
     * <p>不同请求的附件相互独立，即使名称相同，也互不影响。</p>
     *
     * @param name 附件名
     * @param value 附件值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest addAttachment(String name, Object value) {
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
     * @param decoder 反序列化器，{@link ForestConverter}接口实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setDecoder(ForestConverter decoder) {
        this.decoder = decoder;
        return this;
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

    public ForestRequest setLogConfiguration(LogConfiguration logConfiguration) {
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
     * <p>在双向HTTPS请求中使用的验证信息</p>
     *
     * @param keyStore SSL KeyStore信息，{@link SSLKeyStore}类实例
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest setKeyStore(SSLKeyStore keyStore) {
        this.keyStore = keyStore;
        return this;
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
    public ForestRequest setProxy(ForestProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * 设置该请求对应的方法返回值
     *
     * @param result 方法返回值
     * @return {@link ForestRequest}类实例
     */
    public ForestRequest methodReturn(T result) {
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
                return ((MethodLifeCycleHandler) lifeCycleHandler).getResultData();
            }
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

}
