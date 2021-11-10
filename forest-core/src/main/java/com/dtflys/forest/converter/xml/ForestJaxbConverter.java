package com.dtflys.forest.converter.xml;

import com.dtflys.forest.exceptions.ForestConvertException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;
import com.dtflys.forest.http.body.StringRequestBody;
import com.dtflys.forest.reflection.ForestObjectFactory;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 基于JAXB实现的XML转换器
 * @author gongjun
 * @since 2016-07-12
 */
public class ForestJaxbConverter implements ForestXmlConverter {

    @Override
    public String encodeToString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return obj.toString();
        }
        if (obj instanceof Map || obj instanceof List) {
            throw new ForestRuntimeException("[Forest] JAXB XML converter dose not support translating instance of java.util.Map or java.util.List");
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
            StringWriter writer = new StringWriter();
            createMarshaller(jaxbContext, "UTF-8").marshal(obj, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new ForestConvertException(this, e);
        }

    }

    @Override
    public byte[] encodeRequestBody(ForestBody body, Charset charset) {
        StringBuilder builder = new StringBuilder();
        for (ForestRequestBody item : body) {
            if (item instanceof ObjectRequestBody) {
                Object obj = ((ObjectRequestBody) item).getObject();
                String text = encodeToString(obj);
                builder.append(text);
            } else if (item instanceof StringRequestBody) {
                builder.append(((StringRequestBody) item).getContent());
            }
        }
        return builder.toString().getBytes(charset);
    }

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(targetType);
            StringReader reader = new StringReader(source);
            return (T) createUnmarshaller(jaxbContext).unmarshal(reader);
        } catch (JAXBException e) {
            throw new ForestConvertException(this, e);
        }

    }


    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        Class clazz = ReflectUtils.toClass(targetType);
        return (T) convertToJavaObject(source, clazz);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        String str = StringUtils.fromBytes(source, charset);
        return (T) convertToJavaObject(str, targetType);

    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        Class clazz = ReflectUtils.toClass(targetType);
        return (T) convertToJavaObject(source, clazz, charset);
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

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.XML;
    }

}
