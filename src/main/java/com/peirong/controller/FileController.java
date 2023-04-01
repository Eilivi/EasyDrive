package com.peirong.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("file")
public class FileController {
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        // TODO: 实现文件上传功能，并返回处理结果
        try {
            String fileName = file.getOriginalFilename();
            String filePath = "/path/to/local/directory" + fileName;
            File dest = new File(filePath);
            file.transferTo(dest);
            String message = "文件上传成功：" + fileName;
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败" + e.getMessage());
        }

    }
    // TODO: 编写其他必要的Controller方法

    @PostMapping("/scan")
    public String findSimilarFiles(FileController fileController) {



        return "0";
    }

}
