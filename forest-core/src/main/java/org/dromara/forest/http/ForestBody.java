package org.dromara.forest.http;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.converter.json.ForestJsonConverter;
import org.dromara.forest.exceptions.ForestUnsupportException;
import org.dromara.forest.http.body.BinaryBodyItem;
import org.dromara.forest.http.body.ByteArrayBodyItem;
import org.dromara.forest.http.body.FileBodyItem;
import org.dromara.forest.http.body.InputStreamBodyItem;
import org.dromara.forest.http.body.MultipartBodyItem;
import org.dromara.forest.http.body.NameValueBodyItem;
import org.dromara.forest.http.body.ObjectBodyItem;
import org.dromara.forest.http.body.StringBodyItem;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.StringUtil;
import org.dromara.forest.utils.Validations;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

public class ForestBody implements List<ForestBodyItem> {

    private final ForestRequest request;


    private ForestDataType defaultBodyType = ForestDataType.BINARY;

    /**
     * 请求体类型
     */
    private ForestDataType bodyType;

    /**
     * 反序列化器
     */
    private ForestEncoder encoder;


    /**
     * 请求体子项列表
     * <p>
     * 该字段为列表类型，列表每一项为请求体项,
     * 都为 {@link ForestBodyItem} 子类的对象实例
     */
    private List<ForestBodyItem> bodyItems = new LinkedList<>();

    public ForestRequest getRequest() {
        return request;
    }

    public ForestBody(ForestRequest request) {
        this.request = request;
    }

    public ForestEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(ForestEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * 根据名称获取键值对类型请求体项
     *
     * @param name 请求体项的名称
     * @return 键值对类型请求体项，{@link NameValueBodyItem}对象实例
     */
    public NameValueBodyItem getNameValueBody(String name) {
        Validations.assertParamNotEmpty(name, "name");
        for (ForestBodyItem body : bodyItems) {
            if (body instanceof NameValueBodyItem
                    && name.equals(((NameValueBodyItem) body).getName())) {
                return (NameValueBodyItem) body;
            }
        }
        return null;
    }


    /**
     * 根据名称获取键值对类型请求体项列表
     *
     * @param name 请求体项的名称
     * @return 键值对类型请求体项列表，即{@link NameValueBodyItem}对象实例列表
     */
    public List<NameValueBodyItem> getNameValueBodies(String name) {
        List<NameValueBodyItem> bodies = new LinkedList<>();
        Validations.assertParamNotEmpty(name, "name");
        for (ForestBodyItem body : bodyItems) {
            if (body instanceof NameValueBodyItem
                    && name.equals(((NameValueBodyItem) body).getName())) {
                bodies.add((NameValueBodyItem) body);
            }
        }
        return bodies;
    }

    /**
     * 获取请求体中的键值对
     * <p>仅包含{@link NameValueBodyItem}类型请求体项数据
     *
     * @return 键值对类型请求体项组成的 {@link Map}
     */
    public Map<String, Object> nameValuesMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (ForestBodyItem body : bodyItems) {
            if (body instanceof NameValueBodyItem) {
                NameValueBodyItem nameValueRequestBody = (NameValueBodyItem) body;
                String name = nameValueRequestBody.getName();
                if (!map.containsKey(name)) {
                    map.put(name, nameValueRequestBody.getValue());
                }
            }
        }
        return map;
    }

    /**
     * 获取请求体中的键值对
     * <p>包含{@link NameValueBodyItem}类型请求体项、以及{@link ObjectBodyItem}类请求体项拆解出来的键值对数据
     *
     * @return 请求体中的键值对集合 - {@link Map} 对象
     */
    public Map<String, Object> nameValuesMapWithObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        for (ForestBodyItem body : bodyItems) {
            if (body instanceof NameValueBodyItem) {
                NameValueBodyItem nameValueRequestBody = (NameValueBodyItem) body;
                String name = nameValueRequestBody.getName();
                if (!map.containsKey(name)) {
                    map.put(name, nameValueRequestBody.getValue());
                }
            } else if (body instanceof ObjectBodyItem) {
                ObjectBodyItem objectRequestBody = (ObjectBodyItem) body;
                Map<String, Object> keyValueMap = jsonConverter.convertObjectToMap(
                        objectRequestBody.getObject(), request);
                for (Map.Entry<String, Object> entry : keyValueMap.entrySet()) {
                    String name = entry.getKey();
                    Object value = entry.getValue();
                    if (!map.containsKey(name)) {
                        map.put(name, value);
                    }
                }
            }
        }
        return map;
    }

    public  <T extends ForestBodyItem> List<T> getItems(Class<T> bodyItemClass) {
        List<T> items = new LinkedList<>();
        for (ForestBodyItem item : bodyItems) {
            Class itemClass = item.getClass();
            if (bodyItemClass.isAssignableFrom(itemClass)) {
                items.add((T) item);
            }
        }
        return items;
    }


    public List<StringBodyItem> getStringItems() {
        return getItems(StringBodyItem.class);
    }

    public List<NameValueBodyItem> getNameValueItems() {
        return getItems(NameValueBodyItem.class);
    }

    public List<ObjectBodyItem> getObjectItems() {
        return getItems(ObjectBodyItem.class);
    }

    public List<ByteArrayBodyItem> getByteArrayItems() {
        return getItems(ByteArrayBodyItem.class);
    }

    public List<InputStreamBodyItem> getInputStreamItems() {
        return getItems(InputStreamBodyItem.class);
    }

    public List<BinaryBodyItem> getBinaryItems() {
        return getItems(BinaryBodyItem.class);
    }

    public List<FileBodyItem> getFileItems() {
        return getItems(FileBodyItem.class);
    }

    public List<MultipartBodyItem> getMultipartItems() {
        return getItems(MultipartBodyItem.class);
    }



    public ForestDataType getDefaultBodyType() {
        return defaultBodyType;
    }

    public ForestDataType getBodyType() {
        return bodyType;
    }

    public void setBodyType(ForestDataType bodyType) {
        this.bodyType = bodyType;
    }

    @Override
    public int size() {
        return bodyItems.size();
    }

    @Override
    public boolean isEmpty() {
        return bodyItems.isEmpty();
    }

    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        for (ForestBodyItem body : bodyItems) {
            if (body instanceof NameValueBodyItem) {
                if (key.equals(((NameValueBodyItem) body).getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        }
        for (ForestBodyItem body : bodyItems) {
            if (body instanceof NameValueBodyItem) {
                if (value.equals(((NameValueBodyItem) body).getValue())) {
                    return true;
                }
            }
            if (body instanceof ObjectBodyItem) {
                if (value.equals(((ObjectBodyItem) body).getObject())) {
                    return true;
                }
            }
            if (body instanceof StringBodyItem) {
                if (value.equals(((StringBodyItem) body).getContent())) {
                    return true;
                }
            }
            if (byte[].class.isAssignableFrom(value.getClass())) {
                byte[] bytes = body.getByteArray();
                if (Objects.equals(bytes, bytes)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Object setNameValue(String key, Object value) {
        if (key == null) {
            return null;
        }
        NameValueBodyItem body = getNameValueBody(key);
        Object oldValue = null;
        if (key != null) {
            oldValue = body.getValue();
            body.setValue(value);
        }
        return oldValue;
    }


    private ForestEncoder selectEncoder(ForestDataType type) {
        if (ForestDataType.MULTIPART == type) {
            throw new ForestUnsupportException("Forest encoder do not support the body type \"MULTIPART\"");
        }
        ForestDataType dataType = type == null ? this.request.mineContentType().bodyType() : type;
        ForestEncoder forestEncoder = (ForestEncoder) this.request.getConfiguration().getConverter(dataType);
        if (forestEncoder == null) {
            forestEncoder = (ForestEncoder) this.request.getConfiguration().getConverter(ForestDataType.TEXT);
        }
        return forestEncoder;
    }


    private ForestEncoder selectEncoder() {
        return selectEncoder(this.bodyType);
    }

    public byte[] encode(ForestEncoder encoder, Charset charset, ConvertOptions options) {
        ConvertOptions opts = options != null ? options : ConvertOptions.defaultOptions();
        return encoder.encodeRequestBody(this, charset, opts);
    }

    public byte[] encode(ForestEncoder encoder, Charset charset) {
        return encode(encoder, charset, ConvertOptions.defaultOptions());
    }

    public byte[] encode(ForestDataType type, Charset charset, ConvertOptions options) {
        return encode(selectEncoder(type), charset, options);
    }

    public byte[] encode(ForestDataType type, Charset charset) {
        return encode(type, charset, ConvertOptions.defaultOptions());
    }

    public byte[] encode(ForestDataType type, ConvertOptions options) {
        return encode(type, StandardCharsets.UTF_8, options);
    }

    public byte[] encode(ForestDataType type) {
        return encode(type, StandardCharsets.UTF_8);
    }


    public byte[] encode(Charset charset, ConvertOptions options) {
        if (this.encoder != null) {
            return encode(this.encoder, charset, options);
        }
        return encode(selectEncoder(), charset, options);
    }

    public byte[] encode(Charset charset) {
        return encode(charset, ConvertOptions.defaultOptions());
    }

    public byte[] encode(ConvertOptions options) {
        return encode(StandardCharsets.UTF_8, options);
    }

    public byte[] encode() {
        return encode(ConvertOptions.defaultOptions());
    }


    public String encodeToString(ForestEncoder encoder, Charset charset, ConvertOptions options) {
        byte[] bytes = this.encode(encoder, charset, options);
        return new String(bytes);
    }

    public String encodeToString(ForestEncoder encoder, Charset charset) {
        return encodeToString(encoder, charset, ConvertOptions.defaultOptions());
    }

    public String encodeToString(ForestDataType type, Charset charset, ConvertOptions options) {
        return encodeToString(selectEncoder(type), charset, options);
    }

    public String encodeToString(ForestDataType type, Charset charset) {
        return encodeToString(type, charset, ConvertOptions.defaultOptions());
    }

    public String encodeToString(ForestDataType type, ConvertOptions options) {
        return encodeToString(type, StandardCharsets.UTF_8, options);
    }
    public String encodeToString(ForestDataType type) {
        return encodeToString(type, ConvertOptions.defaultOptions());
    }


    public String encodeToString(Charset charset, ConvertOptions options) {
        if (this.encoder != null) {
            return encodeToString(this.encoder, charset, options);
        }
        return encodeToString(selectEncoder(), charset, options);
    }

    public String encodeToString(Charset charset) {
        return encodeToString(charset, ConvertOptions.defaultOptions());
    }

    public String encodeToString(ConvertOptions options) {
        String strCharset = request.getCharset();
        if (StringUtil.isEmpty(strCharset)) {
            strCharset = request.getConfiguration().getCharset();
        }
        if (StringUtil.isEmpty(strCharset)) {
            strCharset = "UTF-8";
        }
        return encodeToString(Charset.forName(strCharset), options);
    }

    public String encodeToString() {
        return encodeToString(ConvertOptions.defaultOptions());
    }


    @Override
    public boolean contains(Object o) {
        return bodyItems.contains(o);
    }

    @Override
    public Iterator<ForestBodyItem> iterator() {
        return bodyItems.iterator();
    }

    @Override
    public Object[] toArray() {
        return bodyItems.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return bodyItems.toArray(a);
    }

    @Override
    public boolean add(ForestBodyItem forestRequestBody) {
        if (bodyType == null) {
            defaultBodyType = forestRequestBody.getDefaultBodyType();
        }
        forestRequestBody.setBody(this);
        return bodyItems.add(forestRequestBody);
    }

    public void replaceNameValue(NameValueBodyItem nameValueRequestBody) {
        NameValueBodyItem body = getNameValueBody(nameValueRequestBody.getName());
        if (body != null) {
            body.setValue(nameValueRequestBody.getValue());
            body.setValue(nameValueRequestBody.getDefaultValue());
            body.setContentType(nameValueRequestBody.getContentType());
        }
    }

    @Override
    public boolean remove(Object o) {
        return bodyItems.remove(o);
    }

    public <T extends ForestBodyItem> boolean remove(Class<T> bodyItemClass) {
        List<Boolean> rets = new LinkedList<>();
        for (ForestBodyItem item : bodyItems) {
            Class itemClass = item.getClass();
            if (bodyItemClass.isAssignableFrom(itemClass)) {
                rets.add(bodyItems.remove(item));
            }
        }
        if (rets.isEmpty()) {
            return false;
        }
        return rets.stream().allMatch(ret -> ret);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return bodyItems.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ForestBodyItem> c) {
        for (ForestBodyItem item : c) {
            item.setBody(this);
        }
        return bodyItems.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends ForestBodyItem> c) {
        for (ForestBodyItem item : c) {
            item.setBody(this);
        }
        return bodyItems.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return bodyItems.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return bodyItems.retainAll(c);
    }

    @Override
    public void clear() {
        for (ForestBodyItem item : bodyItems) {
            item.setBody(null);
        }
        bodyItems.clear();
    }



    @Override
    public ForestBodyItem get(int index) {
        return bodyItems.get(index);
    }

    @Override
    public ForestBodyItem set(int index, ForestBodyItem element) {
        return bodyItems.set(index, element);
    }

    @Override
    public void add(int index, ForestBodyItem element) {
        element.setBody(this);
        bodyItems.add(index, element);
    }

    @Override
    public ForestBodyItem remove(int index) {
        ForestBodyItem item = bodyItems.remove(index);
        if (item != null) {
            item.setBody(null);
        }
        return item;
    }

    @Override
    public int indexOf(Object o) {
        return bodyItems.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return bodyItems.lastIndexOf(o);
    }

    @Override
    public ListIterator<ForestBodyItem> listIterator() {
        return bodyItems.listIterator();
    }

    @Override
    public ListIterator<ForestBodyItem> listIterator(int index) {
        return bodyItems.listIterator();
    }

    @Override
    public List<ForestBodyItem> subList(int fromIndex, int toIndex) {
        return bodyItems.subList(fromIndex, toIndex);
    }

    @Override
    public ForestBody clone() {
        return clone(request);
    }

    public ForestBody clone(ForestRequest request) {
        ForestBody newBody = new ForestBody(request);
        for (ForestBodyItem item : bodyItems) {
            newBody.add(item.clone());
        }
        return newBody;
    }

}
