package com.dtflys.forest.converter;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.Lazy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Forest 转换器转换选项
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.29
 */
public class ConvertOptions {

    /**
     * Null值策略
     */
    enum NullValuePolicy {
        /**
         * 忽略为 Null 的值
         */
        IGNORE,

        /**
         * 写入 "null" 字符串
         */
        WRITE_NULL_STRING,

        /**
         * 写入空字符串
         */
        WRITE_EMPTY_STRING
    }

    private NullValuePolicy nullValuePolicy;

    private Set<String> excludedFieldNames = new HashSet<>();

    /**
     * 是否立刻求值延迟参数
     */
    private boolean evaluateLazyValue = true;

    public static ConvertOptions defaultOptions() {
        return new ConvertOptions().nullValuePolicy(NullValuePolicy.IGNORE);
    }

    public ConvertOptions exclude(String... fieldNames) {
        for (String fieldName : fieldNames) {
            excludedFieldNames.add(fieldName);
        }
        return this;
    }

    public ConvertOptions exclude(Collection<String> fieldNames) {
        this.excludedFieldNames.addAll(fieldNames);
        return this;
    }

    public boolean isEvaluateLazyValue() {
        return evaluateLazyValue;
    }

    public ConvertOptions evaluateLazyValue(boolean evaluateLazyValue) {
        this.evaluateLazyValue = evaluateLazyValue;
        return this;
    }

    public Set<String> excludedFieldNames() {
        return excludedFieldNames;
    }

    public ConvertOptions nullValuePolicy(NullValuePolicy nullValuePolicy) {
        this.nullValuePolicy = nullValuePolicy;
        return this;
    }

    public NullValuePolicy nullValuePolicy() {
        return nullValuePolicy;
    }

    public boolean shouldExclude(String fieldName) {
        return excludedFieldNames.contains(fieldName);
    }

    public boolean shouldIgnore(Object value) {
        return value == null && NullValuePolicy.IGNORE.equals(nullValuePolicy);
    }

    public Object getValue(Object value, ForestRequest request) {
        if (value != null) {
            if (request != null && value instanceof Lazy) {
                Object result = null;
                if (evaluateLazyValue) {
                    result = ((Lazy<?>) value).eval(request);
                } else {
                    return value;
                }
                if (result != null) {
                    return result;
                }
            } else {
                return value;
            }
        }
        return NullValuePolicy.WRITE_NULL_STRING.equals(nullValuePolicy) ? "null" :
                NullValuePolicy.WRITE_EMPTY_STRING.equals(nullValuePolicy) ? "" : null;
    }

}
