package com.dtflys.test.mapping;

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
        Mockito.when(forestMethod.getVariable("name")).thenReturn(nameVar);
        Mockito.when(forestMethod.getVariable("age")).thenReturn(ageVar);
        Mockito.when(forestMethod.getVariableValue("name", forestMethod)).thenReturn("Marry");
        Mockito.when(forestMethod.getVariableValue("motherName", forestMethod)).thenReturn("Linda");
        Mockito.when(forestMethod.getVariableValue("age", forestMethod)).thenReturn(12);
        Mockito.when(forestMethod.isVariableDefined("motherName")).thenReturn(true);
        MappingReference nameRef = new MappingReference(forestMethod, forestMethod, "name");
        MappingReference ageRef = new MappingReference(forestMethod, forestMethod, "age");
        MappingReference motherNameRef = new MappingReference(forestMethod, forestMethod, "motherName");
        Assert.assertEquals("Peter", nameRef.render(new Object[] {"Peter", 15}));
        Mockito.verify(forestMethod).getVariable("name");
        Mockito.verify(forestMethod, Mockito.never()).getVariableValue("name");
        Assert.assertEquals(15, ageRef.render(new Object[] {"Peter", 15}));
        Mockito.verify(forestMethod, Mockito.never()).getVariableValue("motherName");
        Assert.assertEquals("Linda", motherNameRef.render(new Object[] {"Peter", 15}));
        Mockito.verify(forestMethod).getVariableValue("motherName", forestMethod);
    }
}
