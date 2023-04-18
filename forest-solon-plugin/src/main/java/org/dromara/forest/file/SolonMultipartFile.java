package org.dromara.forest.file;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.multipart.ForestMultipart;
import org.dromara.forest.utils.StringUtil;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.UploadedFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SolonMultipartFile extends ForestMultipart<UploadedFile, SolonMultipartFile> {

    private UploadedFile multipartFile;

    @Override
    public SolonMultipartFile setData(UploadedFile data) {
        this.multipartFile = data;
        return this;
    }

    @Override
    public String getOriginalFileName() {
        if (StringUtil.isNotBlank(fileName)) {
            return fileName;
        }
        return multipartFile.getName();
    }

    @Override
    public InputStream getInputStream() {
        return multipartFile.getContent();
    }

    @Override
    public long getSize() {
        return multipartFile.getContentSize();
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public File getFile() {
        throw new ForestRuntimeException("[Forest] SpringMultipartFile instances are not files");
    }

    @Override
    public byte[] getBytes() {
        try {
            return Utils.transferToBytes(multipartFile.getContent());
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }
}
