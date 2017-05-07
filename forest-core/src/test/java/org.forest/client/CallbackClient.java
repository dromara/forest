package org.forest.client;

import org.forest.annotation.Request;
import org.forest.annotation.DataParam;
import org.forest.callback.OnSuccess;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author gongjun
 * @since 2016-05-31
 */
public interface CallbackClient {

    @Request(
        url = "http://ditu.amap.com/service/regeo",
        dataType = "json"
    )
    String testOnSuccess(@DataParam("longitude") BigDecimal longitude, @DataParam("latitude") BigDecimal latitude, OnSuccess<Map> onSuccess);

}
