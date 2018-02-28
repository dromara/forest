package org.forest.backend.okhttp3.body;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.forest.backend.AbstractBodyBuilder;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.http.ForestRequest;
import org.forest.mapping.MappingTemplate;
import org.forest.utils.RequestNameValue;

import java.util.List;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 18:18
 */
public abstract class AbstractOkHttp3BodyBuilder extends AbstractBodyBuilder<Request.Builder> {


    protected abstract void setBody(Request.Builder builder, RequestBody body);
    @Override
    protected void setStringBody(Request.Builder builder, String text, String charset, String contentType) {
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody body = RequestBody.create(mediaType, text);
        setBody(builder, body);
    }

    @Override
    protected void setFormData(Request.Builder builder, ForestRequest request, String charset, String contentType, List<RequestNameValue> nameValueList) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        for (int i = 0; i < nameValueList.size(); i++) {
            RequestNameValue nameValue = nameValueList.get(i);
            if (nameValue.isInQuery()) continue;
            String name = nameValue.getName();
            Object value = nameValue.getValue();
            bodyBuilder.addEncoded(name, MappingTemplate.getParameterValue(jsonConverter, value));
        }

        FormBody body = bodyBuilder.build();
        setBody(builder, body);
    }


}
