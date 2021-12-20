package com.dtflys.test.reflection;

import com.dtflys.forest.reflection.ObjectConstructor;

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
