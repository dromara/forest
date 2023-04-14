package org.dromara.forest.test.reflection;

import org.dromara.forest.callback.SuccessWhen;
import org.dromara.forest.config.ForestConfiguration;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author caihongming
 * @since 1.5.17
 **/
public class TestObjectConstructor {

    @Test
    public void testRegisterConstructor() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        configuration.getForestObjectFactory().registerConstructor(SuccessWhen.class, MySuccessWhen::new);
        SuccessWhen forestObject = configuration.getForestObject(SuccessWhen.class);
        assertEquals(MySuccessWhen.class, forestObject.getClass());
    }

    @Test
    public void testRegisterObject() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        configuration.getForestObjectFactory().registerObject(SuccessWhen.class, new MySuccessWhen2());
        SuccessWhen forestObject = configuration.getForestObject(SuccessWhen.class);
        assertEquals(MySuccessWhen2.class, forestObject.getClass());
    }

}
