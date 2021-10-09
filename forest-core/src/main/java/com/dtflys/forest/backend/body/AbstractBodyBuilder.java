package com.dtflys.forest.backend.body;

import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestBodyType;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.ByteArrayRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;
import com.dtflys.forest.http.body.StringRequestBody;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import com.google.protobuf.Message;

import java.lang.reflect.Array;
import java.util.*;


/**
 * 通用的请求体构造器抽象类
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:06
 */
public abstract class AbstractBodyBuilder<T> implements BodyBuilder<T> {

    /**
     * 构建请求体
     * @param httpRequest 后端http请求对象
     * @param request Forest请求对象
     * @param lifeCycleHandler 生命周期处理器
     */
    @Override
    public void buildBody(T httpRequest, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        String contentType = request.getContentType();

        if (StringUtils.isEmpty(contentType)) {
            contentType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
        }

        String[] typeGroup = contentType.split(";[ ]*charset=");
        String mineType = typeGroup[0];
        String charset = request.getCharset();
        boolean mergeCharset = false;
        if (StringUtils.isEmpty(charset)) {
            if (typeGroup.length > 1) {
                charset = typeGroup[1];
                mergeCharset = true;
            } else {
                charset = "UTF-8";
            }
        }

        if (StringUtils.isEmpty(mineType)) {
            mineType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
        }
        List<RequestNameValue> nameValueList = request.getDataNameValueList();

        ContentType mineContentType = new ContentType(mineType);

        List<ForestRequestBody> reqBody = request.getBody();
        boolean needRequestBody = request.getType().isNeedBody() ||
                !reqBody.isEmpty() ||
                !request.getMultiparts().isEmpty();

        if (needRequestBody) {
            ForestBodyType bodyType = request.bodyType();
            if (bodyType == null) {
                bodyType = mineContentType.bodyType();
            }
            if (bodyType == ForestBodyType.FORM) {
                setFormBody(httpRequest, request, charset, contentType, reqBody);
            } else if(bodyType == ForestBodyType.PROTOBUF) {
                Object[] arguments = request.getArguments();
                if (arguments != null) {
                    Optional<Object> first = Arrays.stream(arguments).filter(e -> Message.class.isAssignableFrom(e.getClass())).findFirst();
                    if (first.isPresent()) {
                        setProtobuf(httpRequest, request, charset, contentType, nameValueList, first.get());
                    }
                }
            } else if (bodyType == ForestBodyType.JSON) {
                ForestConverter encoder = request.getEncoder();
                ForestConverter converter = request.getConfiguration().getJsonConverter();
                ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
                if (encoder != null) {
                    converter = encoder;
                }
                List<ForestRequestBody> srcBodyList = request.getBody();
                List<ForestRequestBody> bodyList = new LinkedList(srcBodyList);
                if (!bodyList.isEmpty()) {
                    Object toJsonObj = bodyList;
                    if (bodyList.size() == 1) {
                        toJsonObj = bodyList.get(0);
                    } else {
                        Map<String, Object> jsonMap = null;
                        List jsonArray = null;
                        for (ForestRequestBody bodyItem : bodyList) {
                            if (bodyItem instanceof NameValueRequestBody) {
                                if (jsonMap == null) {
                                    jsonMap = new LinkedHashMap<>(bodyList.size());
                                }
                                jsonMap.put(((NameValueRequestBody) bodyItem).getName(), ((NameValueRequestBody) bodyItem).getValue());
                            } else if (bodyItem instanceof StringRequestBody) {
                                String content = bodyItem.toString();
                                Map subMap = null;
                                try {
                                    if (converter instanceof ForestJsonConverter) {
                                        subMap = ((ForestJsonConverter) converter).convertObjectToMap(content);
                                    } else {
                                        subMap = jsonConverter.convertObjectToMap(content);
                                    }
                                } catch (Throwable th) {
                                }
                                if (subMap != null) {
                                    if (jsonMap == null) {
                                        jsonMap = new LinkedHashMap<>(bodyList.size());
                                    }
                                    jsonMap.putAll(subMap);
                                } else {
                                    if (jsonArray == null) {
                                        jsonArray = new LinkedList<>();
                                    }
                                    jsonArray.add(content);
                                }
                            } else if (bodyItem instanceof ObjectRequestBody) {
                                Object obj = ((ObjectRequestBody) bodyItem).getObject();
                                if (obj == null) {
                                    continue;
                                }
                                if (obj instanceof List) {
                                    if (jsonArray == null) {
                                        jsonArray = new LinkedList();
                                    }
                                    jsonArray.addAll((List) obj);
                                } else {
                                    Map subMap = null;
                                    try {
                                        if (converter instanceof ForestJsonConverter) {
                                            subMap = ((ForestJsonConverter) converter).convertObjectToMap(obj);
                                        } else {
                                            subMap = jsonConverter.convertObjectToMap(obj);
                                        }
                                    } catch (Throwable th) {
                                    }
                                    if (subMap == null) {
                                        continue;
                                    }
                                    if (jsonMap == null) {
                                        jsonMap = new LinkedHashMap<>(bodyList.size());
                                    }
                                    jsonMap.putAll(subMap);
                                }
                            }
                        }
                        if (jsonMap != null) {
                            toJsonObj = jsonMap;
                        } else if (jsonArray != null) {
                            toJsonObj = jsonArray;
                        }
                    }
                    String text = null;
                    if (toJsonObj instanceof CharSequence || toJsonObj instanceof StringRequestBody) {
                        text = toJsonObj.toString();
                        setStringBody(httpRequest, text, charset, contentType, mergeCharset);
                    } else if (toJsonObj instanceof ObjectRequestBody) {
                        if (converter instanceof ForestJsonConverter) {
                            text = ((ForestJsonConverter) converter).encodeToString(((ObjectRequestBody) toJsonObj).getObject());
                        } else {
                            text = jsonConverter.encodeToString(((ObjectRequestBody) toJsonObj).getObject());
                        }
                        setStringBody(httpRequest, text, charset, contentType, mergeCharset);
                    } else if (toJsonObj instanceof NameValueRequestBody) {
                        Map<String, Object> subMap = new HashMap<>(1);
                        subMap.put(((NameValueRequestBody) toJsonObj).getName(), ((NameValueRequestBody) toJsonObj).getValue());
                        if (converter instanceof ForestJsonConverter) {
                            text = ((ForestJsonConverter) converter).encodeToString(subMap);
                        } else {
                            text = jsonConverter.encodeToString(subMap);
                        }
                        setStringBody(httpRequest, text, charset, contentType, mergeCharset);
                    } else if (toJsonObj instanceof ByteArrayRequestBody) {
                        byte[] bytes = ((ByteArrayRequestBody) toJsonObj).getByteArray();
                        setBinaryBody(httpRequest, request, charset, contentType, nameValueList, bytes, lifeCycleHandler);
                    } else {
                        if (converter instanceof ForestJsonConverter) {
                            text = ((ForestJsonConverter) converter).encodeToString(toJsonObj);
                        } else {
                            text = jsonConverter.encodeToString(toJsonObj);
                        }
                        setStringBody(httpRequest, text, charset, contentType, mergeCharset);
                    }
                } else {
                    setStringBody(httpRequest, "", charset, contentType, mergeCharset);
                }
            } else if (bodyType == ForestBodyType.FILE) {
                List<ForestMultipart> multiparts = request.getMultiparts();
                setFileBody(httpRequest, request, charset, contentType, nameValueList, multiparts, lifeCycleHandler);
            } else if (bodyType == ForestBodyType.BINARY) {
                List<ForestMultipart> multiparts = request.getMultiparts();
                List<byte[]> byteList = new LinkedList<>();
                int size = 0;
                for (ForestMultipart multipart : multiparts) {
                    byte[] byteArray = multipart.getBytes();
                    byteList.add(byteArray);
                    size += byteArray.length;
                }
                for (ForestRequestBody body : reqBody) {
                    byte[] byteArray = body.getByteArray();
                    byteList.add(byteArray);
                    size += byteArray.length;
                }
                byte[] bytes = new byte[size];
                int pos = 0;
                for (byte[] bytesItem : byteList) {
                    for (int i = 0; i < bytesItem.length; i++) {
                        bytes[pos + i] = bytesItem[i];
                    }
                    pos += bytesItem.length;
                }
                setBinaryBody(httpRequest, request, charset, contentType, nameValueList, bytes, lifeCycleHandler);
            } else {
                StringBuilder builder = new StringBuilder();
                List bodyList = request.getBody();
                if (!bodyList.isEmpty()) {
                    for (Object bodyItem : bodyList) {
                        builder.append(bodyItem.toString());
                    }
                    setStringBody(httpRequest, builder.toString(), charset, contentType, mergeCharset);
                }
            }
        }
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
                    for (Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
                        Object item = iterator.next();
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

    /**
     * 设置字符串请求体
     * @param httpReq 后端请求对象
     * @param text 字符串文本
     * @param charset 字符集
     * @param contentType 数据类型
     * @param mergeCharset 是否合并字符集
     */
    protected abstract void setStringBody(T httpReq, String text, String charset, String contentType, boolean mergeCharset);

    /**
     * 设置表单请求体
     * @param httpReq 后端请求对象
     * @param request Forest请求对象
     * @param charset 字符集
     * @param contentType 数据类型
     * @param bodyItems 键值对列表
     */
    protected abstract void setFormBody(T httpReq, ForestRequest request, String charset, String contentType, List<ForestRequestBody> bodyItems);

    /**
     * 设置文件请求体
     * @param httpReq 后端请求对象
     * @param request Forest请求对象
     * @param charset 字符集
     * @param contentType 数据类型
     * @param nameValueList 键值对列表
     * @param multiparts Multiparts
     * @param lifeCycleHandler 生命周期处理器
     */
    protected abstract void setFileBody(T httpReq, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList,  List<ForestMultipart> multiparts, LifeCycleHandler lifeCycleHandler);

    /**
     * 设置二进制请求体
     * @param httpReq 后端请求对象
     * @param request Forest请求对象
     * @param charset 字符集
     * @param contentType 数据类型
     * @param nameValueList 键值对列表
     * @param bytes 字节数组
     * @param lifeCycleHandler 生命周期处理器
     */
    protected abstract void setBinaryBody(T httpReq,
                                 ForestRequest request,
                                 String charset,
                                 String contentType,
                                 List<RequestNameValue> nameValueList,
                                 byte[] bytes,
                                 LifeCycleHandler lifeCycleHandler);


    /**
     * @param httpReq       后端请求对象
     * @param request       Forest请求对象
     * @param charset       字符集
     * @param contentType   数据类型
     * @param nameValueList 键值对列表
     * @param source        protobuf 对象
     */
    protected abstract void setProtobuf(T httpReq, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList, Object source);

}
