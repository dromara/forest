package com.dtflys.spring.test.component;

import org.springframework.stereotype.Component;

@Component
public class ComponentA {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
