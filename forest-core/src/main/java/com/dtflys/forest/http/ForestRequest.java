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

import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.interceptor.InterceptorAttributes;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.logging.LogHandler;
import com.dtflys.forest.multipart.ForestMultipart;
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
     * URL中的Query参数表
     */
    private ForestQueryMap query = new ForestQueryMap();

    /**
     * 请求类型
     */
    private ForestRequestType type;

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
     * 回调函数：上传/下载进度监听时调用
     * 每上传/下载传输 ${progressStep} 个比特数时，执行一次监听回调函数
     */
    private OnProgress onProgress;


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
     * 日志处理器
     */
    private LogHandler logHandler;

    /**
     * SSL KeyStore信息
     * 在双向HTTPS请求中使用的验证信息
     */
    private SSLKeyStore keyStore;

    public ForestRequest(ForestConfiguration configuration, Object[] arguments) {
        this.configuration = configuration;
        this.arguments = arguments;
    }

    public ForestRequest(ForestConfiguration configuration) {
        this(configuration, new Object[0]);
    }


    public ForestConfiguration getConfiguration() {
        return configuration;
    }

    public String getProtocol() {
        return protocol;
    }

    public ForestRequest setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ForestRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public ForestRequest setUserInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public Object getQuery(String name) {
        return query.get(name);
    }

    public String getQueryString() {
        StringBuilder builder = new StringBuilder();
        Iterator<ForestQueryParameter> iterator = query.queryValues().iterator();
        query.values();
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
                    builder.append(value);
                }
            }
            if (iterator.hasNext()) {
                builder.append("&");
            }
        }
        return builder.toString();
    }

    public ForestRequest addQuery(String name, Object value) {
        this.query.addQuery(name, value);
        return this;
    }

    public ForestRequest addQuery(ForestQueryParameter queryParameter) {
        this.query.addQuery(queryParameter);
        return this;
    }

    public ForestRequest addQuery(Collection<ForestQueryParameter> queryParameters) {
        for (ForestQueryParameter queryParameter : queryParameters) {
            addQuery(queryParameter);
        }
        return this;
    }

    public ForestRequest addQueryValues(String name, Collection queryValues) {
        for (Object queryValue : queryValues) {
            addQuery(name, queryValue);
        }
        return this;
    }


    public ForestRequest addQuery(ForestQueryParameter[] queryParameters) {
        for (ForestQueryParameter queryParameter : queryParameters) {
            addQuery(queryParameter);
        }
        return this;
    }


    public ForestRequestType getType() {
        return type;
    }

    public ForestRequest setType(ForestRequestType type) {
        this.type = type;
        return this;
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

    public String getContentType() {
        return headers.getValue("Content-Type");
    }

    public ForestRequest setContentType(String contentType) {
        addHeader("Content-Type", contentType);
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public ForestRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getSslProtocol() {
        return sslProtocol;
    }

    public ForestRequest setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
        return this;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public ForestRequest setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public long getMaxRetryInterval() {
        return maxRetryInterval;
    }

    public ForestRequest setMaxRetryInterval(long maxRetryInterval) {
        this.maxRetryInterval = maxRetryInterval;
        return this;
    }

    /**
     * 旧的获取Body数据的方法，已不建议使用
     * @return
     */
    @Deprecated
    public Map<String, Object> getData() {
        return null;
    }

    /**
     * 添加Body数据
     * @param body
     * @return
     */
    public ForestRequest addBody(ForestRequestBody body) {
        this.bodyItems.add(body);
        return this;
    }


    /**
     * 添加字符串Body数据
     * @param stringBody 请求体字符串内容
     * @return
     */
    public ForestRequest addBody(String stringBody) {
        return addBody(new StringRequestBody(stringBody));
    }

    /**
     * 添加键值对类型Body数据
     * @param name 字段名
     * @param value 字段值
     * @return
     */
    public ForestRequest addBody(String name, Object value) {
        return addBody(new NameValueRequestBody(name, value));
    }

    /**
     * 添加键值对类型Body数据
     * @param nameValue 请求键值对对象
     * @return
     */
    @Deprecated
    public ForestRequest addBody(RequestNameValue nameValue) {
        return addBody(new NameValueRequestBody(nameValue.getName(), nameValue.getValue()));
    }

    /**
     * 批量添加键值对类型Body数据
     * @param nameValueList 请求键值对对象列表
     * @return
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
     * @param name
     * @param value
     * @return
     */
    @Deprecated
    public ForestRequest addData(String name, Object value) {
        return addBody(name, value);
    }

    /**
     * 添加键值对类型Body数据, 已不再建议使用
     * @param nameValue 请求键值对对象
     * @return
     */
    @Deprecated
    public ForestRequest addData(RequestNameValue nameValue) {
        return addNameValue(nameValue);
    }

    /**
     * 批量添加键值对类型Body数据, 已不再建议使用
     * @param data 请求键值对对象列表
     * @return
     */
    @Deprecated
    public ForestRequest addData(List<RequestNameValue> data) {
        return addNameValue(data);
    }

    /**
     * 添加键值对
     * @param nameValue 键值对对象
     * @return
     */
    public ForestRequest addNameValue(RequestNameValue nameValue) {
        if (nameValue.isInHeader()) {
            this.addHeader(nameValue.getName(), nameValue.getValue());
        } else if (nameValue.isInQuery()) {
            this.addQuery(nameValue.getName(), nameValue.getValue());
        } else if (nameValue.isInBody()) {
            this.addBody(nameValue.getName(), nameValue.getValue());
        }
        return this;
    }

    /**
     * 添加键值对列表
     * @param nameValueList 键值对列表
     * @return
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
     * @return
     */
    public ForestRequest replaceBody(ForestRequestBody body) {
        this.bodyItems.clear();
        this.addBody(body);
        return this;
    }

    /**
     * 替换Body为新的字符串数据，原有的Body数据将被清空
     * @param stringbody 字符串请求体
     * @return
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
                RequestNameValue nameValue = new RequestNameValue(name, value, TARGET_BODY);
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


    public Object getArgument(int index) {
        return arguments[index];
    }

    public Object[] getArguments() {
        return arguments;
    }

    public ForestHeaderMap getHeaders() {
        return headers;
    }

    public ForestHeader getHeader(String name) {
        return headers.getHeader(name);
    }

    public String getHeaderValue(String name) {
        return headers.getValue(name);
    }

    public ForestRequest addHeader(String name, Object value) {
        if (StringUtils.isEmpty(name)) {
            return this;
        }
        this.headers.setHeader(name, String.valueOf(value));
        return this;
    }

    public ForestRequest addHeader(RequestNameValue nameValue) {
        this.addHeader(nameValue.getName(), nameValue.getValue());
        return this;
    }


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

    private void putMapAddList(Map<String, Object> map, List<RequestNameValue> source) {
        for (int i = 0; i < source.size(); i++) {
            RequestNameValue nameValue = source.get(i);
            if (nameValue.isInQuery()) {
                addQuery(nameValue.getName(), nameValue.getValue());
            } else if (nameValue.isInBody()) {
                map.put(nameValue.getName(), nameValue.getValue());
            }
        }
    }


    public OnSuccess getOnSuccess() {
        return onSuccess;
    }

    public ForestRequest setOnSuccess(OnSuccess onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public OnError getOnError() {
        return onError;
    }

    public ForestRequest setOnError(OnError onError) {
        this.onError = onError;
        return this;
    }

    public boolean isDownloadFile() {
        return isDownloadFile;
    }

    public void setDownloadFile(boolean downloadFile) {
        isDownloadFile = downloadFile;
    }

    public long getProgressStep() {
        return progressStep;
    }

    public ForestRequest setProgressStep(long progressStep) {
        this.progressStep = progressStep;
        return this;
    }

    public OnProgress getOnProgress() {
        return onProgress;
    }

    public ForestRequest setOnProgress(OnProgress onProgress) {
        this.onProgress = onProgress;
        return this;
    }

    public ForestRequest<T> addInterceptor(Interceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
        return this;
    }

    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

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

    public ForestRequest addInterceptorAttribute(Class interceptorClass, String attributeName, Object attributeValue) {
        InterceptorAttributes attributes = getInterceptorAttributes(interceptorClass);
        if (attributes == null) {
            attributes = new InterceptorAttributes(interceptorClass, new HashMap<>());
            addInterceptorAttributes(interceptorClass, attributes);
        }
        attributes.addAttribute(attributeName, attributeValue);
        return this;
    }


    public Map<Class, InterceptorAttributes> getInterceptorAttributes() {
        return interceptorAttributes;
    }


    public InterceptorAttributes getInterceptorAttributes(Class interceptorClass) {
        return interceptorAttributes.get(interceptorClass);
    }


    public Object getInterceptorAttribute(Class interceptorClass, String attributeName) {
        InterceptorAttributes attributes = interceptorAttributes.get(interceptorClass);
        if (attributes == null) {
            return null;
        }
        return attributes.getAttribute(attributeName);
    }


    public Retryer getRetryer() {
        return retryer;
    }

    public ForestRequest setRetryer(Retryer retryer) {
        this.retryer = retryer;
        return this;
    }

    public ForestRequest addAttachment(String name, Object value) {
        attachments.put(name, value);
        return this;
    }

    public Object getAttachment(String name) {
        return attachments.get(name);
    }

    public ForestConverter getDecoder() {
        return decoder;
    }

    public ForestRequest setDecoder(ForestConverter decoder) {
        this.decoder = decoder;
        return this;
    }

    @Deprecated
    public boolean isLogEnable() {
        if (logConfiguration == null) {
            return true;
        }
        return logConfiguration.isLogEnabled();
    }

    public LogConfiguration getLogConfiguration() {
        return logConfiguration;
    }

    public ForestRequest setLogConfiguration(LogConfiguration logConfiguration) {
        this.logConfiguration = logConfiguration;
        return this;
    }

    public LogHandler getLogHandler() {
        return logHandler;
    }

    public ForestRequest setLogHandler(LogHandler logHandler) {
        this.logHandler = logHandler;
        return this;
    }

    public SSLKeyStore getKeyStore() {
        return keyStore;
    }

    public ForestRequest setKeyStore(SSLKeyStore keyStore) {
        this.keyStore = keyStore;
        return this;
    }

    /**
     * Execute request
     * @param backend
     * @param lifeCycleHandler
     */
    public void execute(HttpBackend backend, LifeCycleHandler lifeCycleHandler) {
        HttpExecutor executor  = backend.createExecutor(this, lifeCycleHandler);
        if (executor != null) {
            if (interceptorChain.beforeExecute(this)) {
                try {
                    executor.execute(lifeCycleHandler);
                } catch (ForestRuntimeException e) {
                    throw e;
                } finally {
                    executor.close();
                }
            }
        }
    }

}
