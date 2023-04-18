package org.dromara.forest.test.http.encoder;


import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.BodyType;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.backend.ContentType;
import org.dromara.forest.converter.json.FastjsonEncoder;
import org.dromara.forest.converter.json.GsonEncoder;
import org.dromara.forest.converter.json.JacksonEncoder;
import org.dromara.forest.http.ForestRequest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Address(port = "{port}")
public interface EncoderClient {

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    class Entry {
        @XmlElement
        private String name;
        @XmlElement
        private String value;

        public Entry() {
        }

        public Entry(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    @BodyType("json")
    @Post(url = "/", contentType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
    ForestRequest testEncoder(@Body("name") String name, @Body("value") Object value);

    @BodyType("{0}")
    @Post(url = "/", contentType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
    ForestRequest testEncoder2(String bodyType, @Body Entry entry);

    @BodyType("multipart")
    @Post(url = "/", contentType = ContentType.MULTIPART_FORM_DATA)
    ForestRequest testMutlipart(@Body Entry entry);

    @BodyType(type = "json", encoder = MyEncoder.class)
    @Post(url = "/", contentType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
    ForestRequest testEncoder3(@Body Entry entry);


    @FastjsonEncoder
    @Post(url = "/")
    ForestRequest testFastjson(@Body Entry entry);

    @JacksonEncoder
    @Post(url = "/")
    ForestRequest testJackson(@Body Entry entry);

    @GsonEncoder
    @Post(url = "/")
    ForestRequest testGson(@Body Entry entry);


}
