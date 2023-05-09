package org.dromara.forest.config;

/**
 * Forest Properties配置文件属性
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.1
 */
public class ForestProperties {

    public String getProperty(String name, String defaultValue) {
        return System.getProperty(name, defaultValue);
    }

}
