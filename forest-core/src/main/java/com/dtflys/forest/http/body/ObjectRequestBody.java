package com.dtflys.forest.http.body;

import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.RequestNameValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 对象类型请求体
 * <p>该请求体对象会包装一个Java对象, 其对象最终会在请求发送前被序列化</p>
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-22 17:30
 */
public class ObjectRequestBody extends ForestRequestBody implements SupportFormUrlEncoded {

    private Object object;

    public ObjectRequestBody(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
    
    public <T> T getObject(Class<T> clazz) {
        return clazz.cast(object);
    }


    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return String.valueOf(object);
    }

    @Override
    public byte[] getByteArray() {
        Object obj = object;
        if (obj == null) {
            return new byte[0];
        }
        if (obj instanceof Lazy) {
            obj = ((Lazy<?>) obj).eval(body.getRequest());
        }
        if (obj instanceof byte[]) {
            return (byte[]) object;
        }
        try {
            if (obj instanceof InputStream) {
                return IOUtils.toByteArray((InputStream) object);
            }
            if (obj instanceof File) {
                return FileUtils.readFileToByteArray((File) object);
            }
            if (obj instanceof Reader) {
                return IOUtils.toByteArray((Reader) object);
            }
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
        return toString().getBytes();
    }
    
    public Class getObjectClass() {
        if (object == null) {
            return null;
        }
        return object.getClass();
    }
    
    

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.FORM;
    }

    @Override
    public List<RequestNameValue> getNameValueList(ForestRequest request) {
        List<RequestNameValue> nameValueList = new LinkedList<>();
        if (object == null) {
            return nameValueList;
        }
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        Object obj = object;
        if (obj instanceof Lazy) {
            obj = ((Lazy<?>) obj).eval(body.getRequest());
        }
        Map<String, Object> map = jsonConverter.convertObjectToMap(obj, request);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            nameValueList.add(new RequestNameValue(entry.getKey(), entry.getValue(), MappingParameter.TARGET_BODY));
        }
        return nameValueList;
    }

    @Override
    public ObjectRequestBody clone() {
        ObjectRequestBody newBody = new ObjectRequestBody(object);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
