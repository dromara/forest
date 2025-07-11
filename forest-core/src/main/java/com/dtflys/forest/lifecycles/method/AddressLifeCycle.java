package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

/**
 * 重试注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class AddressLifeCycle implements MethodAnnotationLifeCycle<Address, Void> {


    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final String scheme = getAttributeAsString(request, "scheme");
        final String host = getAttributeAsString(request, "host");
        final String portStr = getAttributeAsString(request, "port");
        final String basePath = getAttributeAsString(request, "basePath");
        final Class addressSourceClass = getAttribute(request, "source", Class.class);

        Integer port = null;

        // 判断是否有设置 port
        if (StringUtils.isNotBlank(portStr)) {
            if (!Character.isDigit(portStr.charAt(0))) {
                throw new ForestRuntimeException("[Forest] property 'port' of annotation @Address must be a number!");
            }
            try {
                port = Integer.parseInt(portStr);
            } catch (Throwable th) {
                throw new ForestRuntimeException("[Forest] property 'port' of annotation @Address must be a number!");
            }
        }

        // 最后判断有无设置回调函数，此项设置会覆盖 host 和 port 以及 scheme 属性的设置
        if (addressSourceClass != null && AddressSource.class.isAssignableFrom(addressSourceClass) && !addressSourceClass.isInterface() && !addressSourceClass.isAnnotation()) {
            final Object addressSourceObj = request.getConfiguration().getForestObject(addressSourceClass);
            final AddressSource addressSource = (AddressSource) addressSourceObj;
            final ForestAddress address = addressSource.getAddress(request);
            if (isBaseInterceptor(request)) {
                request.baseAddress(address);
            } else {
                request.address(address, false);
            }
        } else {
            final ForestAddress address = new ForestAddress(scheme, host, port, basePath);
            if (isBaseInterceptor(request)) {
                request.baseAddress(address);
            } else {
                request.address(address, false);
            }
        }

    }


    @Override
    public void onMethodInitialized(ForestMethod method, Address annotation) {
    }
}
