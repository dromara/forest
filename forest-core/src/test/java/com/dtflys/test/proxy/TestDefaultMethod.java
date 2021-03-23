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
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author caihongming
 * @since 2021-03-23
 **/
public class TestDefaultMethod {

  private static ForestConfiguration configuration = ForestConfiguration.configuration();

  private TestDefaultClient testDefaultClient = configuration.createInstance(TestDefaultClient.class);

  @Test
  public void testGetProxyFactory() {
    assertEquals("test", testDefaultClient.test());
    assertEquals("test2", testDefaultClient.test2());
    assertEquals("test3: xxx", testDefaultClient.test3("xxx"));
  }

  public interface TestDefaultClient {

    default String test() {
      return "test";
    }

    default String test2() {
      return "test2";
    }

    default String test3(String msg) {
      return "test3: " + msg;
    }

  }
}
