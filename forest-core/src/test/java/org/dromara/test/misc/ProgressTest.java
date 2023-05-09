package org.dromara.test.misc;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.utils.ForestProgress;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-08-19 23:23
 */
public class ProgressTest {

    @Test
    public void testProgress() {
        ForestRequest request = Mockito.mock(ForestRequest.class);
        ForestProgress progress = new ForestProgress(request, 100);
        progress.setBegin(true);
        assertThat(progress.getTotalBytes()).isEqualTo(100);
        assertThat(progress.isBegin()).isTrue();
        assertThat(progress.isDone()).isFalse();
        progress.setCurrentBytes(0);
        assertThat(progress.getRate()).isEqualTo(0);
        progress.setCurrentBytes(10);
        assertThat(progress.getRate()).isEqualTo(10 * 1.0F / 100);
        progress.setCurrentBytes(100);
        assertThat(progress.getRate()).isEqualTo(1);
        progress.setBegin(false);
        progress.setDone(true);
        assertThat(progress.isBegin()).isFalse();
        assertThat(progress.isDone()).isTrue();
    }


    @Test
    public void testProgressWithNegativeTotalBytes() {
        ForestRequest request = Mockito.mock(ForestRequest.class);
        ForestProgress progress = new ForestProgress(request, -100);
        progress.setBegin(true);
        assertThat(progress.getTotalBytes()).isEqualTo(-100);
        progress.setCurrentBytes(0);
        assertThat(progress.getRate()).isEqualTo(0);
        progress.setCurrentBytes(10);
        assertThat(progress.getRate()).isEqualTo(0);
        progress.setCurrentBytes(100);
        assertThat(progress.getRate()).isEqualTo(0);
    }

}
