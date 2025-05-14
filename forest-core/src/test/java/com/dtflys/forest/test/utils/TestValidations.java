package com.dtflys.forest.test.utils;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.dtflys.forest.utils.Validations;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TestValidations {

    @Test
    public void testAssertParamNotNull() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Validations.assertParamNotNull(null, "foo");
        });
    }

    @Test
    public void testAssertParamNotEmpty() {
        Object nullObj = null;
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Validations.assertParamNotEmpty(nullObj, "foo");
        });

        List<Object> emptyList = ListUtil.empty();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Validations.assertParamNotEmpty(emptyList, "foo");
        });

        Map<String, Object> emptyMap = MapUtil.empty();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Validations.assertParamNotEmpty(emptyMap, "foo");
        });

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Validations.assertParamNotEmpty(ListUtil.empty(), "foo");
        });

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Validations.assertParamNotEmpty(MapUtil.empty(), "foo");
        });

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Validations.assertParamNotEmpty("", "foo");
        });
    }
}
