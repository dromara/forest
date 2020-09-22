package com.dtflys.forest.http;

/**
 * Forest请求体
 * <p>该类为Forest请求中所有类型请求体项的父类<p/>
 * <p>该类有3个子类：</p>
 * <p>    StringRequestBody    字符串请求体</p>
 * <p>    ObjectRequestBody    对象请求体</p>
 * <p>    NameValueRequestBody 键值对请求体</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-22 17:30
 */
public abstract class ForestRequestBody {

    /**
     * 请求体类型枚举类
     */
    public enum BodyType {

        /**
         * 字符串请求体类型
         */
        STRING,

        /**
         * 键值对请求体类型
         */
        NAME_VALUE,

        /**
         * 对象请求体类型
         */
        OBJECT,
    }

    /**
     * 请求体类型
     */
    protected final BodyType type;

    public ForestRequestBody(BodyType type) {
        this.type = type;
    }

    /**
     * 获取请求体类型
     * @return
     */
    public BodyType getType() {
        return type;
    }

}
