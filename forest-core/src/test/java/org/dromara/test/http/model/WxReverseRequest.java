package org.dromara.test.http.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 微信撤销订单请求
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-01-23 15:07
 */
@XmlRootElement(name = "xml")
public class WxReverseRequest {

	private String appid = "";
	private String mch_id = "";
	private String transaction_id = "";
	private String out_trade_no = "";
	private String nonce_str = "";
	private String sign = "";
	private String sign_type = "MD5";

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

}
