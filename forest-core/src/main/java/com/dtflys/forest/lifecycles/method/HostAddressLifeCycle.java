package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.HostAddress;
import com.dtflys.forest.callback.HostAddressSource;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestHostAddress;
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
public class HostAddressLifeCycle implements MethodAnnotationLifeCycle<HostAddress, Object> {

    @Override
    public void onMethodInitialized(ForestMethod method, HostAddress annotation) {
        Class<? extends HostAddressSource> clazz = annotation.source();
        if (clazz != null) {
            try {
                HostAddressSource hostAddressSource = clazz.newInstance();
                method.setExtensionParameterValue("hostAddressSource", hostAddressSource);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }
        method.setExtensionParameterValue("hostAddrAnnotation", annotation);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        HostAddress annotation = (HostAddress) request.getMethod().getExtensionParameterValue("hostAddrAnnotation");
        String hostStr = annotation.host();
        String portStr = annotation.port();
        Object onHostAddress = request.getMethod().getExtensionParameterValue("hostAddressSource");
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
                throw new ForestRuntimeException("[Forest] property 'port' of annotation @HostAddress must be a number!");
            }
            try {
                Integer port = Integer.parseInt(portRendered);
                request.port(port);
            } catch (Throwable th) {
                throw new ForestRuntimeException("[Forest] property 'port' of annotation @HostAddress must be a number!");
            }
        }

        // 最后判断有无设置回调函数，此项设置会覆盖 host 和 port 属性的设置
        if (onHostAddress != null && onHostAddress instanceof HostAddressSource) {
            ForestHostAddress hostAddress = ((HostAddressSource) onHostAddress).getHostAddress(request);
            request.hostAddress(hostAddress);
        }

    }


}
