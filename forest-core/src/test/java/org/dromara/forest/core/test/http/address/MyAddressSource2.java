package org.dromara.forest.core.test.http.address;

import org.dromara.forest.callback.AddressSource;
import org.dromara.forest.http.ForestAddress;
import org.dromara.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-17 2:00
 */
public class MyAddressSource2 implements AddressSource {

    @Override
    public ForestAddress getAddress(ForestRequest request) {
        return new ForestAddress(
                "http",
                "localhost",
                (Integer) request.variableValue("port"),
                "/base/path");
    }
}
