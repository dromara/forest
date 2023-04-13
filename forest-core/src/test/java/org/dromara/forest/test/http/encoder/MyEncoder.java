package org.dromara.forest.test.http.encoder;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.http.ForestBody;
import org.dromara.forest.http.ForestRequestBody;
import org.dromara.forest.http.body.ObjectRequestBody;

import java.nio.charset.Charset;

public class MyEncoder implements ForestEncoder {

    @Override
    public byte[] encodeRequestBody(ForestBody body, Charset charset, ConvertOptions options) {
        StringBuilder builder = new StringBuilder();
        for (ForestRequestBody item : body) {
            if (item instanceof ObjectRequestBody) {
                builder.append("Data: ")
                        .append(((ObjectRequestBody) item).getObject().toString());
            }
        }
        return builder.toString().getBytes(charset);

    }

}
