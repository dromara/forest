package org.forest.test;

import junit.framework.TestCase;
import org.forest.JUnit4ClassRunner;
import org.forest.client.BosonClient;
import org.forest.config.ForestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-02-21 17:51
 */
@RunWith(JUnit4ClassRunner.class)
public class BosonTest extends TestCase {

    private ForestConfiguration configuration = ForestConfiguration.configuration();

    @Test
    public void testSuggest() {
        BosonClient bosonnlpClient = configuration.createInstance(BosonClient.class);
        Integer top = 3;
        String word = "\\u9c9c\\u82b1";
        List<List> resp = bosonnlpClient.suggest(word, top);
        assertNotNull(resp);
    }

}
