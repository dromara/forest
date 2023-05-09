package org.dromara.forest.core.test.misc;

import cn.hutool.core.lang.TypeReference;
import org.dromara.forest.utils.ReflectUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectUtilsTest {

    @Test
    public void testGetClassByType() {
        assertThat(ReflectUtils.toClass(byte.class)).isNotNull().isEqualTo(byte.class);
        assertThat(ReflectUtils.toClass(int.class)).isNotNull().isEqualTo(int.class);
        assertThat(ReflectUtils.toClass(Object.class)).isNotNull().isEqualTo(Object.class);
        List<String> list = new ArrayList<>();
        assertThat(ReflectUtils.toClass(list.getClass()))
                .isNotNull()
                .isEqualTo(ArrayList.class);
        assertThat(ReflectUtils.toClass(
                new TypeReference<Map<String, String>>() {}.getType()))
                .isNotNull()
                .isEqualTo(Map.class);
        assertThat(ReflectUtils.toClass(
                new TypeReference<String>() {}.getType()))
                .isNotNull()
                .isEqualTo(String.class);
    }
}
