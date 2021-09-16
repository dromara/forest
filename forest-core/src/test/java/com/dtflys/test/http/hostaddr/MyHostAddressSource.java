package com.dtflys.test.http.hostaddr;

import com.dtflys.forest.callback.HostAddressSource;
import com.dtflys.forest.http.ForestHostAddress;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-17 2:00
 */
public class MyHostAddressSource implements HostAddressSource {

    @Override
    public ForestHostAddress getHostAddress(ForestRequest request) {
        return new ForestHostAddress("1.1.1.1", 88);
    }
}
