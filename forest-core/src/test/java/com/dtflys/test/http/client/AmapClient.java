package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.model.*;
import com.dtflys.test.model.Coordinate;
import com.dtflys.test.model.Result;

import java.util.Map;

/**
 * 地图服务客户端接口
 * @author gongjun
 * @since 2016-06-01
 */
public interface AmapClient {

    @Request(
        url = "https://ditu.amap.com/service/regeo",
        retryCount = 3,
        dataType = "json"
    )
    Map getLocation(@Query("longitude") String longitude, @Query("latitude") String latitude);


    @Request(
        url = "https://ditu.amap.com/service/regeo",
        retryCount = 3,
        dataType = "json"
    )
    Map getLocation(@Query Coordinate coordinate);

    @Request(
            url = "https://ditu.amap.com/service/regeo",
            retryCount = 3,
            dataType = "json"
    )
    Map getLocation(@Query SubCoordinate coordinate);

    @Request(
            url = "https://ditu.amap.com/service/regeo",
            retryCount = 3,
            decoder = ForestJacksonConverter.class
    )
    Map getLocationWithDecoder(@Query SubCoordinate coordinate);

    @Request(
            url = "https://ditu.amap.com/service/regeo",
            dataType = "text",
            retryCount = 3,
            decoder = ForestJacksonConverter.class
    )
    Map getLocationWithDecoder2(@Query SubCoordinate coordinate);


    @Request(
            url = "https://ditu.amap.com/service/regeo",
            dataType = "json",
            retryCount = 3
    )
    Result<Location> getLocationWithJavaObject(@Query Coordinate coordinate);

    @Request(
            url = "https://ditu.amap.com/service/regeo",
            dataType = "json",
            retryCount = 3
    )
    Result<AmapLocation<AmapCross>> getLocationWithJavaObject2(@Query Coordinate coordinate);


    @Request(
            url = "https://ditu.amap.com/service/regeo",
            dataType = "json",
            retryCount = 3
    )
    ForestResponse<Result<AmapLocation<AmapLocation.AmapCross>>> getLocationWithJavaObject3(@Query Coordinate coordinate);

    @Request(
        url = "https://ditu.amap.com/service/regeo",
        dataType = "json",
        retryCount = 3,
        data = {
            "longitude=${coord.longitude}",
            "latitude=${coord.latitude}"
        }
    )
    Map getLocationByCoordinate(@Query("coord") Coordinate coordinate);


}
