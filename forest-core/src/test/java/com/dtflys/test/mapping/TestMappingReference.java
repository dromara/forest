package com.dtflys.test.mapping;

import com.dtflys.forest.mapping.ForestRequestContext;
import com.dtflys.forest.mapping.MappingIndex;
import com.dtflys.forest.mapping.MappingParameter;
import junit.framework.Assert;
import com.dtflys.forest.mapping.MappingReference;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-08 17:41
 */
public class TestMappingReference {

    @Test
    public void testParameter() {
        ForestMethod forestMethod = Mockito.mock(ForestMethod.class);
        MappingVariable nameVar = new MappingVariable("name", String.class);
        nameVar.setIndex(0);
        MappingVariable ageVar = new MappingVariable("age", String.class);
        ageVar.setIndex(1);
        ForestRequestContext context = new ForestRequestContext(forestMethod, new Object[] {"Peter", 15});
        context.setVar("name", "Marry");
        context.setVar("motherName", "Linda");
        context.setVar("age", 12);

        MappingReference nameRef = new MappingReference("name", false);
        MappingReference ageRef = new MappingReference("age", false);
        MappingReference motherNameRef = new MappingReference("motherName", false);
        MappingIndex nameIdx = new MappingIndex(0);
        MappingIndex ageIdx = new MappingIndex(1);
        Assert.assertEquals("Marry", nameRef.render(context));
        Assert.assertEquals(12, ageRef.render(context));
        Assert.assertEquals("Linda", motherNameRef.render(context));
        Assert.assertEquals("Peter", nameIdx.render(context));
        Assert.assertEquals(15, ageIdx.render(context));
        Assert.assertEquals("Peter", nameVar.getValue(context));
        Assert.assertEquals(15, ageVar.getValue(context));

    }
}
