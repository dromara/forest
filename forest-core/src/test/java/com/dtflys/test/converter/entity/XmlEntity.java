package com.dtflys.test.converter.entity;


import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * 趣天返回结果标准类
 * @param <T>
 */
@XmlRootElement(name = "Service")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlEntity<T>  {

    @XmlElementRefs({
            @XmlElementRef(name="Order", type= XmlOrder.class),
    })
    List<T> order;

    @XmlElement(name="ResultCode")
    int resultCode;
    @XmlElement(name="ResultMsg")
    String resultMsg;
    @XmlElement(name="Total")
    int total;

    public List<T> getOrder() {
        return order;
    }

    public void setOrder(List<T> order) {
        this.order = order;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
