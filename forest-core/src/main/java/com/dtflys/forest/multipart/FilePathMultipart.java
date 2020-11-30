package com.dtflys.forest.multipart;

import com.dtflys.forest.exceptions.ForestFileNotFoundException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathMultipart extends ForestMultipart<String> {

    private String filePath;

    private Path path;

    private File file;

    @Override
    public String getOriginalFileName() {
        if (StringUtils.isNotBlank(fileName)) {
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
    public void setData(String data) {
        this.filePath = data;
        this.path = Paths.get(data).normalize();
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
        if (!file.exists()) {
            throw new ForestFileNotFoundException(filePath);
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
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
