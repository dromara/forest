package org.dromara.forest.test.misc;

import cn.hutool.core.lang.TypeReference;
import org.dromara.forest.utils.ReflectUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectUtilTest {

    @Test
    public void testGetClassByType() {
        assertThat(ReflectUtil.toClass(byte.class)).isNotNull().isEqualTo(byte.class);
        assertThat(ReflectUtil.toClass(int.class)).isNotNull().isEqualTo(int.class);
        assertThat(ReflectUtil.toClass(Object.class)).isNotNull().isEqualTo(Object.class);
        List<String> list = new ArrayList<>();
        assertThat(ReflectUtil.toClass(list.getClass()))
                .isNotNull()
                .isEqualTo(ArrayList.class);
        assertThat(ReflectUtil.toClass(
                new TypeReference<Map<String, String>>() {}.getType()))
                .isNotNull()
                .isEqualTo(Map.class);
        assertThat(ReflectUtil.toClass(
                new TypeReference<String>() {}.getType()))
                .isNotNull()
                .isEqualTo(String.class);
    }
}
