package org.forest;

import org.forest.interceptor.InterceptorChain;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-15 11:18
 */
public class Forest {

    private static InterceptorChain interceptorChain = new InterceptorChain();

    public static InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }
}
