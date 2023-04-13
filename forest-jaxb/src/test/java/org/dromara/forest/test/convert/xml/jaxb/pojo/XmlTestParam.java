package org.dromara.forest.test.convert.xml.jaxb.pojo;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
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
