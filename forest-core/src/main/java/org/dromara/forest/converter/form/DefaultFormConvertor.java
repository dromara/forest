package org.dromara.forest.converter.form;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.converter.json.ForestJsonConverter;
import org.dromara.forest.http.ForestBody;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.http.Lazy;
import org.dromara.forest.http.body.SupportFormUrlEncoded;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.mapping.MappingTemplate;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.ReflectUtils;
import org.dromara.forest.utils.RequestNameValue;
import org.dromara.forest.utils.StringUtils;
import org.dromara.forest.utils.URLEncoder;

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
    public String encodeToString(final Object obj) {
        final ForestJsonConverter jsonConverter = configuration.getJsonConverter();
        final Map<String, Object> map = jsonConverter.convertObjectToMap(obj);
        final List<RequestNameValue> nameValueList = new LinkedList<>();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            final RequestNameValue nameValue = new RequestNameValue(entry.getKey(), MappingParameter.TARGET_BODY);
            nameValue.setValue(entry.getValue());
            nameValueList.add(nameValue);
        }
        final List<RequestNameValue> newNameValueList = processFromNameValueList(
                null, nameValueList, configuration, ConvertOptions.defaultOptions());
        return formUrlEncodedString(newNameValueList, StandardCharsets.UTF_8);
    }

    /**
     * 处理Form表单中的集合项
     * @param newNameValueList 键值对列表
     * @param configuration Forest配置
     * @param name 表单项目名
     * @param collection 集合对象
     * @param target 请求目标位置
     */
    protected void processFormCollectionItem(final List<RequestNameValue> newNameValueList, final ForestConfiguration configuration, final String name, final Collection collection, final int target) {
        int index = 0;
        for (final Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            final Object item = iterator.next();
            final String subName = name + "[" + index + "]";
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
    protected void processFormArrayItem(final List<RequestNameValue> newNameValueList, final ForestConfiguration configuration, final String name, final Object array, final int target) {
        final int len = Array.getLength(array);
        for (int i = 0; i < len; i++) {
            final Object item = Array.get(array, i);
            final String subName = name + "[" + i + "]";
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
    protected void processFormMapItem(final List<RequestNameValue> newNameValueList, final ForestConfiguration configuration, final String name, final Map<?, ?> map, final int target) {
        map.forEach((mapKey, mapValue) ->
                processFormItem(newNameValueList, configuration, name + "[" + mapKey + "]", mapValue, target));
    }


    /**
     * 处理Form表单中的项
     * @param newNameValueList 键值对列表
     * @param configuration Forest配置
     * @param name 表单项目名
     * @param value 表单项目值
     * @param target 请求目标位置
     */
    protected void processFormItem(final List<RequestNameValue> newNameValueList, final ForestConfiguration configuration, final String name, final Object value, final int target) {
        if (StringUtils.isEmpty(name) && value == null) {
            return;
        }
        if (value != null) {
            final Class itemClass = value.getClass();
            boolean needCollapse = false;
            if (value instanceof Collection) {
                final Collection collection = (Collection) value;
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
                processFormMapItem(newNameValueList, configuration, name, (Map<?, ?>) value, target);
            } else {
                final Map<String, Object> itemAttrs = ReflectUtils.convertObjectToMap(value, configuration);
                for (final Map.Entry<String, Object> entry : itemAttrs.entrySet()) {
                    final String subAttrName = entry.getKey();
                    final Object subAttrValue = entry.getValue();
                    final String subName = name + "." + subAttrName;
                    processFormItem(newNameValueList, configuration, subName, subAttrValue, target);
                }
            }
        }
    }



    /**
     * 处理Form表单中的键值对列表
     *
     * @param request 请求对象
     * @param nameValueList 键值对列表
     * @param configuration Forest 配置对象
     * @param options 转换选项
     * @return 处理过的新键值对列表
     */
    protected List<RequestNameValue> processFromNameValueList(
            final ForestRequest request,
            final List<RequestNameValue> nameValueList,
            final ForestConfiguration configuration,
            final ConvertOptions options) {
        List<RequestNameValue> newNameValueList = new LinkedList<>();
        for (RequestNameValue nameValue : nameValueList) {
            String name = nameValue.getName();
            if (options != null && options.shouldExclude(name)) {
                continue;
            }
            Object value = nameValue.getValue();
            if (Lazy.isEvaluatingLazyValue(value, request)) {
                continue;
            }
            if (options != null) {
                value = options.getValue(value, request);
                if (options.shouldIgnore(value)) {
                    continue;
                }
            }
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
    public byte[] encodeRequestBody(final ForestBody body, final Charset charset, final ConvertOptions options) {
        final List<RequestNameValue> nameValueList = new LinkedList<>();
        final Charset cs = charset != null ? charset : StandardCharsets.UTF_8;
        final ForestRequest request = body.getRequest();
        for (ForestRequestBody bodyItem : body) {
            if (bodyItem instanceof SupportFormUrlEncoded) {
                nameValueList.addAll(((SupportFormUrlEncoded) bodyItem).getNameValueList(request));
            }
        }
        final List<RequestNameValue> newNameValueList =
                processFromNameValueList(request, nameValueList, configuration, options);
        String strBody = formUrlEncodedString(newNameValueList, cs);
        byte[] bytes = strBody.getBytes(cs);
        return bytes;
    }
}
