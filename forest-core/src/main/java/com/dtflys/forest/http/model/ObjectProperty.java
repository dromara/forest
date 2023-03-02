package com.dtflys.forest.http.model;

import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.http.ForestRequest;

public interface ObjectProperty {

    Object getInstance();

    String getName();

    Object getValue(ForestRequest request, ConvertOptions options);
}
