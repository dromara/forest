package com.dtflys.forest.springboot.test;

import com.dtflys.forest.springboot.annotation.ForestScannerRegister;
import org.junit.AfterClass;

public class BaseSpringBootTest {

    @AfterClass
    public static void cleanup() {
        ForestScannerRegister.cleanBackPackages();
    }


}
