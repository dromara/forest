package com.dtflys.forest.test.mapping;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.BasicVariable;
import junit.framework.Assert;
import com.dtflys.forest.mapping.MappingReference;
import com.dtflys.forest.mapping.MappingVariable;
import com.dtflys.forest.reflection.ForestMethod;
import org.junit.Test;
import org.mockito.Mockito;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-08 17:41
 */
public class TestMappingReference {

    @Test
    public void testParameter() {
        ForestConfiguration configuration = ForestConfiguration.createConfiguration();
        configuration.setVariable("A", 111);
        configuration.setVariable("B", 222);

        ForestMethod forestMethod = Mockito.spy(new ForestMethod(null,
                configuration, Forest.request().getMethod().getMethod()));
        MappingVariable nameVar = new MappingVariable("name", String.class);
        nameVar.setIndex(0);
        MappingVariable ageVar = new MappingVariable("age", String.class);
        ageVar.setIndex(1);
        when(forestMethod.getVariable("name")).thenReturn(nameVar);
        when(forestMethod.getVariable("age")).thenReturn(ageVar);
        when(forestMethod.getVariable("motherName")).thenReturn(new BasicVariable("Linda"));
        
        when(forestMethod.isVariableDefined("motherName")).thenReturn(true);
        MappingReference nameRef = new MappingReference(forestMethod, "name", -1, -1);
        MappingReference ageRef = new MappingReference(forestMethod, "age", -1, -1);
        MappingReference motherNameRef = new MappingReference(forestMethod, "motherName", -1, -1);

        MappingReference aRef = new MappingReference(forestMethod, "A", -1, -1);
        MappingReference bRef = new MappingReference(forestMethod, "B", -1, -1);
        MappingReference cRef = new MappingReference(forestMethod, "C", -1, -1);

        ForestRequest request = new ForestRequest(configuration, forestMethod);
        assertThat(nameRef.render(request, new Object[] {"Peter", 15})).isEqualTo("Peter");
        assertThat(aRef.render(request)).isEqualTo(111);
        assertThat(bRef.render(request)).isEqualTo(222);
        assertThatExceptionOfType(ForestVariableUndefinedException.class).isThrownBy(() -> cRef.render(request));

        when(forestMethod.getVariable("name")).thenReturn(new BasicVariable("Marry"));
        Mockito.verify(forestMethod).getVariable(eq("name"));
        Mockito.verify(forestMethod, Mockito.never()).getVariableValue(eq("name"));
        
        assertThat(ageRef.render(request, new Object[] {"Peter", 15})).isEqualTo(15);
        when(forestMethod.getVariable("age")).thenReturn(new BasicVariable(12));
        
        assertThat(motherNameRef.render(request, new Object[] {"Peter", 15})).isEqualTo("Linda");
    }
}
