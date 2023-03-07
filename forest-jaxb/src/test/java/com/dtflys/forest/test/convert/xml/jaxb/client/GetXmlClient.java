package com.dtflys.forest.test.convert.xml.jaxb.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.test.convert.xml.jaxb.pojo.XmlEntity;
import com.dtflys.forest.test.convert.xml.jaxb.pojo.XmlOrder;

public interface GetXmlClient {

    @Get(url = "http://localhost:${port}/test/xml")
    XmlEntity<XmlOrder> getXmlData();

}
