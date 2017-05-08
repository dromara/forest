package org.forest.mapping;

import junit.framework.Assert;
import org.forest.config.ForestConfiguration;
import org.forest.reflection.ForestMethod;
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
        Mockito.when(forestMethod.getVariable("name")).thenReturn(nameVar);
        Mockito.when(forestMethod.getVariable("age")).thenReturn(ageVar);
        Mockito.when(forestMethod.getVariableValue("name")).thenReturn("Marry");
        Mockito.when(forestMethod.getVariableValue("matherName")).thenReturn("Linda");
        Mockito.when(forestMethod.getVariableValue("age")).thenReturn(12);
        MappingReference nameRef = new MappingReference(forestMethod, "name");
        MappingReference ageRef = new MappingReference(forestMethod, "age");
        MappingReference matherNameRef = new MappingReference(forestMethod, "matherName");
        Assert.assertEquals("Peter", nameRef.render(new Object[] {"Peter", 15}));
        Mockito.verify(forestMethod).getVariable("name");
        Mockito.verify(forestMethod, Mockito.never()).getVariableValue("name");
        Assert.assertEquals(15, ageRef.render(new Object[] {"Peter", 15}));
        Assert.assertEquals("Linda", matherNameRef.render(new Object[] {"Peter", 15}));
    }
}
