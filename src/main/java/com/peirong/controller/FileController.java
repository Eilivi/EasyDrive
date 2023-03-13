package com.peirong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
public class FileController {
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        // TODO: 实现文件上传功能，并返回处理结果
        return ResponseEntity.ok(file);

    }

    // TODO: 编写其他必要的Controller方法

}
