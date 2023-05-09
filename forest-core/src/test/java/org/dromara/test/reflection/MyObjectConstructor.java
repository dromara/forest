package org.dromara.test.reflection;

import org.dromara.forest.reflection.ObjectConstructor;

/**
 * @author caihongming
 * @since 1.5.17
 **/
public class MyObjectConstructor implements ObjectConstructor {

    @Override
    public Object construct() {
        return new MySuccessWhen();
    }
}
