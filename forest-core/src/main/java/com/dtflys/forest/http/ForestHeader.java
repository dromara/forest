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

import com.dtflys.forest.Forest;

/**
 * Forest请求头接口
 * <p>通过该接口可获取单个Forest请求头的信息</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-08-11 12:45
 */
public interface ForestHeader<SELF extends ForestHeader, VALUE> {

    /**
     * 标准头字段：接受的内容类型
     */
    String ACCEPT = "Accept";

    /**
     * 标准头字段：接受的字符编码
     */
    String ACCEPT_CHARSET = "Accept-Charset";

    /**
     * 标准头字段：接受的版本时间
     */
    String ACCEPT_ENCODING = "Accept-Encoding";

    /**
     * 标准头字段：接受的语言
     */
    String ACCEPT_LANGUAGE = "Accept-Language";

    /**
     * 标准头字段：HTTP身份验证的凭证
     */
    String AUTHORIZATION = "Authorization";

    /**
     * 标准头字段：请求响应链上所有的缓存机制必须遵守的指令
     */
    String CACHE_CONTROL = "Cache-Control";

    /**
     * 标准头字段：请求体的字节长度
     */
    String CONTENT_LENGTH = "Content-Length";

    /**
     * 标准头字段：基于MD5算法对请求体内容进行Base64二进制编码
     */
    String CONTENT_MD5 = "Content-MD5";

    /**
     * 标准头字段：请求体的MIME类型
     */
    String CONTENT_TYPE = "Content-Type";

    /**
     * 非标准头字段：数据使用的编码类型
     */
    String CONTENT_ENCODING = "Content-Encoding";


    /**
     * 标准头字段：HTTP请求携带的COOKIE信息
     */
    String COOKIE = "Cookie";

    /**
     * 标准头字段：消息发送的日期和时间
     */
    String DATE = "Cookie";

    /**
     * 标准头字段：标识客户端需要的特殊浏览器行为
     */
    String EXPECT = "Expect";

    /**
     * 标准头字段：披露客户端通过http代理连接web服务的源信息
     */
    String FORWARDED = "Forwarded";

    /**
     * 标准头字段：设置发送请求的用户的email地址
     */
    String FROM = "From";

    /**
     * 标准头字段：服务器域名和TCP端口号
     */
    String HOST = "Host";

    /**
     * 标准头字段：客户端的ETag
     * 当时客户端ETag和服务器生成的ETag一致才执行，适用于更新自从上次更新之后没有改变的资源
     */
    String IF_MATCH = "If-Match";

    /**
     * 标准头字段：更新时间
     * 从更新时间到服务端接受请求这段时间内如果资源没有改变，允许服务端返回304 Not
     */
    String IF_MODIFIED_SINCE = "If-Modified-Since";

    /**
     * 标准头字段：客户端的ETag
     * 如果和服务端接受请求生成的ETage相同，允许服务端返回304 Not Modified
     */
    String IF_NONE_MATCH = "If-None-Match";

    /**
     * 标准头字段：客户端的ETag
     * 如果和服务端接受请求生成的ETage相同，返回缺失的实体部分；否则返回整个新的实体
     */
    String IF_RANGE = "If-Range";

    /**
     * 标准头字段：限制代理或网关转发消息的次数
     */
    String MAX_FORWARDS = "Max-Forwards";

    /**
     * 标准头字段：标识跨域资源请求
     * 请求服务端设置Access-Control-Allow-Origin响应字段
     */
    String ORIGIN = "Origin";

    /**
     * 标准头字段：设置特殊实现字段，可能会对请求响应链有多种影响
     */
    String PRAGMA = "Pragma";

    /**
     * 标准头字段：为连接代理授权认证信息
     */
    String PROXY_AUTHORIZATION = "Proxy-Authorization";

    /**
     * 标准头字段：请求部分实体，设置请求实体的字节数范围
     */
    String RANGE = "Range";

    /**
     * 标准头字段：设置前一个页面的地址，并且前一个页面中的连接指向当前请求
     */
    String REFERER = "Referer";

    /**
     * 标准头字段：用户代理期望接受的传输编码格式，和响应头中的Transfer-Encoding字段一样
     */
    String TE = "TE";

    /**
     * 标准头字段：请求服务端升级协议
     */
    String Upgrade = "Upgrade";

    /**
     * 标准头字段：用户代理字符串
     */
    String USER_AGENT = "User-Agent";

    /**
     * 标准头字段：通知服务器代理请求
     */
    String VIA = "Via";

    /**
     * 标准头字段：实体可能会发生的问题的通用警告
     */
    String WARNING = "Warning";

    /**
     * 标准头字段：重定向地址
     */
    String LOCATION = "Location";

    /**
     * 默认 Forest 请求的 User-Agent 值
     */
    String DEFAULT_USER_AGENT_VALUE = "forest/" + Forest.VERSION;

    /**
     * 获取请求头的名称
     *
     * @return 请求头的名称
     */
    String getName();


    /**
     * 获取请求头的值
     *
     * @return 请求头的值
     */
    String getValue();

    /**
     * 设置请求头的值
     *
     * @param value 求头的值
     * @return 请求头对象自身
     */
    SELF setValue(VALUE value);


    SELF clone(ForestHeaderMap headerMap);

}
