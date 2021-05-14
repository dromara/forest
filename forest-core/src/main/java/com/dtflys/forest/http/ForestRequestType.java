package com.dtflys.forest.http;

import com.dtflys.forest.utils.StringUtils;

import static com.dtflys.forest.mapping.MappingParameter.TARGET_BODY;
import static com.dtflys.forest.mapping.MappingParameter.TARGET_QUERY;

/**
 * Forest请求类型（请求方法）枚举
 * <p>包括： GET POST PUT PATCH HEAD OPTIONS DELETE TRACE</p>
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.4.0
 */
public enum ForestRequestType {

    /**
     * GET请求方法
     */
    GET("GET", TARGET_QUERY),

    /**
     * POST请求方法
     */
    POST("POST", TARGET_BODY),

    /**
     * PUT请求方法
     */
    PUT("PUT", TARGET_BODY),

    /**
     * PATCH请求方法
     */
    PATCH("PATCH", TARGET_BODY),

    /**
     * HEAD请求方法
     */
    HEAD("HEAD", TARGET_QUERY),

    /**
     * OPTIONS请求方法
     */
    OPTIONS("OPTIONS", TARGET_QUERY),

    /**
     * DELETE请求方法
     */
    DELETE("DELETE", TARGET_QUERY),

    /**
     * TREACE请求方法
     */
    TRACE("TRACE", TARGET_QUERY),
    ;

    /**
     * 请求类型名称（HTTP方法名称）
     */
    private final String name;

    /**
     * 参数默认在请求中的位置（URL Query参数/请求体）
     * 默认值为{@link com.dtflys.forest.mapping.MappingParameter#TARGET_UNKNOWN}
     *
     * @see com.dtflys.forest.mapping.MappingParameter#TARGET_UNKNOWN
     * @see com.dtflys.forest.mapping.MappingParameter#TARGET_QUERY
     * @see com.dtflys.forest.mapping.MappingParameter#TARGET_BODY
     */
    private final int defaultParamTarget;

    /**
     * Forest请求类型（请求方法）枚举构造方法
     *
     * @param name 请求类型名称（HTTP方法名称）
     * @param defaultParamTarget 参数默认在请求中的位置（URL Query参数/请求体）
     */
    ForestRequestType(String name, int defaultParamTarget) {
        this.name = name;
        this.defaultParamTarget = defaultParamTarget;
    }

    public String getName() {
        return name;
    }

    public int getDefaultParamTarget() {
        return defaultParamTarget;
    }

    /**
     * 判断本Forest请求类型枚举是否匹配传入的名称
     *
     * @param name 请求类型名称（HTTP方法名称）字符串
     * @return {@code true}: 匹配; {@code false}: 不匹配
     */
    public boolean match(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return this.name.equals(name.toUpperCase());
    }

    /**
     * 此类型请求是否一定需要Body
     *
     * @return {@code true}: 需要, 否则不需要
     */
    public boolean isNeedBody() {
        return !this.equals(GET);
    }

    /**
     * 根据请求类型名称（HTTP方法名称）找到对应的Forest请求类型枚举
     *
     * @param name 请求类型名称（HTTP方法名称）
     * @return 对应的Forest请求类型 {@link ForestRequestType}枚举实例
     */
    public static ForestRequestType findType(String name) {
        for (ForestRequestType type : ForestRequestType.values()) {
            if (type.match(name)) {
                return type;
            }
        }
        return null;
    }

}
