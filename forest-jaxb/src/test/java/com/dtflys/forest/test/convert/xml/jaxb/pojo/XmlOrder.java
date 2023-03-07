package com.dtflys.forest.test.convert.xml.jaxb.pojo;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Order")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlOrder {
    @XmlElement(name = "PaymentRefundDate")
    String paymentRefundDate;
    @XmlElement(name = "PaymentDate")
    String paymentDate;
    @XmlElement(name = "Status")
    Integer status;
    @XmlElement(name = "OrderNo")
    Long orderNo;
    @XmlElement(name = "CartNo")
    Long cartNo;
    @XmlElement(name = "BuyerPaidAmount")
    Double buyerPaidAmount;
    @XmlElement(name = "ServiceFee")
    Double serviceFee;
    @XmlElement(name = "SettledDate")
    String settledDate;
    @XmlElement(name = "SettledAmount")
    Double settledAmount;

    public String getPaymentRefundDate() {
        return paymentRefundDate;
    }

    public void setPaymentRefundDate(String paymentRefundDate) {
        this.paymentRefundDate = paymentRefundDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getCartNo() {
        return cartNo;
    }

    public void setCartNo(Long cartNo) {
        this.cartNo = cartNo;
    }

    public Double getBuyerPaidAmount() {
        return buyerPaidAmount;
    }

    public void setBuyerPaidAmount(Double buyerPaidAmount) {
        this.buyerPaidAmount = buyerPaidAmount;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(String settledDate) {
        this.settledDate = settledDate;
    }

    public Double getSettledAmount() {
        return settledAmount;
    }

    public void setSettledAmount(Double settledAmount) {
        this.settledAmount = settledAmount;
    }
}
