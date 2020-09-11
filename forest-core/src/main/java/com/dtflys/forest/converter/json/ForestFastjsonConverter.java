package com.dtflys.forest.converter.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
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

    private static Field nameField;

    private static Method nameMethod;

    static {
         Class clazz = FieldInfo.class;
        try {
            nameField = clazz.getField("name");
        } catch (NoSuchFieldException e) {
            try {
                nameMethod = clazz.getMethod("getName", new Class[0]);
            } catch (NoSuchMethodException ex) {
            }
        }
    }


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

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        try {
            return JSON.parseObject(source, targetType);
        } catch (Throwable th) {
            throw new ForestConvertException("json", th);
        }
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            return JSON.parseObject(source, targetType);
        } catch (Throwable th) {
            throw new ForestConvertException("json", th);
        }

    }

    public <T> T convertToJavaObject(String source, TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(source, typeReference);
        } catch (Throwable th) {
            throw new ForestConvertException("json", th);
        }

    }

    @Override
    public String encodeToString(Object obj) {
        if (obj instanceof CharSequence) {
            obj.toString();
        }
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
    public Map<String, Object> convertObjectToMap(Object obj) {
        if (nameField == null && nameMethod == null) {
            return defaultJsonMap(obj);
        }
        List<FieldInfo> getters = TypeUtils.computeGetters(obj.getClass(), null);
        JSONObject json = new JSONObject(getters.size(), true);
        try {
            for (FieldInfo field : getters) {
                Object value = field.get(obj);
                Object jsonValue = JSON.toJSON(value);
                if (nameField != null) {
                    json.put((String) nameField.get(field), jsonValue);
                } else if (nameMethod != null) {
                    json.put((String) nameMethod.invoke(field), jsonValue);
                }
            }
            return json;
        } catch (IllegalAccessException e) {
            return defaultJsonMap(obj);
        } catch (InvocationTargetException e) {
            return defaultJsonMap(obj);
        }
    }

    public Map<String, Object> defaultJsonMap(Object obj) {
        Object jsonObj = JSON.toJSON(obj);
        return (Map<String, Object>) jsonObj;
    }

}
