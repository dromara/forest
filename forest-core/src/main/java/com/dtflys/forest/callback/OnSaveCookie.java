package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookieMap;
import com.dtflys.forest.http.ForestRequest;

import java.util.List;

public interface OnSaveCookie {

    void onSaveCookie(ForestRequest request, ForestCookieMap cookies);
}
