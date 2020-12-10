package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestCookieMap;
import com.dtflys.forest.http.ForestRequest;

public interface OnLoadCookie {

    void onLoadCookie(ForestRequest request, ForestCookieMap cookies);

}
