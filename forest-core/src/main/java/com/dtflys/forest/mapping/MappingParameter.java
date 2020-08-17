package com.dtflys.forest.mapping;

import com.dtflys.forest.filter.Filter;
import com.dtflys.forest.filter.FilterChain;

/**
 * Created by Administrator on 2016/5/17.
 */
public class MappingParameter {

    public final static int TYPE_UNKNOWN = 0;
    public final static int TYPE_QUERY = 1;
    public final static int TYPE_BODY = 2;
    public final static int TYPE_HEADER = 3;

    protected Integer index;

    protected String name;

    protected int type = TYPE_UNKNOWN;

    private boolean objectProperties = false;

    private boolean isJsonParam = false;

    private String jsonParamName;

    private FilterChain filterChain = new FilterChain();

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnknown() {
        return type == TYPE_UNKNOWN;
    }

    public boolean isQuery() {
        return type == TYPE_QUERY;
    }

    public boolean isBody() {
        return type == TYPE_BODY;
    }

    public boolean isHeader() {
        return type == TYPE_HEADER;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isObjectProperties() {
        return objectProperties;
    }

    public void setObjectProperties(boolean objectProperties) {
        this.objectProperties = objectProperties;
    }

    public boolean isJsonParam() {
        return isJsonParam;
    }

    public void setJsonParam(boolean jsonParam) {
        isJsonParam = jsonParam;
    }

    public String getJsonParamName() {
        return jsonParamName;
    }

    public void setJsonParamName(String jsonParamName) {
        this.jsonParamName = jsonParamName;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }

    public void addFilter(Filter filter) {
        filterChain.addFilter(filter);
    }
}
