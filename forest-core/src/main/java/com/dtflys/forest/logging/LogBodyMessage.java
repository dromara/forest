package com.dtflys.forest.logging;


/**
 * 请求体日志消息接口
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-14 17:10
 */
public interface LogBodyMessage {

    /**
     * 获取请求体日志信息
     * @return 请求体日志字符串
     */
    String getBodyString();
}
