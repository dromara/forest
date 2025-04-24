package com.dtflys.forest.test.mapping;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.BasicVariable;
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
        Mockito.when(forestMethod.getVariable("motherName")).thenReturn(new BasicVariable("Linda"));
        
        Mockito.when(forestMethod.isVariableDefined("motherName")).thenReturn(true);
        MappingReference nameRef = new MappingReference(forestMethod, forestMethod, "name", -1, -1);
        MappingReference ageRef = new MappingReference(forestMethod, forestMethod, "age", -1, -1);
        MappingReference motherNameRef = new MappingReference(forestMethod, forestMethod, "motherName", -1, -1);
        ForestRequest request = Forest.request();
        Assert.assertEquals("Peter", nameRef.render(request, new Object[] {"Peter", 15}));
        
        Mockito.when(forestMethod.getVariable("name")).thenReturn(new BasicVariable("Marry"));
        Mockito.verify(forestMethod).getVariable("name");
        Mockito.verify(forestMethod, Mockito.never()).getVariableValue("name");
        
        Assert.assertEquals(15, ageRef.render(request, new Object[] {"Peter", 15}));
        Mockito.when(forestMethod.getVariable("age")).thenReturn(new BasicVariable(12));
        
        Assert.assertEquals("Linda", motherNameRef.render(request, new Object[] {"Peter", 15}));
    }
}
