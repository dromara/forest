package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.extensions.DownloadFile;
import com.dtflys.forest.http.ForestResponse;

import java.io.File;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-05 22:35
 */
public interface DownloadClient {

    @Get("https://www.baidu.com/img/PCfb_5bf082d29588c07f842ccde3f97243ea.png")
    @DownloadFile(dir = "${dir}", filename = "${filename}")
    File downloadImage(@Var("dir") String dir, @Var("filename") String filename, OnProgress onProgress);


    @Get(value = "http://forspeed.onlinedown.net/down/QQliveSetup_20_731.exe", progressStep = 100)
    @DownloadFile(dir = "${dir}")
    ForestResponse<File> downloadFile(@Var("dir") String dir, OnProgress onProgress);


    @Get("http://forspeed.onlinedown.net/down/QQliveSetup_20_731.exe")
    byte[] downloadFileToBytes(OnProgress onProgress);

    @Get("http://localhost:${port}/download/test-img.jpg")
    @DownloadFile(dir = "${dir}")
    File downloadImageFile(@Var("dir") String dir);

}
