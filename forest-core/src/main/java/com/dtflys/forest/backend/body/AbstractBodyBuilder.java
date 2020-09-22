package com.dtflys.forest.backend.body;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.NameValueRequestBody;
import com.dtflys.forest.http.ObjectRequestBody;
import com.dtflys.forest.http.StringRequestBody;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.multipart.ForestMultipart;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 通用的请求体构造器抽象类
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:06
 */
public abstract class AbstractBodyBuilder<T> implements BodyBuilder<T> {

    public final static String TYPE_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public final static String TYPE_APPLICATION_JSON = "application/json";
    public final static String TYPE_MULTIPART_FORM_DATA = "multipart/form-data";

    @Override
    public void buildBody(T httpRequest, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        String contentType = request.getContentType();
        if (StringUtils.isEmpty(contentType)) {
            String value = request.getHeaders().getValue("Content-Type");
            if (value != null) {
                if (value.length() > 0) {
                    contentType = value;
                }
            }
        }

        if (StringUtils.isEmpty(contentType)) {
            contentType = TYPE_APPLICATION_X_WWW_FORM_URLENCODED;
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
            mineType = TYPE_APPLICATION_X_WWW_FORM_URLENCODED;
        }
        List<RequestNameValue> nameValueList = request.getDataNameValueList();

        if (mineType.equals(TYPE_APPLICATION_X_WWW_FORM_URLENCODED) && !nameValueList.isEmpty()) {
            setFormBody(httpRequest, request, charset, contentType, nameValueList);
        }
        else if (mineType.equals(TYPE_APPLICATION_JSON)) {
            ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
            List<ForestRequestBody> srcBodyList = request.getBody();
            List<ForestRequestBody> bodyList = new LinkedList(srcBodyList);
//            Map<String, Object> map = convertNameValueListToMap(request, nameValueList);
//            if (map != null && !map.isEmpty()) {
//                bodyList.add(map);
//            }
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
                                subMap = jsonConverter.convertObjectToMap(content);
                            } catch (Throwable th) {}
                            if (subMap != null) {
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
                                    subMap = jsonConverter.convertObjectToMap(obj);
                                } catch (Throwable th) {}
                                if (subMap == null) {
                                    continue;
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
                } else if (toJsonObj instanceof ObjectRequestBody) {
                    text = jsonConverter.encodeToString(((ObjectRequestBody) toJsonObj).getObject());
                } else if (toJsonObj instanceof NameValueRequestBody) {
                    Map<String, Object> subMap = new HashMap<>(1);
                    subMap.put(((NameValueRequestBody) toJsonObj).getName(), ((NameValueRequestBody) toJsonObj).getValue());
                    text = jsonConverter.encodeToString(subMap);
                } else {
                    text = jsonConverter.encodeToString(toJsonObj);
                }
                setStringBody(httpRequest, text, charset, contentType, mergeCharset);
            } else {
                setStringBody(httpRequest, "", charset, contentType, mergeCharset);
            }
        }
        else if (mineType.startsWith("multipart/")) {
            List<ForestMultipart> multiparts = request.getMultiparts();
            setFileBody(httpRequest, request, charset, contentType, nameValueList, multiparts, lifeCycleHandler);
        }
        else  {
//            Map<String, Object> map = convertNameValueListToMap(request, nameValueList);
//            StringBuilder builder = new StringBuilder();
//            for (Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
//                Map.Entry<String, Object> entry = iterator.next();
//                Object value = entry.getValue();
//                builder.append(value);
//            }
            StringBuilder builder = new StringBuilder();
            List bodyList = request.getBody();
            for (Object bodyItem : bodyList) {
                builder.append(bodyItem.toString());
            }
            setStringBody(httpRequest, builder.toString(), charset, contentType, mergeCharset);
        }
    }

    protected abstract void setStringBody(T httpReq, String text, String charset, String contentType, boolean mergeCharset);

    protected abstract void setFormBody(T httpReq, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList);

    protected abstract void setFileBody(T httpReq, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList,  List<ForestMultipart> multiparts, LifeCycleHandler lifeCycleHandler);

    private Map<String, Object> convertNameValueListToMap(ForestRequest request, List<RequestNameValue> nameValueList) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            if (value instanceof Date) {
                value = MappingTemplate.getParameterValue(jsonConverter, value);
            }
            if (value == null && StringUtils.isNotEmpty(name)) {
                if (name.charAt(0) == '{') {
                    Map nameMap = jsonConverter.convertToJavaObject(name, Map.class);
                    if (nameMap != null && nameMap.size() > 0) {
                        map.putAll(nameMap);
                    } else {
                        map.put(name, value);
                    }
                }
            }
            else {
                map.put(name, value);
            }
        }
        return map;
    }
}
