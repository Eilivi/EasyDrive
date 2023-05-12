package com.peirong.service.Implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peirong.entity.FileEntity;
import com.peirong.mapper.FileMapper;
import com.peirong.service.FileService;
import com.peirong.util.FileUtils;
import com.peirong.util.SnowflakeIdUtils;
import com.peirong.util.UUIDUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
@Transactional
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {
    @Resource
    private FileMapper fileMapper;

    @Override
    public int uploadFile(MultipartFile multipartFile, FileEntity fileEntity) {
        if (!multipartFile.isEmpty()){

            //将UUID设置为主键id
            SnowflakeIdUtils idUtils = new SnowflakeIdUtils(11,18);
            long id = idUtils.nextId();
            fileEntity.setId(id);
            //System.out.println(file);

            //获取文件的前后缀名
            String filename = multipartFile.getOriginalFilename();
            //System.out.println(filename);
            fileEntity.setFilename(filename);

            //获取文件后缀名
            assert filename != null;
            String[] suffix = filename.split(".");
            //System.out.println(Arrays.toString(suffix));
            int suffixIndex = suffix.length - 1;
            //System.out.println(suffixIndex);

            //随机生成UUID为文件名并保存到数据库
            String name = UUIDUtils.getUUID() + "." + suffix[suffixIndex];
            //System.out.println(name);

            //设置文件上传路径
            String path = "/Users/peirong/file/";
            try {
                //调用工具类上传文件
                String filePath = FileUtils.uploadFile(multipartFile.getBytes(), path, name);
                fileEntity.setFilepath(filePath);
                //使用MybatisPlus自带的新增方法进行插入
                return fileMapper.insert(fileEntity);
            } catch (Exception e){
                return -1;
            }
        }else {
            return 0;
        }
    }
}
