package com.peirong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/after")
public class AfterLoginController {
    private static final String UPLOAD_DIR = "/uploads/";
    @PostMapping("/uploadAvatar")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 保存图片到服务器本地
        String fileName = file.getOriginalFilename();
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, bytes);
            // 返回图片的URL
            return ResponseEntity.ok().body("/uploads/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to upload image");
        }
    }
}
