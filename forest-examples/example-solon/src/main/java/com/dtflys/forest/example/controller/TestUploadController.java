package com.dtflys.forest.example.controller;

import com.dtflys.forest.example.client.UploadClient;
import com.dtflys.forest.example.service.FileService;
import com.dtflys.forest.example.utils.PathUtil;
import org.noear.solon.Utils;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.core.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class TestUploadController {

    private final static Logger logger = LoggerFactory.getLogger(TestUploadController.class);

    @Inject
    private FileService fileService;

    @Inject("${myfile.uploadPath}")
    private String uploadPath;

    @Inject
    private UploadClient uploadClient;


    @Get
    @Mapping("/hello")
    public String hello() {
        return "hello";
    }

    //处理文件上传的方法
    @Post
    @Mapping("/upload")
    public Map upload(UploadedFile file) throws IOException {
        String webPath = "upload";
        System.out.println("webPath=" + webPath);
        String webFilePath = PathUtil.appendWebPath(webPath, file.getName());
        System.out.println("webFilePath=" + webFilePath);
        String filePath = PathUtil.appendWebPath(uploadPath, webFilePath);
        System.out.println("filePath=" + filePath);
        Map<String, String> result = fileService.uploadReal(filePath, file);
        result.put("webUrl", webFilePath);
        return result;
    }

    //处理文件上传的方法
    @Post
    @Mapping("/upload2")
    public Map upload2(UploadedFile file, String username) throws IOException {
        String webPath = "upload";
        System.out.println("username=" + username);
        System.out.println("webPath=" + webPath);
        String webFilePath = PathUtil.appendWebPath(webPath, file.getName());
        System.out.println("webFilePath=" + webFilePath);
        String filePath = PathUtil.appendWebPath(uploadPath, webFilePath);
        System.out.println("filePath=" + filePath);
        Map<String, String> result = fileService.uploadReal(filePath, file);
        result.put("webUrl", webFilePath);
        return result;
    }

    //处理文件上传的方法
    @Post
    @Mapping("/upload-array")
    public Map uploadList(UploadedFile[] files) throws IOException {
        String webPath = "upload";
        System.out.println("webPath=" + webPath);
        Map<String, Map> resultMap = new LinkedHashMap<>();
        for (UploadedFile file : files) {
            String webFilePath = PathUtil.appendWebPath(webPath, file.getName());
            System.out.println("webFilePath=" + webFilePath);
            String filePath = PathUtil.appendWebPath(uploadPath, webFilePath);
            System.out.println("filePath=" + filePath);
            Map<String, String> result = fileService.uploadReal(filePath, file);
            result.put("webUrl", webFilePath);
            resultMap.put(file.getName(), result);
        }
        return resultMap;
    }


    @Post
    @Mapping("/do-upload-file-path")
    public Map doUploadFilePath() throws IOException {
        URL resource = ResourceUtil.getResource("test-img.jpg");
        String filePath = resource.getPath();

        Map result = uploadClient.upload(filePath, progress -> {
            logger.info("-------------------------------------------------------");
            logger.info("total bytes: " + progress.getTotalBytes());
            logger.info("current bytes: " + progress.getCurrentBytes());
            logger.info("percentage: " + (progress.getRate() * 100) + "%");
        });
        return result;
    }


    @Post
    @Mapping("/do-upload-file")
    public Map doUploadFile() throws IOException {
        URL resource = ResourceUtil.getResource("test-img.jpg");
        File file = new File(resource.getFile());

        Map result = uploadClient.upload(file, progress -> {
            logger.info("-------------------------------------------------------");
            logger.info("total bytes: " + progress.getTotalBytes());
            logger.info("current bytes: " + progress.getCurrentBytes());
            logger.info("percentage: " + (progress.getRate() * 100) + "%");
        });
        return result;
    }

    @Post
    @Mapping("/do-upload-bytes")
    public Map doUploadBytes() throws IOException {
        URL resource = ResourceUtil.getResource("test-img.jpg");

        byte[] buffer = null;

        try(InputStream ins = resource.openStream()) {
            buffer = Utils.transferToBytes(ins);
        }

        Map result = uploadClient.upload(buffer, "test-bytes.jpg");
        return result;
    }


    @Post
    @Mapping("/do-upload-input-stream")
    public Map doUploadInputStream() throws IOException {
        URL resource = ResourceUtil.getResource("test-img.jpg");

        Map result = uploadClient.upload(resource.openStream(), "test-input-stream.jpg");
        return result;
    }

    @Post
    @Mapping("/do-upload-resource")
    public Map doUploadResource() {
        URL resource = ResourceUtil.getResource("test-img.jpg");
        Map result = uploadClient.upload(resource);
        return result;
    }

    @Post
    @Mapping("/do-upload-multipart-file")
    public Map doUploadMultipartFile(UploadedFile multipartFile) {
        Map result = uploadClient.upload(multipartFile, multipartFile.getName(), progress -> {
            logger.info("-------------------------------------------------------");
            logger.info("total bytes: " + progress.getTotalBytes());
            logger.info("current bytes: " + progress.getCurrentBytes());
            logger.info("percentage: " + (progress.getRate() * 100) + "%");
            logger.info("is done: " + progress.isDone());
        });
        return result;
    }

    @Post
    @Mapping("/do-upload-path-list")
    public Map doUploadPathList() throws IOException {
        URL[] resources = new URL[]{
                ResourceUtil.getResource("static/images/test-img.jpg"),
                ResourceUtil.getResource("static/images/test-img2.jpg"),
                ResourceUtil.getResource("static/images/test-img3.jpg")
        };
        List<String> pathList = new LinkedList<>();
        for (int i = 0; i < resources.length; i++) {
            pathList.add(resources[i].getPath());
        }
        Map result = uploadClient.uploadPathList(pathList);
        return result;
    }
}
