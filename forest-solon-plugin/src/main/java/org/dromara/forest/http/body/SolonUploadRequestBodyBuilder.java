package org.dromara.forest.http.body;


import org.noear.solon.core.handle.UploadedFile;

public class SolonUploadRequestBodyBuilder extends RequestBodyBuilder<UploadedFile, SolonUploadBodyItem, SolonUploadRequestBodyBuilder> {

    @Override
    public SolonUploadBodyItem build(UploadedFile data, String defaultValue) {
        if (data == null) {
            return null;
        }
        SolonUploadBodyItem body = new SolonUploadBodyItem(data);
        body.setDefaultValue(defaultValue);
        return body;
    }
}
