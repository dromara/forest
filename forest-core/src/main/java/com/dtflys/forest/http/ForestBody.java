package com.dtflys.forest.http;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.http.body.ByteArrayRequestBody;
import com.dtflys.forest.http.body.FileRequestBody;
import com.dtflys.forest.http.body.InputStreamRequestBody;
import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;
import com.dtflys.forest.http.body.StringRequestBody;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.Validations;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

public class ForestBody implements List<ForestRequestBody> {

    private final ForestConfiguration configuration;

    private ForestDataType defaultBodyType = ForestDataType.BINARY;

    /**
     * 请求体类型
     */
    private ForestDataType bodyType;

    /**
     * 请求体项列表
     * <p>
     * 该字段为列表类型，列表每一项为请求体项,
     * 都为 {@link ForestRequestBody} 子类的对象实例
     */
    private List<ForestRequestBody> bodyItems = new LinkedList<>();

    public ForestBody(ForestConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * 根据名称获取键值对类型请求体项
     *
     * @param name 请求体项的名称
     * @return 键值对类型请求体项，{@link NameValueRequestBody}对象实例
     */
    public NameValueRequestBody getNameValueBody(String name) {
        Validations.assertParamNotEmpty(name, "name");
        for (ForestRequestBody body : bodyItems) {
            if (body instanceof NameValueRequestBody
                    && name.equals(((NameValueRequestBody) body).getName())) {
                return (NameValueRequestBody) body;
            }
        }
        return null;
    }

    /**
     * 根据名称获取键值对类型请求体项列表
     *
     * @param name 请求体项的名称
     * @return 键值对类型请求体项列表，即{@link NameValueRequestBody}对象实例列表
     */
    public List<NameValueRequestBody> getNameValueBodies(String name) {
        List<NameValueRequestBody> bodies = new LinkedList<>();
        Validations.assertParamNotEmpty(name, "name");
        for (ForestRequestBody body : bodyItems) {
            if (body instanceof NameValueRequestBody
                    && name.equals(((NameValueRequestBody) body).getName())) {
                bodies.add((NameValueRequestBody) body);
            }
        }
        return bodies;
    }

    /**
     * 获取请求体中的键值对
     * <p>仅包含{@link NameValueRequestBody}类型请求体项数据
     *
     * @return 键值对类型请求体项组成的 {@link Map}
     */
    public Map<String, Object> nameValuesMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (ForestRequestBody body : bodyItems) {
            if (body instanceof NameValueRequestBody) {
                NameValueRequestBody nameValueRequestBody = (NameValueRequestBody) body;
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
     * <p>包含{@link NameValueRequestBody}类型请求体项、以及{@link ObjectRequestBody}类请求体项拆解出来的键值对数据
     *
     * @return 请求体中的键值对集合 - {@link Map} 对象
     */
    public Map<String, Object> nameValuesMapWithObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        ForestJsonConverter jsonConverter = configuration.getJsonConverter();
        for (ForestRequestBody body : bodyItems) {
            if (body instanceof NameValueRequestBody) {
                NameValueRequestBody nameValueRequestBody = (NameValueRequestBody) body;
                String name = nameValueRequestBody.getName();
                if (!map.containsKey(name)) {
                    map.put(name, nameValueRequestBody.getValue());
                }
            } else if (body instanceof ObjectRequestBody) {
                ObjectRequestBody objectRequestBody = (ObjectRequestBody) body;
                Map<String, Object> keyValueMap = jsonConverter.convertObjectToMap(objectRequestBody.getObject());
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

    public  <T extends ForestRequestBody> List<T> getItems(Class<T> bodyItemClass) {
        List<T> items = new LinkedList<>();
        for (ForestRequestBody item : bodyItems) {
            Class itemClass = item.getClass();
            if (bodyItemClass.isAssignableFrom(itemClass)) {
                items.add((T) item);
            }
        }
        return items;
    }

    public List<StringRequestBody> getStringItems() {
        return getItems(StringRequestBody.class);
    }

    public List<NameValueRequestBody> getNameValueItems() {
        return getItems(NameValueRequestBody.class);
    }

    public List<ObjectRequestBody> getObjectItems() {
        return getItems(ObjectRequestBody.class);
    }

    public List<ByteArrayRequestBody> getByteArrayItems() {
        return getItems(ByteArrayRequestBody.class);
    }

    public List<InputStreamRequestBody> getInputStreamItems() {
        return getItems(InputStreamRequestBody.class);
    }

    public List<FileRequestBody> getFileItems() {
        return getItems(FileRequestBody.class);
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
        for (ForestRequestBody body : bodyItems) {
            if (body instanceof NameValueRequestBody) {
                if (key.equals(((NameValueRequestBody) body).getName())) {
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
        for (ForestRequestBody body : bodyItems) {
            if (body instanceof NameValueRequestBody) {
                if (value.equals(((NameValueRequestBody) body).getValue())) {
                    return true;
                }
            }
            if (body instanceof ObjectRequestBody) {
                if (value.equals(((ObjectRequestBody) body).getObject())) {
                    return true;
                }
            }
            if (body instanceof StringRequestBody) {
                if (value.equals(((StringRequestBody) body).getContent())) {
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
        NameValueRequestBody body = getNameValueBody(key);
        Object oldValue = null;
        if (key != null) {
            oldValue = body.getValue();
            body.setValue(value);
        }
        return oldValue;
    }

    @Override
    public boolean contains(Object o) {
        return bodyItems.contains(o);
    }

    @Override
    public Iterator<ForestRequestBody> iterator() {
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
    public boolean add(ForestRequestBody forestRequestBody) {
        if (bodyType == null) {
            defaultBodyType = forestRequestBody.getDefaultBodyType();
        }
        return bodyItems.add(forestRequestBody);
    }

    @Override
    public boolean remove(Object o) {
        return bodyItems.remove(o);
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        return bodyItems.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ForestRequestBody> c) {
        return bodyItems.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends ForestRequestBody> c) {
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
        bodyItems.clear();
    }



    @Override
    public ForestRequestBody get(int index) {
        return bodyItems.get(index);
    }

    @Override
    public ForestRequestBody set(int index, ForestRequestBody element) {
        return bodyItems.set(index, element);
    }

    @Override
    public void add(int index, ForestRequestBody element) {
        bodyItems.add(index, element);
    }

    @Override
    public ForestRequestBody remove(int index) {
        return bodyItems.remove(index);
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
    public ListIterator<ForestRequestBody> listIterator() {
        return bodyItems.listIterator();
    }

    @Override
    public ListIterator<ForestRequestBody> listIterator(int index) {
        return bodyItems.listIterator();
    }

    @Override
    public List<ForestRequestBody> subList(int fromIndex, int toIndex) {
        return bodyItems.subList(fromIndex, toIndex);
    }
}
