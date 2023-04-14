package org.dromara.forest.test.http.client;

import org.dromara.forest.annotation.DataObject;
import org.dromara.forest.annotation.DataParam;
import org.dromara.forest.annotation.DataVariable;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.converter.json.ForestJacksonConverter;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.test.model.AmapCross;
import org.dromara.forest.test.model.AmapLocation;
import org.dromara.forest.test.model.Coordinate;
import org.dromara.forest.test.model.Location;
import org.dromara.forest.test.model.Result;
import org.dromara.forest.test.model.SubCoordinate;

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
    Map getLocation(@DataParam("longitude") String longitude, @DataParam("latitude") String latitude);


    @Request(
        url = "https://ditu.amap.com/service/regeo",
        retryCount = 3,
        dataType = "json"
    )
    Map getLocation(@DataObject Coordinate coordinate);

    @Request(
            url = "https://ditu.amap.com/service/regeo",
            retryCount = 3,
            dataType = "json"
    )
    Map getLocation(@DataObject SubCoordinate coordinate);

    @Request(
            url = "https://ditu.amap.com/service/regeo",
            retryCount = 3,
            decoder = ForestJacksonConverter.class
    )
    Map getLocationWithDecoder(@DataObject SubCoordinate coordinate);

    @Request(
            url = "https://ditu.amap.com/service/regeo",
            dataType = "text",
            retryCount = 3,
            decoder = ForestJacksonConverter.class
    )
    Map getLocationWithDecoder2(@DataObject SubCoordinate coordinate);


    @Request(
            url = "https://ditu.amap.com/service/regeo",
            dataType = "json",
            retryCount = 3
    )
    Result<Location> getLocationWithJavaObject(@DataObject Coordinate coordinate);

    @Request(
            url = "https://ditu.amap.com/service/regeo",
            dataType = "json",
            retryCount = 3
    )
    Result<AmapLocation<AmapCross>> getLocationWithJavaObject2(@DataObject Coordinate coordinate);


    @Request(
            url = "https://ditu.amap.com/service/regeo",
            dataType = "json",
            retryCount = 3
    )
    ForestResponse<Result<AmapLocation<AmapLocation.AmapCross>>> getLocationWithJavaObject3(@DataObject Coordinate coordinate);

    @Request(
        url = "https://ditu.amap.com/service/regeo",
        dataType = "json",
        retryCount = 3,
        data = {
            "longitude=${coord.longitude}",
            "latitude=${coord.latitude}"
        }
    )
    Map getLocationByCoordinate(@DataVariable("coord") Coordinate coordinate);


}
