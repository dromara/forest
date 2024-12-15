package com.dtflys.forest.test.http.encoder;

import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.dtflys.forest.http.ForestBody;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.ObjectRequestBody;

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
