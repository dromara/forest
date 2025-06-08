package com.dtflys.forest.test.utils;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.TimeUtils;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class TestTimeUtils {

    @Test
    public void testIsNone() {
        assertThat(TimeUtils.isNone(null)).isTrue();
        assertThat(TimeUtils.isNone(-1)).isTrue();
        assertThat(TimeUtils.isNone(0)).isFalse();
        assertThat(TimeUtils.isNone(1000)).isFalse();
    }

    @Test
    public void testIsNontNone() {
        assertThat(TimeUtils.isNotNone(null)).isFalse();
        assertThat(TimeUtils.isNotNone(-1)).isFalse();
        assertThat(TimeUtils.isNotNone(0)).isTrue();
        assertThat(TimeUtils.isNotNone(1000)).isTrue();
    }

    @Test
    public void testToMillis() {
        assertThat(TimeUtils.toMillis(null, TimeUnit.SECONDS)).isNull();
        assertThat(TimeUtils.toMillis(1, null)).isEqualTo(1);
        assertThat(TimeUtils.toMillis(1, TimeUnit.MILLISECONDS)).isEqualTo(1);
        assertThat(TimeUtils.toMillis(1, TimeUnit.SECONDS)).isEqualTo(1000);
        assertThat(TimeUtils.toMillis(1, TimeUnit.MINUTES)).isEqualTo(1000 * 60);
        assertThat(TimeUtils.toMillis(1, TimeUnit.HOURS)).isEqualTo(1000 * 60 * 60);
        assertThat(TimeUtils.toMillis(1, TimeUnit.DAYS)).isEqualTo(1000 * 60 * 60 * 24);

        assertThat(TimeUtils.toMillis("foo", null)).isNull();
        assertThat(TimeUtils.toMillis("foo", Duration.ofMillis(1))).isEqualTo(1);
        assertThat(TimeUtils.toMillis("foo", Duration.ofSeconds(1))).isEqualTo(1000);
        assertThat(TimeUtils.toMillis("foo", Duration.ofMinutes(1))).isEqualTo(1000 * 60);
        assertThat(TimeUtils.toMillis("foo", Duration.ofHours(1))).isEqualTo(1000 * 60 * 60);
        assertThat(TimeUtils.toMillis("foo", Duration.ofDays(1))).isEqualTo(1000 * 60 * 60 * 24);

        assertThatExceptionOfType(ForestRuntimeException.class).isThrownBy(() -> {
            TimeUtils.toMillis("foo", Duration.ofMillis(Integer.MAX_VALUE + 1L));
        });
    }

}
