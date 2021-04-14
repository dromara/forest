package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.test.converter.entity.XmlEntity;
import com.dtflys.test.converter.entity.XmlOrder;

public interface XmlClient {

    @Get(url = "http://localhost:${port}/test/xml")
    XmlEntity<XmlOrder> getXmlData();

}
