package org.dromara.forest.http.body;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.utils.RequestNameValue;

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
     * @param request Forest请求对象
     * @return 请求键值对
     */
    List<RequestNameValue> getNameValueList(ForestRequest request);

}
