package com.dtflys.forest.core.test.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Administrator on 2016/6/20.
 */
public class Contact {

    @JSONField(ordinal = 0)
    private String name;

    @JSONField(ordinal = 2)
    private Integer age;

    @JSONField(ordinal = 1)
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
