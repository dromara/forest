package org.dromara.forest.springboot3.test.override;

import org.springframework.stereotype.Component;

@Component
public class MyClientImpl implements MyClient {
    @Override
    public String test1() {
        return "xxxx";
    }
}
