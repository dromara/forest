package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Request;
import com.dtflys.test.http.model.WxReverseRequest;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-11-27 15:37
 */
public interface WxHttpClient {


    /**
     * 布鲁爱商户撤销微信支付订单
     * @param request
     * @return
     */
    @Request(
            url = "https://api.mch.weixin.qq.com/secapi/pay/reverse",
            type = "POST",
            contentType = "application/xml",
            dataType = "xml",
            logEnabled = false,
            keyStore = "bla-weixin-keystore",
            data = "${xml($0)}")
    String blaReverse(WxReverseRequest request);
    


}
