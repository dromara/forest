package org.dromara.forest.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Forest路由集合
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.22
 */
public class ForestRoutes {

    private final static Map<String, ForestRoute> routes = new ConcurrentHashMap<>();

    /**
     * 获取或创建路由
     *
     * @param host 主机地址
     * @param port 端口号
     * @return 路由, {@link ForestRoute}对象实例
     */
    public static ForestRoute getRoute(String host, int port) {
        final String domain = ForestRoute.domain(host, port);
        ForestRoute route = routes.get(domain);
        if (route == null) {
            synchronized (ForestRoutes.class) {
                route = routes.get(domain);
                if (route == null) {
                    route = new ForestRoute(host, port);
                    routes.put(domain, route);
                }
            }
        }
        return route;
    }

}
