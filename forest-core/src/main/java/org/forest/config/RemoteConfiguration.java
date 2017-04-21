package org.forest.config;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-25 18:19
 */
public class RemoteConfiguration {

    private String name;

    private String baseUrl;

    private Integer timeout;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
