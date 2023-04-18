package org.dromara.forest.http.body;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestBodyItem;
import org.dromara.forest.utils.ForestDataType;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.UploadedFile;

import java.io.IOException;

public class SolonUploadBodyItem extends ForestBodyItem {

    private UploadedFile multipartFile;

    public SolonUploadBodyItem(UploadedFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public UploadedFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(UploadedFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    @Override
    public byte[] getByteArray() {
        try {
            return Utils.transferToBytes(multipartFile.getContent());
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public ForestDataType getDefaultBodyType() {
        return ForestDataType.MULTIPART;
    }

    @Override
    public SolonUploadBodyItem clone() {
        SolonUploadBodyItem newBody = new SolonUploadBodyItem(multipartFile);
        newBody.setDefaultValue(getDefaultValue());
        return newBody;
    }
}
