package org.dromara.forest.core.test.multipart;

import org.dromara.forest.multipart.ByteArrayMultipart;
import org.dromara.forest.multipart.FileMultipart;
import org.dromara.forest.multipart.FilePathMultipart;
import org.dromara.forest.multipart.InputStreamMultipart;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class TestMultipart extends TestCase {

    public void testByteArrayMultipart() {
        String text = "{\"id\":1,\"direction\":4,\"type\":\"yyy\",\"body\":\"xxx\",\"crc\":1}";
        byte[] byteArray = text.getBytes();
        ByteArrayMultipart byteArrayMultipart = new ByteArrayMultipart();
        byteArrayMultipart.setData(byteArray);
        byteArrayMultipart.setName("file");
        byteArrayMultipart.setFileName("test.json");
        byteArrayMultipart.setContentType("application/json");
        byte[] results = byteArrayMultipart.getBytes();
        assertNotNull(results);
        assertEquals(byteArray.length, results.length);
        String resultText = new String(results);
        assertEquals(text, resultText);
        assertEquals("file", byteArrayMultipart.getName());
        assertEquals("test.json", byteArrayMultipart.getOriginalFileName());
        assertEquals("application/json", byteArrayMultipart.getContentType());
        assertEquals(byteArray.length, byteArrayMultipart.getSize());
    }

    public void testFileMultipart() throws IOException {
        URL url = TestMultipart.class.getClassLoader().getResource("logback.xml");
        File file = new File(url.getFile());
        assertNotNull(file);
        FileMultipart fileMultipart = new FileMultipart();
        fileMultipart.setName("file");
        fileMultipart.setData(file);
        File resultFile = fileMultipart.getFile();
        assertNotNull(resultFile);
        assertEquals(file, resultFile);
        assertEquals("file", fileMultipart.getName());
        assertEquals("logback.xml", fileMultipart.getOriginalFileName());
        long fileSize = FileUtils.sizeOf(file);
        assertEquals(fileSize, fileMultipart.getSize());
        String text = FileUtils.readFileToString(file);
        String resultText = IOUtils.toString(fileMultipart.getInputStream());
        assertEquals(text, resultText);
        byte[] byteArray = fileMultipart.getBytes();
        assertNotNull(byteArray);
        String resultText2 = new String(byteArray);
        assertEquals(text, resultText2);
    }


    public void testFilePathMultipart() throws IOException {
        URL url = TestMultipart.class.getClassLoader().getResource("logback.xml");
        String filePath = url.getFile();
        File file = new File(filePath);
        FilePathMultipart filePathMultipart = new FilePathMultipart();
        filePathMultipart.setName("file");
        filePathMultipart.setData(file.getAbsolutePath());
        File resultFile = filePathMultipart.getFile();
        assertNotNull(resultFile);
        assertEquals(file.getAbsoluteFile(), resultFile.getAbsoluteFile());
        assertEquals("file", filePathMultipart.getName());
        assertEquals("logback.xml", filePathMultipart.getOriginalFileName());
        long fileSize = FileUtils.sizeOf(file);
        assertEquals(fileSize, filePathMultipart.getSize());
        String text = FileUtils.readFileToString(file);
        String resultText = IOUtils.toString(filePathMultipart.getInputStream());
        assertEquals(text, resultText);
        byte[] byteArray = filePathMultipart.getBytes();
        assertNotNull(byteArray);
        String resultText2 = new String(byteArray);
        assertEquals(text, resultText2);
    }


    public void testInputStreamMultipart() throws IOException {
        URL url = TestMultipart.class.getClassLoader().getResource("logback.xml");
        String filePath = url.getFile();
        File file = new File(filePath);
        InputStream in = new FileInputStream(file);
        InputStreamMultipart inputStreamMultipart = new InputStreamMultipart();
        inputStreamMultipart.setName("file");
        inputStreamMultipart.setData(in);
        File resultFile = inputStreamMultipart.getFile();
        inputStreamMultipart.setFileName("logback.xml");
        assertNull(resultFile);
        assertEquals("file", inputStreamMultipart.getName());
        assertEquals("logback.xml", inputStreamMultipart.getOriginalFileName());
        assertEquals(-1, inputStreamMultipart.getSize());
        String text = FileUtils.readFileToString(file);
        String resultText = IOUtils.toString(inputStreamMultipart.getInputStream());
        assertEquals(text, resultText);
    }


    public void testInputStreamMultipart2() throws IOException {
        URL url = TestMultipart.class.getClassLoader().getResource("logback.xml");
        String filePath = url.getFile();
        File file = new File(filePath);
        InputStream in = new FileInputStream(file);
        InputStreamMultipart inputStreamMultipart = new InputStreamMultipart();
        inputStreamMultipart.setData(in);
        File resultFile = inputStreamMultipart.getFile();
        inputStreamMultipart.setFileName("logback.xml");
        assertNull(resultFile);
        assertEquals("logback.xml", inputStreamMultipart.getOriginalFileName());
        assertEquals(-1, inputStreamMultipart.getSize());
        String text = FileUtils.readFileToString(file);
        byte[] byteArray = inputStreamMultipart.getBytes();
        assertNotNull(byteArray);
        String resultText2 = new String(byteArray);
        assertEquals(text, resultText2);
    }

}
