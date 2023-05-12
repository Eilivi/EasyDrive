package com.peirong.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peirong.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileService extends IService<FileEntity> {
    /**
     * 上传文件
     * @param multipartFile 上传的文件
     * @param fileEntity 上传的文件
     * @return 0 or 1
     */
    int uploadFile(MultipartFile multipartFile, FileEntity fileEntity);
}
