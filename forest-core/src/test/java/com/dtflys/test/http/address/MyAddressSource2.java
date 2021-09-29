package com.dtflys.test.http.address;

import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-17 2:00
 */
public class MyAddressSource2 implements AddressSource {

    @Override
    public ForestAddress getAddress(ForestRequest request) {
        return new ForestAddress("localhost", (Integer) request.variableValue("port"));
    }
}
