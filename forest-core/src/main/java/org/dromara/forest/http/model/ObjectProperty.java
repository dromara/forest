package org.dromara.forest.http.model;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.http.ForestRequest;

public interface ObjectProperty {

    Object getInstance();

    String getName();

    Object getValue(ForestRequest request, ConvertOptions options);
}
