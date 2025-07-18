package com.dtflys.forest.http.body;


import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.reflection.ForestVariable;
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
    private ForestVariable value;

    /**
     * 请求体项Content-Type
     */
    private String contentType;

    public NameValueRequestBody(String name, ForestVariable value) {
        this.name = name;
        this.value = value;
    }

    public NameValueRequestBody(String name, String contentType, ForestVariable value) {
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

    public Object getOriginalValue() {
        return value;
    }

    public boolean canGetValue() {
        if (body == null) {
            return false;
        }
        return ForestVariable.canGetValue(value, body.getRequest());
    }

    public Object getValue() {
        if (value == null) {
            return getDefaultValue();
        }
        if (body == null) {
            return value;
        }
        final ForestRequest request = body.getRequest();
        if (value instanceof Lazy) {
            if (Lazy.isEvaluatingLazyValue(value, request)) {
                return null;
            }
            final Object evaluatedValue = ((Lazy<?>) value).eval(request);
            if (evaluatedValue == null) {
                return getDefaultValue();
            }
            return evaluatedValue;
        }
        return ForestVariable.getValue(value, request);
    }

    public void setValue(Object value) {
        this.value = ForestVariable.create(value);
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
    public List<RequestNameValue> getNameValueList(ForestRequest request) {
        final List<RequestNameValue> nameValueList = new ArrayList<>(1);
        if (ForestVariable.canGetValue(value, request)) {
            nameValueList.add(new RequestNameValue(name, ForestVariable.getValue(value, request), MappingParameter.TARGET_BODY));
        }
        return nameValueList;
    }

    @Override
    public NameValueRequestBody clone() {
        final NameValueRequestBody newBody = new NameValueRequestBody(name, contentType, value);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }

}
