/*
 * Copyright (C) 2011-2021 ShenZhen iBOXCHAIN Information Technology Co.,Ltd.
 *
 * All right reserved.
 *
 * This software is the confidential and proprietary
 * information of iBOXCHAIN Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with iBOXCHAIN inc.
 *
 */
package com.dtflys.test.proxy;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.proxy.InterfaceProxyHandler;
import com.dtflys.forest.proxy.ProxyFactory;
import com.dtflys.test.http.client.GetClient;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-03-23
 **/
public class TestDefaultMethod {

  private static ForestConfiguration configuration = ForestConfiguration.configuration();

  private TestDefault test = configuration.createInstance(TestDefault.class);

  @Test
  public void testGetProxyFactory() {
    String testStr = test.test();
    System.out.println(testStr);
    assertEquals("test", testStr);
  }

  public interface TestDefault {

    default String test() {
      return "test";
    }
  }
}
