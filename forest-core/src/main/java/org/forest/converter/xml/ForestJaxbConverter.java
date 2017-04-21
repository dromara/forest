package org.forest.converter.xml;

import org.forest.exceptions.ForestRuntimeException;
import org.forest.utils.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;

/**
 * 基于JAXB实现的XML转换器
 * @author gongjun
 * @since 2016-07-12
 */
public class ForestJaxbConverter implements ForestXmlConverter {

    @Override
    public String convertToXml(Object obj) {
        JAXBContext jaxbContext = null;
        try {
            StringWriter writer = new StringWriter();
            createMarshaller(jaxbContext, null).marshal(obj, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new ForestRuntimeException(e);
        }

    }

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(targetType);
            StringReader reader = new StringReader(source);
            return (T) createUnmarshaller(jaxbContext).unmarshal(reader);
        } catch (JAXBException e) {
            throw new ForestRuntimeException(e);
        }

    }


    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        return convertToJavaObject(source, targetType);
    }


    public Marshaller createMarshaller(JAXBContext jaxbContext, String encoding) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            if (StringUtils.isNotEmpty(encoding)) {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            }
            return marshaller;
        } catch (JAXBException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public Unmarshaller createUnmarshaller(JAXBContext jaxbContext) {
        try {
            return jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }




}
