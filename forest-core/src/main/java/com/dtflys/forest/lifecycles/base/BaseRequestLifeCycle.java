package com.dtflys.forest.lifecycles.base;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.lifecycles.BaseAnnotationLifeCycle;
import com.dtflys.forest.proxy.InterfaceProxyHandler;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-23 23:03
 */
public class BaseRequestLifeCycle implements BaseAnnotationLifeCycle<BaseRequest, Object> {

    @Override
    public void onProxyHandlerInitialized(InterfaceProxyHandler interfaceProxyHandler, BaseRequest annotation) {

    }
}
