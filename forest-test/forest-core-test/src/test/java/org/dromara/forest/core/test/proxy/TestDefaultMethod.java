package org.dromara.forest.core.test.proxy;

import org.dromara.forest.config.ForestConfiguration;
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
    assertEquals("org/dromara/forest/test", testDefaultClient.test());
    assertEquals("test2", testDefaultClient.test2());
    assertEquals("test3: xxx", testDefaultClient.test3("xxx"));
  }

  public interface TestDefaultClient {

    default String test() {
      return "org/dromara/forest/test";
    }

    default String test2() {
      return "test2";
    }

    default String test3(String msg) {
      return "test3: " + msg;
    }

  }
}
