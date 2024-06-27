package com.dtflys.forest.callback;

import java.io.InputStream;

@FunctionalInterface
public interface OnAcceptStreamFunction<R> {

    R onAccept(InputStream inputStream);
}
