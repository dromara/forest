package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.extensions.DownloadFile;

import java.io.File;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-05 22:35
 */
public interface DownloadClient {

    @Request(url = "https://www.baidu.com/img/PCfb_5bf082d29588c07f842ccde3f97243ea.png")
    @DownloadFile(dir = "${dir}", filename = "${filename}")
    File downloadImage(@DataVariable("dir") String dir, @DataVariable("filename") String filename, OnProgress onProgress);


    @Request(url = "http://forspeed.onlinedown.net/down/QQliveSetup_20_731.exe")
    @DownloadFile(dir = "${dir}", filename = "${filename}")
    File downloadFile(@DataVariable("dir") String dir, @DataVariable("filename") String filename, OnProgress onProgress);

}
