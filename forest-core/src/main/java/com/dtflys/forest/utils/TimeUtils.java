package com.dtflys.forest.utils;

import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    /**
     * 时长是否为空
     *
     * @param duration 时长
     * @return {@code true}: 时长为空, {@code false}: 时长不为空
     */
    public static boolean isNone(final Integer duration) {
        if (duration == null) {
            return true;
        }
        return duration < 0;
    }

    /**
     * 时长是否不为空
     *
     * @param duration 时长, 整数对象
     * @return {@code true}: 时长不为空, {@code false}: 时长为空
     */
    public static boolean isNotNone(final Integer duration) {
        return !isNone(duration);
    }

    /**
     * 将特定时间单位字段转换为毫秒数
     *
     * @param duration 时长, 整数对象
     * @param timeUnit 时间单位
     * @return 毫秒数
     */
    public static Integer toMillis(final Integer duration, final TimeUnit timeUnit) {
        if (isNone(duration)) {
            return null;
        }
        if (timeUnit != null) {
            return (int) timeUnit.toMillis(duration);
        }
        return duration;
    }

    /**
     * 将特定时间单位字段转换为毫秒数
     *
     * @param name 字段名
     * @param duration 时长 - {@link Duration}对象
     * @return 毫秒数
     */
    public static Integer toMillis(final String name, final Duration duration) {
        if (duration != null) {
            final long millis = duration.toMillis();
            if (millis > Integer.MAX_VALUE) {
                throw new ForestRuntimeException(name + " (" + millis + " ms) is too large");
            }
            return (int) millis;
        }
        return null;
    }

}
