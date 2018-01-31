package org.forest.test;

import org.forest.annotation.DataVariable;
import org.forest.test.model.Coordinate;
import org.forest.annotation.Request;
import org.forest.annotation.DataObject;
import org.forest.annotation.DataParam;
import org.forest.test.model.Location;
import org.forest.test.model.Result;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 地图服务客户端接口
 * @author gongjun
 * @since 2016-06-01
 */
public interface MapClient {

    @Request(
        url = "http://ditu.amap.com/service/regeo",
        dataType = "json"
    )
    Map getLocation(@DataParam("longitude") BigDecimal longitude, @DataParam("latitude") BigDecimal latitude);


    @Request(
        url = "http://ditu.amap.com/service/regeo",
        dataType = "json"
    )
    Map getLocation(@DataObject Coordinate coordinate);

    @Request(
            url = "http://ditu.amap.com/service/regeo",
            dataType = "json"
    )
    Result<Location> getLocationWithJavaObject(@DataObject Coordinate coordinate);


    @Request(
        url = "http://ditu.amap.com/service/regeo",
        dataType = "json",
        data = {
            "longitude=${coord.longitude}",
            "latitude=${coord.latitude}"
        }
    )
    Map getLocationByCoordinate(@DataVariable("coord") Coordinate coordinate);


}
