package org.dromara.forest.solon.test.address;

import org.dromara.forest.callback.AddressSource;
import org.dromara.forest.http.ForestAddress;
import org.dromara.forest.http.ForestRequest;
import org.noear.solon.annotation.Component;

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
