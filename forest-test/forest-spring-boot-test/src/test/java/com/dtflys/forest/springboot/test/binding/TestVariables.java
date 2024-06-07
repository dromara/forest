package com.dtflys.forest.springboot.test.binding;

import com.dtflys.forest.annotation.BindingVar;
import org.springframework.stereotype.Component;

@BindingVar
@Component
public class TestVariables {

    public String getTestName() {
        return "test";
    }
}
