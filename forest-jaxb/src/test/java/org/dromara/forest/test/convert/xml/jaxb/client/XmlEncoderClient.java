package org.dromara.forest.test.convert.xml.jaxb.client;


import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.BodyType;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.backend.ContentType;
import com.dtflys.forest.converter.json.FastjsonEncoder;
import org.dromara.forest.http.ForestRequest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Address(port = "{port}")
public interface XmlEncoderClient {

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


    @BodyType("{0}")
    @Post(url = "/", contentType = ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
    ForestRequest testEncoder(String bodyType, @Body Entry entry);



}
