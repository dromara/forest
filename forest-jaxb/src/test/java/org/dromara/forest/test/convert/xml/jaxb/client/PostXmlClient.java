package org.dromara.forest.test.convert.xml.jaxb.client;

import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.DataVariable;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.annotation.XMLBody;
import org.dromara.forest.test.convert.xml.jaxb.pojo.XmlTestParam;
import org.dromara.forest.test.convert.xml.jaxb.interceptor.XmlResponseInterceptor;

public interface PostXmlClient {

    @Request(
            url = "http://localhost:{port}/xml",
            type = "post",
            contentType = "application/xml"
    )
    String postXml(@Body(filter = "xml") XmlTestParam testParam);


    @Request(
            url = "http://localhost:{port}/xml",
            type = "post",
            contentType = "application/xml",
            data = "{xml(misc)}"
    )
    String postXmlInDataProperty(@DataVariable("misc") XmlTestParam testParam);


    @Request(
            url = "http://localhost:{port}/xml",
            type = "post",
            contentType = "application/xml",
            data = "{xml($0)}"
    )
    String postXmlInDataProperty2(XmlTestParam testParam);


    @Post("http://localhost:{port}/xml")
    String postXmlWithXMLBodyAnn(@XMLBody XmlTestParam testParam);


    @Post("http://localhost:{port}/xml")
    String postXmlBodyString(@XMLBody String xml);

    @Post(url = "http://localhost:{port}/xml-response", interceptor = XmlResponseInterceptor.class)
    XmlTestParam postXmlWithXMLBodyAnnAndReturnObj(@XMLBody XmlTestParam testParam);

}
