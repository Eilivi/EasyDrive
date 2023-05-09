package com.peirong.service.Implement;

import com.peirong.service.FileScannerService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;



/**
 * @author Peirong
 */
public class FileScannerServiceImpl implements FileScannerService {

    public List<File> findSimilarFiles(MultipartFile uploadedFile) {
        // TODO: 实现查找相似文件的逻辑，返回重复或相似文件的列表
        /**
         * uploadedFile.getBytes()：获取上传文件的字节数组，用于比较文件内容。
         * Files.walk()：遍历文件系统中的所有文件，用于查找已存在的文件。
         * 文件比较算法：例如MD5、SHA等哈希算法，或者相似度计算算法，如余弦相似度等。*/
        return findSimilarFiles(uploadedFile);

    }

    // TODO: 编写其他必要的服务方法
}
