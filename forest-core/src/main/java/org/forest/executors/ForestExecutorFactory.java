package org.forest.executors;

import org.forest.http.ForestRequest;
import org.forest.reflection.ForestMethod;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 18:22
 */
public interface ForestExecutorFactory {

    HttpExecutor create(ForestRequest request);

}
