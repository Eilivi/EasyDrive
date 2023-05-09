package com.peirong.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peirong.entity.File;
import com.peirong.service.FileService;
import com.peirong.util.FileDownloadUtils;
import com.peirong.util.Result;
import com.peirong.util.SnowflakeIdUtils;
import com.peirong.util.UUIDUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
    @ApiOperation("文件上传")
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传成功"),
            @ApiResponse(code = 510, message = "上传失败")
    })

    @PostMapping("/upload")
    public Map fileUpload(MultipartFile file, @RequestParam Long owner, @RequestParam String path) throws IOException {
        //获取文件的前后缀名
        String filename = file.getOriginalFilename();
        //获取文件后缀名
        assert filename != null;
        String[] suffix = filename.split("\\.");
        int suffixIndex = suffix.length - 1;
        String name = UUIDUtils.getUUID() + "." + suffix[suffixIndex];
        file.transferTo(new java.io.File(uploadPath + name));

        Double size = (file.getSize() / 1024.0);

        File info = new File();
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
        } else {
            map.put(Result.CODE, 510);
            map.put(Result.SUCCESS, false);
            map.put(Result.MESSAGE, "上传失败");
        }
        return map;
    }

    @ApiOperation("文件下载")
    @ApiResponses({
            @ApiResponse(code = 200, message = "下载成功"),
            @ApiResponse(code = 510, message = "下载失败"),
    })
    @ApiImplicitParam(name = "id", value = "文件主键id", dataType = "int",
            example = "1103674859480985600", required = true)
    @GetMapping("/fileDownload")
    public @ResponseBody
    Map<String, Object> fileDownload(final HttpServletResponse response, Long id) throws Exception {
        Map<String, Object> map = new HashMap<>();
        //通过Mybatis-plus自带的方法根据id查询文件详情
        File file = fileService.getById(id);
        //获取文件路径
        String filePath = file.getFilepath();
        //获取文件名
        String fileName = file.getFilename();
        FileDownloadUtils downloadUtils = new FileDownloadUtils();
        boolean res = downloadUtils.download(response, filePath, fileName);
        if (res) {
            map.put("code", 200);
            map.put("success", true);
            map.put("message", "下载成功！");
        } else {
            map.put("code", 510);
            map.put("success", false);
            map.put("message", "下载失败！");
        }
        return map;
    }

    @PostMapping("listAll")
    public List<File> listAll(@RequestBody File file) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner", file.getOwner()).eq("path", file.getPath());
        return fileService.list(queryWrapper);
    }

    @PostMapping("rename")
    public boolean rename(@RequestBody File file) {
        try {
            File f = fileService.getById(file.getId());
            if (f == null) {
                return false;
            }
            String path = f.getFilepath();
            java.io.File f1 = new java.io.File(path);
            if (f1.exists()) {
                String newPath = path.substring(0, path.lastIndexOf("/") + 1) + file.getFilename();
                java.io.File f2 = new java.io.File(newPath);
                f1.renameTo(f2);
                f.setFilename(file.getFilename());
                fileService.updateById(f);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("delete")
    public boolean delete(Long owner, String path, HttpServletResponse response) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("filepath", path);
        fileService.remove(queryWrapper);
        java.io.File file2 = new java.io.File(path);
        if (file2.exists()) {
            return file2.delete();
        } else {
            return false;
        }
    }

    @GetMapping("/download")
    public void download(Long owner, String path, HttpServletResponse response) throws IOException {
        java.io.File file2 = new java.io.File(path);
        java.io.FileInputStream inputStream = new java.io.FileInputStream(file2);
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("filepath", path);
        File file = fileService.getOne(queryWrapper);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getFilename().getBytes(),"ISO-8859-1"));
        byte[] bytes = new byte[2048 * 1024];
        int readCount = 0;
        while ((readCount = inputStream.read(bytes)) != -1) {
            response.getOutputStream().write(bytes, 0, readCount);
        }
        response.getOutputStream().flush();
    }

    @GetMapping("/getImage")
    public void getImage(Long owner, String path, HttpServletResponse response) throws IOException {
        java.io.File file2 = new java.io.File(path);
        java.io.FileInputStream inputStream = new java.io.FileInputStream(file2);
        byte[] bytes = new byte[2048 * 1024];
        int readCount = 0;
        while ((readCount = inputStream.read(bytes)) != -1) {
            response.getOutputStream().write(bytes, 0, readCount);
        }
        response.getOutputStream().flush();
    }

    @GetMapping("/getPDF")
    public void getPDF(Long owner, String path, HttpServletResponse response) throws IOException {
        java.io.File file2 = new java.io.File(path);
        java.io.FileInputStream inputStream = new java.io.FileInputStream(file2);
        response.setContentType("application/pdf");
        byte[] bytes = new byte[2048 * 1024];
        int readCount = 0;
        while ((readCount = inputStream.read(bytes)) != -1) {
            response.getOutputStream().write(bytes, 0, readCount);
        }
        response.getOutputStream().flush();
    }

    @GetMapping("/share")
    public void share(Long id, String path, HttpServletResponse response) {
        try {
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            File file = fileService.getOne(queryWrapper);
            String sharePath = file.getFilepath();
            java.io.File file2 = new java.io.File(sharePath);
            java.io.FileInputStream inputStream = new java.io.FileInputStream(file2);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + file.getFilename());
            byte[] bytes = new byte[2048 * 1024];
            int readCount = 0;
            while ((readCount = inputStream.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0, readCount);
            }
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}