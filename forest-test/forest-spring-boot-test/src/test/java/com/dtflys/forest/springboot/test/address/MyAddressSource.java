package com.dtflys.forest.springboot.test.address;

import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestRequest;
import org.springframework.stereotype.Component;

@Component
public class MyAddressSource implements AddressSource {

    private int port;

    @Override
    public ForestAddress getAddress(ForestRequest req) {
        return new ForestAddress("https", "127.0.0.1", port);
    }

    public void setPort(int port) {
        this.port = port;
    }
}
