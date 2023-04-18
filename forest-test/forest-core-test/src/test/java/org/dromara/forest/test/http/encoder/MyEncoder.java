package org.dromara.forest.test.http.encoder;

import org.dromara.forest.converter.ConvertOptions;
import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.http.ForestBody;
import org.dromara.forest.http.ForestBodyItem;
import org.dromara.forest.http.body.ObjectBodyItem;

import java.nio.charset.Charset;

public class MyEncoder implements ForestEncoder {

    @Override
    public byte[] encodeRequestBody(ForestBody body, Charset charset, ConvertOptions options) {
        StringBuilder builder = new StringBuilder();
        for (ForestBodyItem item : body) {
            if (item instanceof ObjectBodyItem) {
                builder.append("Data: ")
                        .append(((ObjectBodyItem) item).getObject().toString());
            }
        }
        return builder.toString().getBytes(charset);

    }

}
