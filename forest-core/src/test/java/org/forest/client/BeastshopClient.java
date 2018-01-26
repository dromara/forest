package org.forest.client;

import org.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 19:02
 */
public interface BeastshopClient {

    @Request(
            url = "http://www.thebeastshop.com",
            timeout = 80000,
            logEnable = false
    )
    public String index();
}
