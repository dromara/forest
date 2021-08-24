package com.dtflys.test.misc;

import cn.hutool.core.lang.TypeReference;
import com.dtflys.forest.utils.ReflectUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectUtilsTest {

    @Test
    public void testGetClassByType() {
        assertThat(ReflectUtils.getClassByType(byte.class)).isNotNull().isEqualTo(byte.class);
        assertThat(ReflectUtils.getClassByType(int.class)).isNotNull().isEqualTo(int.class);
        assertThat(ReflectUtils.getClassByType(Object.class)).isNotNull().isEqualTo(Object.class);
        List<String> list = new ArrayList<>();
        assertThat(ReflectUtils.getClassByType(list.getClass()))
                .isNotNull()
                .isEqualTo(ArrayList.class);
        assertThat(ReflectUtils.getClassByType(
                new TypeReference<Map<String, String>>() {}.getType()))
                .isNotNull()
                .isEqualTo(Map.class);
        assertThat(ReflectUtils.getClassByType(
                new TypeReference<String>() {}.getType()))
                .isNotNull()
                .isEqualTo(String.class);
    }
}
