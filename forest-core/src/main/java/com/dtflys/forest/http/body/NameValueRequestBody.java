package com.dtflys.forest.http.body;


import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 键值对类型请求体
 * <p>该请求体对象会包装键值对的名称和值, 会根据请求的ContentType转换成表单项或JSON中的一个字段</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-22 17:30
 */
public class NameValueRequestBody extends ForestRequestBody implements SupportFormUrlEncoded {

    /**
     * 键值对名称
     */
    private String name;

    /**
     * 键值对值
     */
    private Object value;

    /**
     * 请求体项Content-Type
     */
    private String contentType;

    public NameValueRequestBody(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public NameValueRequestBody(String name, String contentType, Object value) {
        this.name = name;
        this.contentType = contentType;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String toFormString() {
        if (name == null && value == null) {
            return "";
        }
        if (value == null) {
            return name;
        }
        if (name == null) {
            return String.valueOf(value);
        }
        return name + "=" + value;
    }

    @Override
    public String toString() {
        return toFormString();
    }

    @Override
    public byte[] getByteArray() {
        return new byte[0];
    }

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.FORM;
    }

    @Override
    public List<RequestNameValue> getNameValueList(ForestConfiguration configuration) {
        List<RequestNameValue> nameValueList = new ArrayList<>(1);
        nameValueList.add(new RequestNameValue(name, value, MappingParameter.TARGET_BODY));
        return nameValueList;
    }
}
