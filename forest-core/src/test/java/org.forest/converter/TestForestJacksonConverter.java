package org.forest.converter;

import junit.framework.Assert;
import org.forest.converter.json.ForestJacksonConverter;
import org.junit.Test;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-05-08 23:26
 */
public class TestForestJacksonConverter {

    @Test
    public void testConvertToJson() {
        ForestJacksonConverter forestJacksonConverter = new ForestJacksonConverter();
        String text = forestJacksonConverter.convertToJson(new Integer[] {100, 10});
        Assert.assertEquals("[100,10]", text);
    }

}
