package com.dtflys.forest.springboot.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
public class AppTest
{
    Logger log = LoggerFactory.getLogger(AppTest.class);

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testApp()
    {
        log.info( "testApp start" );
    }
}
