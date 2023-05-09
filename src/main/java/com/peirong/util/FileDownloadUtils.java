package com.peirong.util;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

public class FileDownloadUtils {

    public boolean download(final HttpServletResponse response,
                            String filePath,
                            String fileName) throws Exception {
        //获得文件
        File file = new File(filePath);

        //清空缓冲区，状态码和响应头（header）
        response.reset();
        //设置ContentType，响应内容为二进制数据流，文件内容编码为UTF-8
        response.setContentType("application/octet-stream;charset=utf8");
        //设置默认的文件名并设置文件名的编码
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName + ";filename*=utf-8''" + URLEncoder.encode(fileName, "utf-8"));

        //文件下载
        byte[] buffer = new byte[10240];
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            //获取字节流
            OutputStream outputStream = response.getOutputStream();
            int i = bufferedInputStream.read(buffer);
            while (i != -1) {
                outputStream.write(buffer, 0, i);
                i = bufferedInputStream.read(buffer);
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
