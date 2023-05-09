package org.dromara.test.handler;

import java.io.Serializable;

/**
 * @author YAKAX
 * @since 2021-04-02 19:30
 **/
public class DefinitionInfo implements Serializable {
    private static final long serialVersionUID = 4864008582215127428L;
    private String token;

    public DefinitionInfo() {
    }

    public DefinitionInfo(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
