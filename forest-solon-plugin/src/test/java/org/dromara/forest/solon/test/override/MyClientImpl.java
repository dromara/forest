package org.dromara.forest.solon.test.override;

import org.noear.solon.annotation.Component;

@Component
public class MyClientImpl implements MyClient {
    @Override
    public String test1() {
        return "xxxx";
    }
}
