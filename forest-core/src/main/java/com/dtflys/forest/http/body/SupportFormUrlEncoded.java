package com.dtflys.forest.http.body;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.utils.RequestNameValue;

import java.util.List;

/**
 * 支持x-www-form-urlencoded的请求体接口
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since v1.5.0
 */
public interface SupportFormUrlEncoded {

    /**
     * 获取请求键值对
     *
     * @param configuration Forest配置对象
     * @return 请求键值对
     */
    List<RequestNameValue> getNameValueList(ForestConfiguration configuration);

}
