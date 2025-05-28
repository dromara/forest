package com.dtflys.forest.http.cookie;

import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

public interface ForestCookieStorage {

    ForestCookies load(ForestRequest request);

    void save(ForestRequest request, ForestResponse response, ForestCookies cookies);


}
