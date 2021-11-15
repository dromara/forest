package com.dtflys.forest.converter.form;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.SupportFormUrlEncoded;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLEncoder;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultFormConvertor implements ForestConverter<String>, ForestEncoder {

    private final ForestConfiguration configuration;

    public DefaultFormConvertor(ForestConfiguration configuration) {
        this.configuration = configuration;
    }


    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        return null;
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        return null;
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        return null;
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.FORM;
    }

    @Override
    public String encodeToString(Object obj) {
        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
        Map<String, Object> map = jsonConverter.convertObjectToMap(obj);
        List<RequestNameValue> nameValueList = new LinkedList<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            RequestNameValue nameValue = new RequestNameValue(entry.getKey(), MappingParameter.TARGET_BODY);
            nameValue.setValue(entry.getValue());
            nameValueList.add(nameValue);
        }
        nameValueList = processFromNameValueList(nameValueList, configuration);
        return formUrlEncodedString(nameValueList, StandardCharsets.UTF_8);
    }

    /**
     * 处理Form表单中的集合项
     * @param newNameValueList 键值对列表
     * @param configuration Forest配置
     * @param name 表单项目名
     * @param collection 集合对象
     * @param target 请求目标位置
     */
    protected void processFormCollectionItem(List<RequestNameValue> newNameValueList, ForestConfiguration configuration, String name, Collection collection, int target) {
        int index = 0;
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            Object item = iterator.next();
            String subName = name + "[" + index + "]";
            processFormItem(newNameValueList, configuration, subName, item, target);
            index++;
        }
    }

    /**
     * 处理Form表单中的数组项
     * @param newNameValueList 键值对列表
     * @param configuration Forest配置
     * @param name 表单项目名
     * @param array 数组
     * @param target 请求目标位置
     */
    protected void processFormArrayItem(List<RequestNameValue> newNameValueList, ForestConfiguration configuration, String name, Object array, int target) {
        int len = Array.getLength(array);
        for (int i = 0; i < len; i++) {
            Object item = Array.get(array, i);
            String subName = name + "[" + i + "]";
            processFormItem(newNameValueList, configuration, subName, item, target);
        }
    }

    /**
     * 处理Form表单中的Map项
     * @param newNameValueList 键值对列表
     * @param configuration Forest配置
     * @param name 表单项目名
     * @param map Map对象
     * @param target 请求目标位置
     */
    protected void processFormMapItem(List<RequestNameValue> newNameValueList, ForestConfiguration configuration, String name, Map map, int target) {
        for (Iterator<Map.Entry> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry entry = iterator.next();
            Object mapKey = entry.getKey();
            Object mapValue = entry.getValue();
            String subName = name + "[" + mapKey + "]";
            processFormItem(newNameValueList, configuration, subName, mapValue, target);
        }
    }


    /**
     * 处理Form表单中的项
     * @param newNameValueList 键值对列表
     * @param configuration Forest配置
     * @param name 表单项目名
     * @param value 表单项目值
     * @param target 请求目标位置
     */
    protected void processFormItem(List<RequestNameValue> newNameValueList, ForestConfiguration configuration, String name, Object value, int target) {
        if (StringUtils.isEmpty(name) && value == null) {
            return;
        }
        if (value != null) {
            Class itemClass = value.getClass();
            boolean needCollapse = false;
            if (value instanceof Collection) {
                Collection collection = (Collection) value;
                if (collection.size() <= 8) {
                    for (Object item : collection) {
                        if (!ReflectUtils.isPrimaryType(item.getClass())) {
                            needCollapse = true;
                            break;
                        }
                    }
                }
            } else if (itemClass.isArray() && !ReflectUtils.isPrimaryArrayType(itemClass)) {
                needCollapse = true;
            }
            if (needCollapse) {
                if (value instanceof Collection) {
                    processFormCollectionItem(newNameValueList, configuration, name, (Collection) value, target);
                } else if (itemClass.isArray()) {
                    processFormArrayItem(newNameValueList, configuration, name, value, target);
                }
            } else if (ReflectUtils.isPrimaryType(itemClass)
                    || ReflectUtils.isPrimaryArrayType(itemClass)
                    || value instanceof Collection) {
                newNameValueList.add(new RequestNameValue(name, value, target));
            } else if (value instanceof Map) {
                processFormMapItem(newNameValueList, configuration, name, (Map) value, target);
            } else {
                Map<String, Object> itemAttrs = ReflectUtils.convertObjectToMap(value, configuration);
                for (Map.Entry<String, Object> entry : itemAttrs.entrySet()) {
                    String subAttrName = entry.getKey();
                    Object subAttrValue = entry.getValue();
                    String subName = name + "." + subAttrName;
                    processFormItem(newNameValueList, configuration, subName, subAttrValue, target);
                }
            }
        }
    }



    /**
     * 处理Form表单中的键值对列表
     * @param nameValueList 键值对列表
     * @return 处理过的新键值对列表
     */
    protected List<RequestNameValue> processFromNameValueList(List<RequestNameValue> nameValueList, ForestConfiguration configuration) {
        List<RequestNameValue> newNameValueList = new LinkedList<>();
        for (RequestNameValue nameValue : nameValueList) {
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            int target = nameValue.getTarget();
            processFormItem(newNameValueList, configuration,  name, value, target);
        }
        return newNameValueList;
    }

    private String formUrlEncodedString(List<RequestNameValue> nameValueList, Charset charset) {
        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            if (!nameValue.isInBody()) {
                continue;
            }
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            strBuilder.append(name);
            if (value != null) {
                value = MappingTemplate.getFormValueString(jsonConverter, value);
                strBuilder.append("=").append(URLEncoder.FORM_VALUE.encode(String.valueOf(value), charset));
            }
            if (i < nameValueList.size() - 1) {
                strBuilder.append("&");
            }
        }
        return strBuilder.toString();
    }

    @Override
    public byte[] encodeRequestBody(ForestBody body, Charset charset) {
        List<RequestNameValue> nameValueList = new LinkedList<>();
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        for (ForestRequestBody bodyItem : body) {
            if (bodyItem instanceof SupportFormUrlEncoded) {
                nameValueList.addAll(((SupportFormUrlEncoded) bodyItem).getNameValueList(configuration));
            }
        }
        nameValueList = processFromNameValueList(nameValueList, configuration);
        String strBody = formUrlEncodedString(nameValueList, charset);
        byte[] bytes = strBody.getBytes(charset);
        return bytes;
    }
}
