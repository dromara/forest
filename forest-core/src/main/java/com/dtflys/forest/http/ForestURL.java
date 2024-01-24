package com.dtflys.forest.http;

import com.dtflys.forest.utils.URLUtils;

import java.net.URI;
import java.net.URL;

public interface ForestURL {
    static ForestURLBuilder builder() {
        return new ForestURLBuilder();
    }

    static int normalizePort(Integer port, boolean ssl) {
        if (URLUtils.isNonePort(port)) {
            return ssl ? 443 : 80;
        }
        return port;
    }

    void needRegenerateUrl();

    /**
     * 获取原始URL
     *
     * @return 原始URL字符串
     */
    String getOriginalUrl();

    /**
     * 设置基础地址信息
     *
     * @param baseAddress {@link ForestAddress}对象
     * @return {@link ForestURL}对象
     */
    ForestURL setBaseAddress(ForestAddress baseAddress);

    String getScheme();

    ForestURL setScheme(String scheme);

    void refreshSSL();

    String getHost();

    ForestURL setHost(String host);

    int getPort();

    ForestURL setPort(int port);

    /**
     * 获取URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @return URL根路径
     */
    String normalizeBasePath();

    String normalizeBasePath(String basePath);

    /**
     * 设置URL根路径 (强制修改)
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @param basePath 根路径
     * @return {@link ForestURL}对象实例
     */
    ForestURL setBasePath(String basePath);

    /**
     * 设置URL根路径
     * <p>该路径为整个URL去除前面协议 + Host + Port 后部分
     *
     * @param basePath 根路径
     * @param forced   是否强制修改, {@code true}: 强制修改非根路径部分地址信息, {@code false}: 非强制，如果URL已设置host、port等非根路径部分地址信息则不会修改
     * @return {@link ForestURL}对象实例
     */
    ForestURL setBasePath(String basePath, boolean forced);

    /**
     * 获取URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     *
     * @return URL路径
     */
    String getPath();

    /**
     * 设置URL路径
     * <p>该路径为整个URL去除前面协议 + Host + Port + BasePath 后部分
     *
     * @param path URL路径
     * @return {@link ForestURL}对象实例
     */
    ForestURL setPath(String path);

    String getUserInfo();

    ForestURL setUserInfo(String userInfo);

    String getAuthority();

    String getRef();

    ForestURL setRef(String ref);

    boolean isSSL();

    String toURLString();

    /**
     * 获取URL对应的路由
     *
     * @return {@link ForestRoute}对象实例
     * @author gongjun [dt_flys@hotmail.com]
     * @since 1.5.22
     */
    ForestRoute getRoute();

    @Override
    String toString();

    URL toJavaURL();

    URI toURI();

    /**
     * 修改地址信息 (强制修改)
     *
     * @param address 地址, {@link ForestAddress}对象实例
     * @return {@link ForestURL}对象实例
     */
    ForestURL setAddress(ForestAddress address);

    /**
     * 修改地址信息
     *
     * @param address 地址, {@link ForestAddress}对象实例
     * @param forced  是否强制修改, {@code true}: 强制修改, {@code false}: 非强制，如果URL已设置host、port等信息则不会修改
     * @return {@link ForestURL}对象实例
     */
    ForestURL setAddress(ForestAddress address, boolean forced);

    /**
     * 合并两个URL
     *
     * @param url 被合并的一个URL
     * @return 合并完的新URL
     */
    ForestURL mergeURLWith(SimpleForestURL url);

    /**
     * 设置基地址URL
     *
     * @param baseURL 基地址URL
     * @return {@link SimpleForestURL}对象实例
     */
    ForestURL setBaseURL(SimpleForestURL baseURL);

    ForestURL mergeAddress();

    ForestURL checkAndComplete();
}
