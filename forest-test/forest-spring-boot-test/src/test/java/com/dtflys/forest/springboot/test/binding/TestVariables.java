package com.dtflys.forest.springboot.test.binding;

import com.dtflys.forest.annotation.Var;
import org.springframework.stereotype.Component;

@Component
@Var("testVariables")
public class TestVariables {

    public String getTestName() {
        return "test";
    }
}
