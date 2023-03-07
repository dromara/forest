package com.dtflys.forest.test.convert.xml.jaxb.client;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.XMLBody;
import com.dtflys.forest.test.convert.xml.jaxb.interceptor.XmlResponseInterceptor;
import com.dtflys.forest.test.convert.xml.jaxb.pojo.XmlTestParam;

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
