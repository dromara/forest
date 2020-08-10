package com.dtflys.forest.mapping;

import com.dtflys.forest.filter.Filter;
import com.dtflys.forest.filter.FilterChain;

/**
 * Created by Administrator on 2016/5/17.
 */
public class MappingParameter {

    protected Integer index;

    protected String name;

    private Boolean query;

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

    public Boolean getQuery() {
        return query;
    }

    public void setQuery(Boolean query) {
        this.query = query;
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
