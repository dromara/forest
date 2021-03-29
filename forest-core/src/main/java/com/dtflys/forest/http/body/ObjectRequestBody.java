package com.dtflys.forest.http.body;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.mapping.MappingParameter;
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

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return String.valueOf(object);
    }

    @Override
    public byte[] getByteArray() {
        if (object instanceof byte[]) {
            return (byte[]) object;
        }
        try {
            if (object instanceof InputStream) {
                return IOUtils.toByteArray((InputStream) object);
            }
            if (object instanceof File) {
                return FileUtils.readFileToByteArray((File) object);
            }
            if (object instanceof Reader) {
                return IOUtils.toByteArray((Reader) object);
            }
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
        return toString().getBytes();
    }

    @Override
    public List<RequestNameValue> getNameValueList(ForestConfiguration configuration) {
        List<RequestNameValue> nameValueList = new LinkedList<>();
        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
        Map<String, Object> map = jsonConverter.convertObjectToMap(object);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            nameValueList.add(new RequestNameValue(entry.getKey(), entry.getValue(), MappingParameter.TARGET_BODY));
        }
        return nameValueList;
    }
}
