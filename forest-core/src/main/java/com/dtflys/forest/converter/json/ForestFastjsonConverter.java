package com.dtflys.forest.converter.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


/**
 * 使用Fastjson实现的消息转换实现类
 * @author gongjun
 * @since 2016-05-30
 */
public class ForestFastjsonConverter implements ForestJsonConverter {

    /**
     * Fastjson序列化方式
     */
    private String serializerFeatureName = "DisableCircularReferenceDetect";

    private SerializerFeature serializerFeature;


    public String getSerializerFeatureName() {
        return serializerFeatureName;
    }

    public void setSerializerFeatureName(String serializerFeatureName) {
        this.serializerFeatureName = serializerFeatureName;
        SerializerFeature feature = SerializerFeature.valueOf(serializerFeatureName);
        setSerializerFeature(feature);
    }

    public SerializerFeature getSerializerFeature() {
        return serializerFeature;
    }

    public ForestFastjsonConverter() {
        setSerializerFeature(SerializerFeature.valueOf(serializerFeatureName));
    }

    public void setSerializerFeature(SerializerFeature serializerFeature) {
        this.serializerFeature = serializerFeature;
        if (serializerFeature == null) {
            this.serializerFeatureName = null;
        }
        else {
            this.serializerFeatureName = serializerFeature.name();
        }
    }

    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        try {
            return JSON.parseObject(source, targetType);
        } catch (Throwable th) {
            throw new ForestRuntimeException(th);
        }
    }

    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return JSON.parseObject(source, targetType);
        } catch (Throwable th) {
            throw new ForestRuntimeException(th);
        }

    }

    public <T> T convertToJavaObject(String source, TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(source, typeReference);
        } catch (Throwable th) {
            throw new ForestRuntimeException(th);
        }

    }



    public String convertToJson(Object obj) {
        try {
            if (serializerFeature == null) {
                return JSON.toJSONString(obj);
            }
            return JSON.toJSONString(obj, serializerFeature);
        } catch (Throwable th) {
            throw new ForestRuntimeException(th);
        }
    }

    @Override
    public Map convertToJsonMap(Object obj) {
        if (obj instanceof Map) {
            return (Map) obj;
        }
        if (obj instanceof List) {
            throw new ForestConvertException("can not convert " + obj.getClass().getName() + " to " + Map.class.getName());
        }
        return (Map) JSON.toJSON(obj);
    }

}
