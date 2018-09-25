package com.dtflys.test.http.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-12-15 15:46
 */
@XmlRootElement(name = "misc")
public class XmlTestParam {

    private Integer a;

    private Integer b;

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public Integer getB() {
        return b;
    }

    public void setB(Integer b) {
        this.b = b;
    }
}
