package com.dtflys.forest.http;

import java.io.Closeable;

public interface UnclosedResponse<T> extends Res<T>, Closeable {

    
}
