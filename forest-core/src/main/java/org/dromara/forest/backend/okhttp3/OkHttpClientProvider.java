package org.dromara.forest.backend.okhttp3;

import org.dromara.forest.backend.BackendClientProvider;
import okhttp3.OkHttpClient;

/**
 * Forest OkHttp 后端的 Client 工厂接口
 *
 * @author dt_flys[dt_flys@hotmail.com]
 * @since 1.5.23
 */
public interface OkHttpClientProvider extends BackendClientProvider<OkHttpClient> {


}
