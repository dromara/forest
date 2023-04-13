package org.dromara.forest.spring.test.component;

import org.springframework.stereotype.Component;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2022-02-19 18:08
 */
@Component("componentA")
public class ComponentA {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
