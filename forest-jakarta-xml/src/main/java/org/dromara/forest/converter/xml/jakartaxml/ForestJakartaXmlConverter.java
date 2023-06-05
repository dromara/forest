package org.dromara.forest.converter.xml.jakartaxml;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.converter.xml.ForestXmlConverter;
import org.dromara.forest.exceptions.ForestConvertException;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestBody;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.http.body.ObjectRequestBody;
import org.dromara.forest.http.body.StringRequestBody;
import org.dromara.forest.utils.ForestDataType;
import org.dromara.forest.utils.ReflectUtils;
import org.dromara.forest.utils.StringUtils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 Jakarta JAXB 实现的XML转换器
 *
 * @author gongjun
 * @since 1.5.29
 */
public class ForestJakartaXmlConverter implements ForestXmlConverter {


    private final static Map<Class<?>, JAXBContext> JAXB_CONTEXT_CACHE = new ConcurrentHashMap<>();

    private JAXBContext getJAXBContext(final Class<?> clazz) {
        return JAXB_CONTEXT_CACHE.computeIfAbsent(clazz, key -> {
            try {
                return JAXBContext.newInstance(clazz);
            } catch (JAXBException e) {
                throw new ForestRuntimeException(e);
            }
        });
    }

    @Override
    public String encodeToString(final Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return obj.toString();
        }
        if (obj instanceof Map || obj instanceof List) {
            throw new ForestRuntimeException("[Forest] Jakarta JAXB XML converter dose not support translating instance of java.util.Map or java.util.List");
        }
        try {
            final JAXBContext jaxbContext = getJAXBContext(obj.getClass());
            final StringWriter writer = new StringWriter();
            createMarshaller(jaxbContext, "UTF-8").marshal(obj, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new ForestConvertException(this, e);
        }

    }

    @Override
    public byte[] encodeRequestBody(final ForestBody body, final Charset charset, final ConvertOptions options) {
        final StringBuilder builder = new StringBuilder();
        for (final ForestRequestBody item : body) {
            if (item instanceof ObjectRequestBody) {
                final Object obj = ((ObjectRequestBody) item).getObject();
                final String text = encodeToString(obj);
                builder.append(text);
            } else if (item instanceof StringRequestBody) {
                builder.append(((StringRequestBody) item).getContent());
            }
        }
        return builder.toString().getBytes(charset);
    }

    @Override
    public <T> T convertToJavaObject(final String source, final Class<T> targetType) {
        try {
            final JAXBContext jaxbContext = getJAXBContext(targetType);
            final StringReader reader = new StringReader(source);
            return (T) createUnmarshaller(jaxbContext).unmarshal(reader);
        } catch (JAXBException e) {
            throw new ForestConvertException(this, e);
        }
    }


    @Override
    public <T> T convertToJavaObject(final String source, final Type targetType) {
        final Class clazz = ReflectUtils.toClass(targetType);
        return (T) convertToJavaObject(source, clazz);
    }

    @Override
    public <T> T convertToJavaObject(final byte[] source, final Class<T> targetType, final Charset charset) {
        final String str = StringUtils.fromBytes(source, charset);
        return (T) convertToJavaObject(str, targetType);

    }

    @Override
    public <T> T convertToJavaObject(final byte[] source, final Type targetType, final Charset charset) {
        final Class clazz = ReflectUtils.toClass(targetType);
        return (T) convertToJavaObject(source, clazz, charset);
    }


    public Marshaller createMarshaller(final JAXBContext jaxbContext, final String encoding) {
        try {
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            if (StringUtils.isNotEmpty(encoding)) {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            }
            return marshaller;
        } catch (JAXBException e) {
            throw new ForestRuntimeException(e);
        }
    }

    public Unmarshaller createUnmarshaller(final JAXBContext jaxbContext) {
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
