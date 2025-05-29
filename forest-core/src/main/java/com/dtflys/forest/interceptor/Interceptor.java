package com.dtflys.forest.interceptor;

import com.dtflys.forest.callback.OnCanceled;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnLoadCookie;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnRedirection;
import com.dtflys.forest.callback.OnResponse;
import com.dtflys.forest.callback.OnRetry;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestProgress;

import java.util.function.Supplier;

/**
 * Forest拦截器接口
 * <p>拦截器在请求的初始化、发送请求前、发送成功、发送失败等生命周期中都会被调用
 * <p>总的生命周期回调函数调用顺序如下:
 * <pre>
 * Forest接口方法调用 -&gt;
 *  &#166; onInvokeMethod -&gt;
 *  &#166; beforeExecute -&gt;
 *  &#166; &#166; 如果返回 false -&gt; 中断请求，直接返回
 *  &#166; &#166; 如果返回 true -&gt;
 *  &#166; &#166; &#166; 发送请求 -&gt;
 *  &#166; &#166; &#166; &#166; 发送请求失败 -&gt;
 *  &#166; &#166; &#166; &#166; &#166; retryWhen -&gt;
 *  &#166; &#166; &#166; &#166; &#166; &#166; 返回 true 则触发请求重试
 *  &#166; &#166; &#166; &#166; &#166; &#166; 返回 false 则跳转到 [onError]
 *  &#166; &#166; &#166; &#166; &#166; onError -&gt; 跳转到 [afterExecute]
 *  &#166; &#166; &#166; &#166; 发送请求成功 -&gt;
 *  &#166; &#166; &#166; &#166; &#166; 等待响应 -&gt;
 *  &#166; &#166; &#166; &#166; &#166; 接受到响应 -&gt;
 *  &#166; &#166; &#166; &#166; &#166; retryWhen -&gt;
 *  &#166; &#166; &#166; &#166; &#166; &#166; 返回 true 则触发请求重试
 *  &#166; &#166; &#166; &#166; &#166; &#166; 返回 false 判断响应状态 -&gt;
 *  &#166; &#166; &#166; &#166; &#166; &#166; &#166; 响应失败 -&gt; onError -&gt; 跳转到 [afterExecute]
 *  &#166; &#166; &#166; &#166; &#166; &#166; &#166; 响应成功 -&gt; onSuccess -&gt; 跳转到 [afterExecute]
 *  &#166; afterExecute -&gt; 退出 Forest 接口方法，并返回数据
 * </pre>
 *
 * @deprecated 建议使用 {@link ForestInterceptor}
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-06-26
 */
@Deprecated
public interface Interceptor<T> extends
        OnResponse, OnSuccess<T>, OnError, OnCanceled, OnProgress, OnLoadCookie, OnSaveCookie, OnRetry, OnRedirection {


    /**
     * 默认回调函数: 接口方法执行时调用该方法
     * <p>默认为什么都不做
     *
     * @param request Forest请求对象
     * @param method Forest方法对象
     * @param args 方法调用入参数组
     */
    default void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
    }

    /**
     * 默认回调函数: 请求执行前调用该方法
     * <p>其返回值为布尔类型，可以控制请求是否继续执行
     * <p>默认为什么都不做
     *
     * @param request Forest请求对象
     * @return {@code true}: 继续执行该请求, 否则中断请求
     */
    default boolean beforeExecute(ForestRequest request) {
        return true;
    }

    /**
     * 默认回调函数: 请求完成后(成功/失败后) 调用该方法
     * <p>默认为什么都不做
     *
     * @param request Forest请求对象
     * @param response Forest响应对象
     */
    default void afterExecute(ForestRequest request, ForestResponse response) {
    }

    /**
     * 在请求体数据序列化后，发送请求数据前调用该方法
     * <p>默认为什么都不做
     * <p>注: mutlipart/data类型的文件上传格式的 Body 数据不会调用该回调函数
     *
     * @param request Forest请求对象
     * @param encoder Forest转换器
     * @param encodedData 序列化后的请求体数据
     */
    default byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
        return encodedData;
    }

    /**
     * 默认回调函数: 接受到请求响应时调用该方法
     * <p>默认返回未知状态，继续执行后续逻辑
     *
     * @param request Forest请求对象
     * @param response Forest响应对象
     * @return 请求响应结果: {@link ResponseSuccess} 或 {@link ResponseSuccess} 实例
     * @since 1.6.0
     */
    @Override
    default ResponseResult onResponse(ForestRequest request, ForestResponse response) {
        return ResponseResult.RESPONSE_RESULT_PROCEED;
    }

    /**
     * 默认回调函数: 请求成功后调用该方法
     * <p>默认为什么都不做
     *
     * @param data 请求响应返回后经过序列化后的数据
     * @param request Forest请求对象
     * @param response Forest响应对象
     */
    @Override
    default void onSuccess(T data, ForestRequest request, ForestResponse response) {
    }

    /**
     * 默认回调函数: 请求失败后调用该方法
     * <p>默认为什么都不做
     *
     * @param ex 请求失败的异常对象
     * @param request Forest请求对象
     * @param response Forest响应对象
     */
    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
    }

    /**
     * 默认回调函数: 请求取消后调用该方法
     * <p>默认为什么都不做
     *
     * @param req Forest请求对象
     * @param res Forest响应对象
     */
    @Override
    default void onCanceled(ForestRequest req, ForestResponse res) {
    }

    /**
     * 默认回调函数: 在触发请求重试时执行
     * <p>默认为什么都不做
     *
     * @param request Forest请求对象
     * @param response Forest响应对象
     */
    @Override
    default void onRetry(ForestRequest request, ForestResponse response) {
    }

    /**
     * 默认文件上传或下载监听传输进度时调用该方法
     * <p>默认为什么都不做
     *
     * @param progress Forest进度对象
     */
    @Override
    default void onProgress(ForestProgress progress) {
    }

    /**
     * 默认回调函数:在请求重定向时触发
     * <p>默认为什么都不做
     *
     * @param redirectReq 进行重定向的新请求
     * @param prevReq 上一个请求
     * @param prevRes 上一个请求的响应
     */
    @Override
    default void onRedirection(ForestRequest<?> redirectReq, ForestRequest<?> prevReq, ForestResponse<?> prevRes) {
    }

    /**
     * 默认回调函数: 在发送请求加载Cookie时调用该方法
     * <p>默认为什么都不做
     *
     * @param request Forest请求对象
     * @param cookies Cookie集合, 需要通过请求发送的Cookie都添加到该集合
     */
    @Override
    default void onLoadCookie(ForestRequest request, ForestCookies cookies) {
    }

    /**
     * 默认回调函数: 在请求响应成功后，需要保存Cookie时调用该方法
     * <p>默认为什么都不做
     *
     * @param request Forest请求对象
     * @param cookies Cookie集合，通过响应返回的Cookie都从该集合获取
     */
    @Override
    default void onSaveCookie(ForestRequest request, ForestCookies cookies) {
    }

    /**
     * 获取请求在本拦截器中的 Attribute 属性
     *
     * @param request Forest请求对象
     * @return {@link InterceptorAttributes} 对象实例
     */
    default InterceptorAttributes getAttributes(ForestRequest request) {
        return request.getInterceptorAttributes(this.getClass());
    }

    /**
     * 添加请求在本拦截器中的 Attribute 属性
     *
     * @param request Forest请求对象
     * @param name 属性名称
     * @param value 属性值
     */
    default void addAttribute(ForestRequest request, String name, Object value) {
        request.addInterceptorAttribute(this.getClass(), name, value);
    }

    /**
     * 获取请求在本拦截器中的 Attribute 属性
     *
     * @param request Forest请求对象
     * @param name 属性名称
     * @return 属性值
     */
    default Object getAttribute(ForestRequest request, String name) {
        return request.getInterceptorAttribute(this.getClass(), name);
    }

    /**
     * 按自义定类型获取请求在本拦截器中的 Attribute 属性
     *
     * @param request Forest请求对象
     * @param name 属性名称
     * @param clazz 属性值的类型对象
     * @param <R> 属性值类型的泛型
     * @return Attribute 属性值
     */
    default <R> R getAttribute(ForestRequest request, String name, Class<R> clazz) {
        Object obj = request.getInterceptorAttribute(this.getClass(), name);
        if (obj == null) {
            return null;
        }
        return clazz.cast(obj);
    }

    /**
     * 按字符串类型获取请求在本拦截器中的 Attribute 属性
     *
     * @param request Forest请求对象
     * @param name 属性名称
     * @return 字符串类型属性值
     */
    default String getAttributeAsString(ForestRequest request, String name) {
        Object attr = getAttribute(request, name);
        if (attr == null) {
            return null;
        }
        return String.valueOf(attr);
    }

    /**
     * 按整数类型获取请求在本拦截器中的 Attribute 属性
     *
     * @param request Forest请求对象
     * @param name 属性名称
     * @return 整数类型属性值
     */
    default Integer getAttributeAsInteger(ForestRequest request, String name) {
        Object attr = getAttribute(request, name);
        if (attr == null) {
            return null;
        }
        return (Integer) attr;
    }

    /**
     * 按单精度浮点数类型获取请求在本拦截器中的 Attribute 属性
     *
     * @param request Forest请求对象
     * @param name 属性名称
     * @return 单精度浮点数类型属性值
     */
    default Float getAttributeAsFloat(ForestRequest request, String name) {
        Object attr = getAttribute(request, name);
        if (attr == null) {
            return null;
        }
        return (Float) attr;
    }

    /**
     * 按双精度浮点数类型获取请求在本拦截器中的 Attribute 属性
     *
     * @param request Forest请求对象
     * @param name 属性名称
     * @return 双精度浮点数类型属性值
     */
    default Double getAttributeAsDouble(ForestRequest request, String name) {
        Object attr = getAttribute(request, name);
        if (attr == null) {
            return null;
        }
        return (Double) attr;
    }

    /**
     * 获取或添加请求在本拦截器中的 Attribute 属性
     * <p>当 Attribute 属性中不存在属性名称所对应的值，则添加属性值</p>
     * 
     * @param request Forest请求对象
     * @param name  属性名称
     * @param supplier 属性值回调函数
     * @return 属性值
     * @param <R> 属性值类型
     * @since 1.6.1
     */
    default <R> R getOrAddAttribute(ForestRequest request, String name, Supplier<R> supplier) {
        Object obj = getAttribute(request, name);
        if (obj == null && supplier != null) {
            obj = supplier.get();
            addAttribute(request, name, obj);
        }
        return (R) obj;
    }

}
