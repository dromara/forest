package com.dtflys.forest.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Forest路由集合
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.22
 */
public class ForestRoutes {

    private final static Map<String, ForestRoute> routes = new HashMap<>();

    public static ForestRoute getRoute(String host, int port) {
        String domain = ForestRoute.domain(host, port);
        ForestRoute route = routes.get(domain);
        if (route == null) {
            synchronized (ForestRoutes.class) {
                if (route == null) {
                    route = new ForestRoute(host, port);
                    routes.put(domain, route);
                }
            }
        }
        return route;
    }

}
