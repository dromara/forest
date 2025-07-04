package com.dtflys.forest.test.mapping;

import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.mapping.MappingURLTemplate;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestVariables {

    @Test
    public void testConstantInTemplate() {
        MappingTemplate template1 = MappingTemplate.create("/aaa/foo/bar");
        assertThat(template1).isNotNull();
        assertThat(template1.isConstant()).isTrue();

        MappingTemplate template2 = MappingTemplate.create("/aaa/{true}/{1.0f}");
        assertThat(template2).isNotNull();
        assertThat(template2.isConstant()).isTrue();
    }


    @Test
    public void testVarsInTemplate() {
        MappingTemplate template = MappingTemplate.create("/aaa/{foo}/{bar}");
        assertThat(template).isNotNull();
        assertThat(template.isConstant()).isFalse();
    }

    @Test
    public void testConstantInUrlTemplate() {
        MappingURLTemplate template1 = MappingURLTemplate.get("/aaa/foo/bar");
        assertThat(template1).isNotNull();
        assertThat(template1.isConstant()).isTrue();

        MappingURLTemplate template2 = MappingURLTemplate.get("/aaa/{true}/{1.0f}");
        assertThat(template2).isNotNull();
        assertThat(template2.isConstant()).isTrue();
    }


    @Test
    public void testVarsInUrlTemplate() {
        MappingURLTemplate template = MappingURLTemplate.get("/aaa/{foo}/{bar}");
        assertThat(template).isNotNull();
        assertThat(template.isConstant()).isFalse();
    }

}
