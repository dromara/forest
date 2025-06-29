package com.dtflys.forest.test.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestInterceptor;
import com.dtflys.forest.interceptor.ResponseResult;
import com.dtflys.forest.interceptor.ResponseSuccess;
import com.dtflys.forest.reflection.ForestMethod;

public class AddQueryInterceptor implements ForestInterceptor {

    /**
     * 默认回调函数: 接受到请求响应时调用该方法
     * <p>默认返回未知状态，继续执行后续逻辑
     *
     * @param request Forest请求对象
     * @param response Forest响应对象
     * @return 请求响应结果: {@link ResponseSuccess} 或 {@link ResponseSuccess} 实例
     */
    @Override
    public ResponseResult onResponse(ForestRequest request, ForestResponse response) {
        if (response.isError()) {
            // return error(); 返回错误标识，会进入 onError() 方法
            // return error("错误!"); 返回错误，并带上错误消息字符串
            return error(response.getException()); // 返回错误，并带上响应的异常信息 
        }
        
        // request: Forest请求对象
        // response: Forest响应对象
        // response.getResult(): 获取反序列化后的响应结果
        // response.get(数据类型.class): 读取响应流并反序列化为参数对应的数据类型，该方法一次请求只能调用一次
        
        return proceed(); // 继续执行后续环节
    }

    /**
     * 默认回调函数: 接口方法执行时调用该方法
     * <p>默认为什么都不做
     *
     * @param request Forest请求对象
     * @param method Forest方法对象
     * @param args 方法调用入参数组
     */
    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        ForestInterceptor.super.onInvokeMethod(request, method, args);
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }


    @Override
    public boolean beforeExecute(ForestRequest request) {
        Integer port = (Integer) request.getConfiguration().getVariableValue("port");
        request.setUrl("http://localhost:" + port + "/hello/user?username=foo");
        request.addQuery("password", "bar");
        return true;
    }
}
