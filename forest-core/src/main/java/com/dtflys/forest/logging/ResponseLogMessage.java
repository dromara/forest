package com.dtflys.forest.logging;

import com.dtflys.forest.http.ForestResponse;

/**
 * 请求响应日志消息
 * <p>封装了请求响应日志打印所需的所有信息<p/>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-14 17:31
 */
public class ResponseLogMessage {

    /**
     * Forest响应对象
     */
    private final ForestResponse response;

    /**
     * 请求时间
     */
    private final long requestTime;

    /**
     * 响应时间
     */
    private final long responseTime;

    /**
     * 请求响应状态码
     */
    private final int status;

    public ResponseLogMessage(ForestResponse response, long requestTime, long responseTime, int status) {
        this.response = response;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
        this.status = status;
    }

    public ForestResponse getResponse() {
        return response;
    }


    public long getRequestTime() {
        return requestTime;
    }


    public long getResponseTime() {
        return responseTime;
    }

    /**
     * 计算并获取请求耗时
     * @return 请求耗时
     */
    public long getTime() {
        return responseTime - requestTime;
    }


    public int getStatus() {
        return status;
    }

}
