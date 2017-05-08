package org.forest.conf;

import junit.framework.Assert;
import org.forest.config.ForestConfiguration;
import org.junit.Test;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-08 12:40
 */
public class TestForestConfiguration {

    @Test
    public void testDefault() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        Assert.assertEquals(Integer.valueOf(3000), configuration.getTimeout());
        Assert.assertEquals(Integer.valueOf(2000), configuration.getConnectTimeout());
        Assert.assertEquals(Integer.valueOf(500), configuration.getMaxConnections());
        Assert.assertEquals(Integer.valueOf(500), configuration.getMaxRouteConnections());
        Assert.assertNotNull(configuration.getJsonCoverter());
    }

}
