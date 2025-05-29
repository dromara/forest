package com.dtflys.forest.test.http.annmerge;

import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;

public interface AnnotationMergeClient {

    @Post("/")
    @MyHeaders
    String testMyHeaders(@Var("port") int port);
}
