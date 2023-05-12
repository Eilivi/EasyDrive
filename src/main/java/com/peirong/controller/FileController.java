package com.peirong.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peirong.entity.FileEntity;
import com.peirong.service.FileService;
import com.peirong.util.Result;
import com.peirong.util.SnowflakeIdUtils;
import com.peirong.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @Value("${uploadPath}")
    private String uploadPath;

    @PostMapping("/upload")
    public Map<String, Object> fileUpload(MultipartFile file, @RequestParam Long owner, @RequestParam String path) throws IOException {
        //获取文件的前后缀名
        String filename = file.getOriginalFilename();
        //获取文件后缀名
        assert filename != null;
        String[] suffix = filename.split("\\.");
        int suffixIndex = suffix.length - 1;
        String name = UUIDUtils.getUUID() + "." + suffix[suffixIndex];
        file.transferTo(new File(uploadPath + name));

        Double size = (file.getSize() / 1024.0);

        FileEntity info = new FileEntity();
        info.setFilename(filename);
        info.setFilepath(uploadPath + name);
        info.setPath(path);
        info.setOwner(owner);
        info.setSize(size);

        //将UUID设置为主键id
        SnowflakeIdUtils idUtils = new SnowflakeIdUtils(11, 18);
        long id = idUtils.nextId();
        info.setId(id);
        Map<String, Object> map = new HashMap<>();

        if (fileService.save(info)) {
            map.put(Result.CODE, 200);
            map.put(Result.SUCCESS, true);
            map.put(Result.MESSAGE, "上传成功");
            map.put("filepath", uploadPath + name);
        } else {
            map.put(Result.CODE, 510);
            map.put(Result.SUCCESS, false);
            map.put(Result.MESSAGE, "上传失败");
        }
        return map;
    }

    @PostMapping("listAll")
    public List<FileEntity> listAll(@RequestBody FileEntity fileEntity) {
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner", fileEntity.getOwner()).eq("path", fileEntity.getPath());
        return fileService.list(queryWrapper);
    }

    @PostMapping("rename")
    public boolean rename(@RequestBody FileEntity fileEntity) {
        try {
            FileEntity f = fileService.getById(fileEntity.getId());
            if (f == null) {
                return false;
            }
            String path = f.getFilepath();
            File f1 = new File(path);
            if (f1.exists()) {
                String newPath = path.substring(0, path.lastIndexOf("/") + 1) + fileEntity.getFilename();
                File f2 = new File(newPath);
                f1.renameTo(f2);
                f.setFilename(fileEntity.getFilename());
                fileService.updateById(f);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("delete")
    public boolean delete(Long owner, String path, HttpServletResponse response) {
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("filepath", path);
        File file = new File(path);
        try {
            if (file.exists()) {
                file.delete();
                return fileService.remove(queryWrapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/download")
    public void download(Long owner, String path, HttpServletResponse response) throws IOException {
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("filepath", path);
        FileEntity fileEntity = fileService.getOne(queryWrapper);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileEntity.getFilename().getBytes(), "ISO-8859-1"));
        byte[] bytes = new byte[256 * 1024];
        int readCount = 0;
        while ((readCount = inputStream.read(bytes)) != -1) {
            response.getOutputStream().write(bytes, 0, readCount);
        }
        response.getOutputStream().flush();
    }

    @GetMapping("/getImage")
    public void getImage(Long owner, String path, HttpServletResponse response) throws FileNotFoundException {
        File file = new File(path);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[256 * 1024];
            int readCount = 0;
            while ((readCount = inputStream.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0, readCount);
            }
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getPDF")
    public void getPDF(Long owner, String path, HttpServletResponse response) throws IOException {
        File file = new File(path);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            response.setContentType("application/pdf");
            byte[] bytes = new byte[256 * 1024];
            int readCount = 0;
            while ((readCount = inputStream.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0, readCount);
            }
            response.getOutputStream().flush();
        }
    }

    @GetMapping("/share")
    public void share(Long id, String path, HttpServletResponse response) throws IOException {

        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        FileEntity fileEntity = fileService.getOne(queryWrapper);
        String sharePath = fileEntity.getFilepath();
        File file = new File(sharePath);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileEntity.getFilename());
            byte[] bytes = new byte[256 * 1024];
            int readCount = 0;
            while ((readCount = inputStream.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0, readCount);
            }
            response.getOutputStream().flush();
        }
    }

    @GetMapping("search")
    public Map<String, Object> search(String keyword, Integer currentPage, Integer pageSize, String path) {
        QueryWrapper<FileEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyword), "filename", keyword).eq("path", path)
                .orderByDesc("upload_time");
        //分页查询
        Page<FileEntity> page = new Page<>(currentPage, pageSize);
        fileService.page(page, queryWrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("total", page.getTotal());
        map.put("data", page.getRecords());
        return map;
    }

    @GetMapping("folder")
    public boolean folder(Long owner, String path, String filename) {
        try {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(filename);
            fileEntity.setFilepath(filename);
            fileEntity.setOwner(owner);
            fileEntity.setPath(path);
            fileEntity.setSize(0.0);
            fileService.save(fileEntity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}