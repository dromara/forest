package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.mapping.ForestRequestContext;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLUtils;

/**
 * 重试注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class AddressLifeCycle implements MethodAnnotationLifeCycle<Address> {

    private final static String PARAM_KEY_ADDRESS_SOURCE = "__address_source";
    private final static String PARAM_KEY_ADDRESS = "__address";

    @Override
    public void onMethodInitialized(ForestMethod method, Address annotation) {
        final Class<? extends AddressSource> clazz = annotation.source();
        if (clazz != null && !clazz.isInterface()) {
            final AddressSource addressSource = method.getConfiguration().getForestObject(clazz);
            method.setExtensionParameterValue(PARAM_KEY_ADDRESS_SOURCE, addressSource);
        }
        method.setExtensionParameterValue(PARAM_KEY_ADDRESS, annotation);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Address annotation = (Address) request.getMethod().getExtensionParameterValue(PARAM_KEY_ADDRESS);
        final String schemeStr = annotation.scheme();
        final String hostStr = annotation.host();
        final String portStr = annotation.port();
        final String basePathStr = annotation.basePath();
        final Object addressSource = request.getMethod().getExtensionParameterValue(PARAM_KEY_ADDRESS_SOURCE);
        String basePath = null;
        String scheme = null;
        Integer port = null;
        String host = null;

        // 判断是否有设置 basePath
        if (StringUtils.isNotBlank(basePathStr)) {
            final MappingTemplate basePathTemplate = request.getMethod().makeTemplate(Address.class, "basePath", basePathStr.trim());
            basePath = basePathTemplate.render(request, args);
        }

        // 判断是否有设置 scheme
        if (StringUtils.isNotBlank(schemeStr)) {
            final MappingTemplate schemeTemplate = request.getMethod().makeTemplate(Address.class, "schema", schemeStr.trim());
            scheme = schemeTemplate.render(request, args);
        }

        // 判断是否有设置 host
        if (StringUtils.isNotBlank(hostStr)) {
            final MappingTemplate hostTemplate = request.getMethod().makeTemplate(Address.class, "host", hostStr.trim());
            host = hostTemplate.render(request, args);
        }

        // 判断是否有设置 port
        if (StringUtils.isNotBlank(portStr)) {
            final MappingTemplate portTemplate = request.getMethod().makeTemplate(Address.class, "port", portStr.trim());
            final String portRendered = portTemplate.render(request, args);
            if (!Character.isDigit(portRendered.charAt(0))) {
                throw new ForestRuntimeException("[Forest] property 'port' of annotation @Address must be a number!");
            }
            try {
                port = Integer.parseInt(portRendered);
            } catch (Throwable th) {
                throw new ForestRuntimeException("[Forest] property 'port' of annotation @Address must be a number!");
            }
        }

        // 最后判断有无设置回调函数，此项设置会覆盖 host 和 port 以及 scheme 属性的设置
        if (addressSource != null && addressSource instanceof AddressSource) {
            final ForestAddress address = ((AddressSource) addressSource).getAddress(request);
            request.address(address, false);
        } else {
            final ForestAddress address = new ForestAddress(scheme, host, port, basePath);
            request.address(address, false);
        }

    }


}
