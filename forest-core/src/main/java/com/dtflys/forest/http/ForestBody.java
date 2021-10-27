package com.dtflys.forest.http;

import com.dtflys.forest.http.body.NameValueRequestBody;
import com.dtflys.forest.utils.Validations;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ForestBody implements List<ForestRequestBody> {

    /**
     * 请求体类型
     */
    private ForestBodyType bodyType;

    /**
     * 请求体项列表
     * <p>
     * 该字段为列表类型，列表每一项为请求体项,
     * 都为 {@link ForestRequestBody} 子类的对象实例
     */
    private List<ForestRequestBody> bodyItems = new LinkedList<>();

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


    public ForestBodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(ForestBodyType bodyType) {
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
