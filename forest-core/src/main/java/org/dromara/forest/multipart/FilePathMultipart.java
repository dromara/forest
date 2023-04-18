package org.dromara.forest.multipart;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.utils.StringUtil;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathMultipart extends ForestMultipart<String, FilePathMultipart> {

    private String filePath;

    private Path path;

    private File file;

    @Override
    public String getOriginalFileName() {
        if (StringUtil.isNotBlank(fileName)) {
            return fileName;
        }
        String[] strs = filePath.split("(/|\\\\)");
        return strs[strs.length - 1];
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public FilePathMultipart setData(String data) {
        this.filePath = data;
        this.path = Paths.get(data).normalize();
        return this;
    }

    @Override
    public long getSize() {
        try {
            return Files.size(this.path);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public InputStream getInputStream() {
        File file = getFile();
        try {
            return FileUtils.openInputStream(file);
        } catch (IOException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public boolean isFile() {
        return true;
    }

    /**
     * 获取文件实际路径
     *
     * @return 文件实际路径
     */
    public String getFilePath() {
        return filePath;
    }

    @Override
    public File getFile() {
        if (file != null) {
            return file;
        }
        file = path.toFile();
        return file;
    }
}
