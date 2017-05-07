package org.forest.client;

import org.forest.annotation.DataParam;
import org.forest.annotation.Request;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author gongjun
 * @since 2016-06-12
 */
public interface DataClient {

    @Request(
            url = "http://ditu.amap.com/service/regeo",
            dataType = "json"
    )
    Map getLocation(@DataParam("longitude") BigDecimal longitude, @DataParam("latitude") BigDecimal latitude);

}
