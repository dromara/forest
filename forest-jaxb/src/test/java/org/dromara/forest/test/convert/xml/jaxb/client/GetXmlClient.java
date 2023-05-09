package org.dromara.forest.test.convert.xml.jaxb.client;

import org.dromara.forest.annotation.Get;
import org.dromara.forest.test.convert.xml.jaxb.pojo.XmlEntity;
import org.dromara.forest.test.convert.xml.jaxb.pojo.XmlOrder;

public interface GetXmlClient {

    @Get(url = "http://localhost:${port}/test/xml")
    XmlEntity<XmlOrder> getXmlData();

}
