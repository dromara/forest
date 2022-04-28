package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.extensions.DownloadFile;
import com.dtflys.forest.http.ForestResponse;

import java.io.File;
import java.io.InputStream;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-05 22:35
 */
public interface DownloadClient {

    @Get(url = "http://localhost:${port}/download/test-img-{2}.jpg", connectTimeout = 3600 * 1000, readTimeout = 3600 * 1000)
    @DownloadFile(dir = "${dir}", filename = "${filename}")
    File downloadImage(@Var("dir") String dir, @Var("filename") String filename, Integer index, OnProgress onProgress);


    @Get(value = "http://localhost:${port}/download/test-img.jpg", progressStep = 100)
    @DownloadFile(dir = "${dir}")
    ForestResponse<File> downloadFile(@Var("dir") String dir, OnProgress onProgress);


    @Get("http://localhost:${port}/download/test-img.jpg")
    byte[] downloadFileToBytes(OnProgress onProgress);

    @Get("http://localhost:${port}/download/test-img.jpg")
    @DownloadFile(dir = "${dir}")
    File downloadImageFile(@Var("dir") String dir);

    @Get("http://localhost:${port}/download/test-img.jpg")
    InputStream downloadAsInputStream();

    @Get("http://localhost:${port}/download/test-img.jpg")
    ForestResponse downloadAsInputResponse();

}
