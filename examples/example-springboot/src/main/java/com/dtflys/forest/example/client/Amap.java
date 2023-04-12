package com.dtflys.forest.example.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.DataObject;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.example.model.Coordinate;
import com.dtflys.forest.example.model.Location;
import com.dtflys.forest.example.model.Result;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;

/**
 * 高德地图服务客户端接口
 * @author gongjun
 */
@BaseRequest(baseURL = "http://ditu.amap.com")
public interface Amap {

    /**
     * 根据经纬度获取详细地址
     * @param longitude 经度
     * @param latitude 纬度
     * @return
     */
    @Get("http://ditu.amap.com/service/regeo?longitude={lng}&latitude={lat}")
    Result<Location> getLocation(@Var("lng") String longitude, @Var("lat") String latitude);

    /**
     * 根据经纬度获取详细地址
     * @param coordinate 经纬度对象
     * @return
     */
    @Get(url = "/service/regeo")
    Map getLocation(@Query Coordinate coordinate);


    /**
     * 根据经纬度获取详细地址
     * @param coordinate 经纬度对象
     * @return
     */
    @Get(
        url = "/service/regeo",
        data = {
            "longitude=${coord.longitude}",
            "latitude=${coord.latitude}"
        }
    )
    Map getLocationByCoordinate(@DataVariable("coord") Coordinate coordinate);


}
