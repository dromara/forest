package com.dtflys.forest.springboot.test.logging;

import com.dtflys.forest.logging.DefaultLogHandler;

public class TestLogHandler extends DefaultLogHandler {

    @Override
    public void logContent(String content) {
        super.logContent("[Test] " + content);
    }
}
