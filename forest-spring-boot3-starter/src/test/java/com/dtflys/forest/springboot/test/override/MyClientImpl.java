package com.dtflys.forest.springboot.test.override;

import com.dtflys.forest.http.ForestResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyClientImpl implements MyClient {
    @Override
    public String test1() {
        return "xxxx";
    }
}
