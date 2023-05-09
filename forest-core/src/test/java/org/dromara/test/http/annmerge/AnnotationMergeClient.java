package org.dromara.test.http.annmerge;

import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Var;

public interface AnnotationMergeClient {

    @Post("/")
    @MyHeaders
    String testMyHeaders(@Var("port") int port);
}
