package com.dtflys.forest.http;

/**
 * 请求体类型枚举类
 */
public enum ForestBodyType {

    /**
     * 字符串请求体类型
     */
    TEXT,

    /**
     * 表单格式
     */
    FORM,

    /**
     * JSON格式
     */
    JSON,

    /**
     * XML格式
     */
    XML,

    /**
     * Protobuf格式
     */
    PROTOBUF,

    /**
     * 二进制格式
     */
    BINARY,

    /**
     * 文件类型
     */
    FILE,

}
