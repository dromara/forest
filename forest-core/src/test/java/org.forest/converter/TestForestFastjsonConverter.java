package org.forest.converter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sun.javafx.collections.MappingChange;
import junit.framework.Assert;
import org.forest.converter.json.ForestFastjsonConverter;
import org.forest.exceptions.ForestRuntimeException;
import org.junit.Test;

import java.util.Map;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-05-08 23:13
 */
public class TestForestFastjsonConverter {

    @Test
    public void testSerializerFeature() {
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        String defaultSerializerFeatureName = forestFastjsonConverter.getSerializerFeatureName();
        SerializerFeature defaultSerializerFeature = forestFastjsonConverter.getSerializerFeature();
        Assert.assertEquals(SerializerFeature.DisableCircularReferenceDetect.name(),
                defaultSerializerFeatureName);
        Assert.assertEquals(defaultSerializerFeature.name(),
                defaultSerializerFeatureName);

        forestFastjsonConverter.setSerializerFeatureName(SerializerFeature.WriteClassName.name());
        Assert.assertEquals(SerializerFeature.WriteClassName.name(),
                forestFastjsonConverter.getSerializerFeatureName());
        Assert.assertEquals(SerializerFeature.WriteClassName,
                forestFastjsonConverter.getSerializerFeature());

        forestFastjsonConverter.setSerializerFeature(SerializerFeature.BeanToArray);
        Assert.assertEquals(SerializerFeature.BeanToArray.name(),
                forestFastjsonConverter.getSerializerFeatureName());
        Assert.assertEquals(SerializerFeature.BeanToArray,
                forestFastjsonConverter.getSerializerFeature());
    }

    @Test
    public void testConvertToJson() {
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        String text = forestFastjsonConverter.convertToJson(new Integer[] {100, 10});
        Assert.assertEquals("[100,10]", text);
    }

    @Test
    public void testConverterError() {
        String badJsonText = "{\"a\"=1";
        ForestFastjsonConverter forestFastjsonConverter = new ForestFastjsonConverter();
        boolean error = false;
        try {
            forestFastjsonConverter.convertToJavaObject(badJsonText, Map.class);
        } catch (ForestRuntimeException e) {
            error = true;
        }
        Assert.assertTrue(error);
    }

}
