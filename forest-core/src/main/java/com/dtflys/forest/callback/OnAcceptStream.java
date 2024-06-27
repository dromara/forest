package com.dtflys.forest.callback;

import java.io.InputStream;

@FunctionalInterface
public interface OnAcceptStream {

    void onAccept(InputStream inputStream);
}
