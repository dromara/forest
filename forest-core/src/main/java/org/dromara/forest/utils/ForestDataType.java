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

package org.dromara.forest.utils;

import org.dromara.forest.exceptions.ForestRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 数据类型封装类型
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-05-30
 */
public class ForestDataType {

    /**
     * 数据类型表
     * <p>所有在Forest中创建的数据类型对象都会放入这个哈希表中</p>
     *
     * @since 1.5.0-BETA4
     */
    public final static Map<String, ForestDataType> DATA_TYPES = new HashMap<>();

    /** 数据类型： 自动类型 */
    public final static ForestDataType AUTO = ForestDataType.createDataType("auto", null);

    /** 数据类型： 文本类型 */
    public final static ForestDataType TEXT = ForestDataType.createDataType("text", false);

    /** 数据类型： 表单类型 */
    public final static ForestDataType FORM = ForestDataType.createDataType("form", true);

    /** 数据类型： JSON类型 */
    public final static ForestDataType JSON = ForestDataType.createDataType("json", true);

    /** 数据类型： XML类型 */
    public final static ForestDataType XML = ForestDataType.createDataType("xml", false);

    /** 数据类型： 二进制类型 */
    public final static ForestDataType BINARY = ForestDataType.createDataType("binary", false);

    /** 数据类型： 文件类型 */
    public final static ForestDataType MULTIPART = ForestDataType.createDataType("multipart", true);


    /** 数据类型： Protobuf类型 */
    public final static ForestDataType PROTOBUF = ForestDataType.createDataType("protobuf", true);

    /** 数据类型名称 */
    private final String name;

    private final Boolean hasNameValue;

    /**
     * 创建新的数据类型
     *
     * @param name Data type name
     * @param hasNameValue 是否有键值对
     * @return New instance of {@link  ForestDataType}
     * @since 1.5.0-BETA4
     */
    public static ForestDataType createDataType(String name, Boolean hasNameValue) {
        if (StringUtil.isEmpty(name)) {
            throw new ForestRuntimeException("Data type name cannot be empty!");
        }
        name = name.toLowerCase();
        ForestDataType dataType = new ForestDataType(name, hasNameValue);
        if (DATA_TYPES.containsKey(name)) {
            throw new ForestRuntimeException("Data type '" + name + "' has already been existed!" );
        }
        DATA_TYPES.put(name, dataType);
        return dataType;
    }

    /**
     * 数据类型构造函数
     * <p>该构造函数为私有方法，外部代码不能直接通过new ForestDataType(name)进行创建数据类型对象</p>
     * <p>需要通过静态方法ForestDataType.createDataType或ForestDataType.findOrCreateDataType进行创建</p>
     *
     * @param name Date type name
     * @param hasNameValue
     * @since 1.5.0-BETA4
     */
    private ForestDataType(String name, Boolean hasNameValue) {
        this.name = name;
        this.hasNameValue = hasNameValue;
    }

    /**
     * 获取数据类型名称
     *
     * @return Name of this data type
     */
    public String getName() {
        return name;
    }

    /**
     * Find data type object by data type name
     *
     * @param name Data type name
     * @return Instance of {@code com.dtflys.forest.utils.ForestDataType}
     * @since 1.5.0-BETA4
     */
    public static ForestDataType findByName(String name) {
        return DATA_TYPES.get(name.toLowerCase());
    }

    /**
     * Find or create a data type
     *
     * @param name Data type name
     * @return Instance of {@code com.dtflys.forest.utils.ForestDataType}
     * @since 1.5.0-BETA4
     */
    public static ForestDataType findOrCreateDataType(String name) {
        if (StringUtil.isEmpty(name)) {
            return null;
        }
        name = name.toLowerCase();
        ForestDataType dataType = DATA_TYPES.get(name);
        if (dataType == null) {
            dataType = createDataType(name, null);
        }
        return dataType;
    }

    /**
     * 重载equals方法
     * @param o 相比较的对象
     * @return {@code true}：相同对象; {@code false}：不同对象
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ForestDataType)) {
            return false;
        }
        ForestDataType that = (ForestDataType) o;
        return Objects.equals(getName(), that.getName());
    }

    public Boolean hasNameValue() {
        return hasNameValue;
    }

    /**
     * 重载HashCode
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
