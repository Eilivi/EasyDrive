package com.peirong.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import javax.servlet.http.HttpSession;
import java.io.File;

/**
 * @author Peirong
 */
@RestController
public class FileController {
    @PostMapping(value = "/upload")
    public ResponseEntity<?> uploadFile(String name, @RequestParam("file") CommonsMultipartFile file,
                                        HttpSession session) {
        try {
            String fileName = file.getOriginalFilename();
            String filePath = "" + fileName;
            File dest = new File(filePath);
            String type = fileName.substring(fileName.lastIndexOf("."));
            file.transferTo(dest);
            String message = "文件上传成功：" + fileName;
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败" + e.getMessage());
        }
    }
    @PostMapping("/scan")
    public String findSimilarFiles(FileController fileController) {
        return "0";
    }
}
