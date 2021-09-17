package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.callback.AddressSource;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestAddress;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

/**
 * 重试注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class AddressLifeCycle implements MethodAnnotationLifeCycle<Address, Object> {

    private final static String PARAM_KEY_ADDRESS_SOURCE = "__address_source";
    private final static String PARAM_KEY_ADDRESS = "__address";

    @Override
    public void onMethodInitialized(ForestMethod method, Address annotation) {
        Class<? extends AddressSource> clazz = annotation.source();
        if (clazz != null && !AddressSource.class.equals(clazz)) {
            try {
                AddressSource addressSource = clazz.newInstance();
                method.setExtensionParameterValue(PARAM_KEY_ADDRESS_SOURCE, addressSource);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }
        method.setExtensionParameterValue(PARAM_KEY_ADDRESS, annotation);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Address annotation = (Address) request.getMethod().getExtensionParameterValue(PARAM_KEY_ADDRESS);
        String hostStr = annotation.host();
        String portStr = annotation.port();
        Object addressSource = request.getMethod().getExtensionParameterValue(PARAM_KEY_ADDRESS_SOURCE);
        // 先判断是否有设置 host
        if (StringUtils.isNotBlank(hostStr)) {
            MappingTemplate hostTemplate = request.getMethod().makeTemplate(hostStr.trim());
            String host = hostTemplate.render(args);
            request.host(host);
        }

        // 再判断是否有设置 port
        if (StringUtils.isNotBlank(portStr)) {
            MappingTemplate portTemplate = request.getMethod().makeTemplate(portStr.trim());
            String portRendered = portTemplate.render(args);
            if (!Character.isDigit(portRendered.charAt(0))) {
                throw new ForestRuntimeException("[Forest] property 'port' of annotation @Address must be a number!");
            }
            try {
                Integer port = Integer.parseInt(portRendered);
                request.port(port);
            } catch (Throwable th) {
                throw new ForestRuntimeException("[Forest] property 'port' of annotation @Address must be a number!");
            }
        }

        // 最后判断有无设置回调函数，此项设置会覆盖 host 和 port 属性的设置
        if (addressSource != null && addressSource instanceof AddressSource) {
            ForestAddress address = ((AddressSource) addressSource).getAddress(request);
            request.address(address);
        }

    }


}
