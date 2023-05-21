package com.peirong.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peirong.entity.*;
import com.peirong.mapper.FileMapper;
import com.peirong.service.FileService;
import com.peirong.service.RecycleService;
import com.peirong.service.ResponseService;
import com.peirong.service.ShareService;
import com.peirong.util.UUIDUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("file")
public class FileController {

    @Resource
    private FileService fileService;
    @Value("${uploadPath}")
    private String uploadPath;
    @Resource
    private HttpServletRequest request;
    @Resource
    private FileMapper fileMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RecycleService recycleService;
    @Resource
    private ShareService shareService;

    @PostMapping("search")
    public Map<String, Object> search(@RequestBody Map<String, String> map) {
        String keyword = map.get("keyword");
        Account account = (Account) redisTemplate.opsForValue().get("account_" + map.get("id"));
        System.out.println(map.get("id"));
        assert account != null;
        Long id = account.getId();
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyword), "filename", keyword).eq("folder", map.get("path")).eq("uid", id)
                .orderByDesc("upload_time");

        long currentPage = Long.parseLong(map.get("currentPage"));
        long pageSize = Long.parseLong(map.get("pageSize"));
        Page<Files> page = new Page<>(currentPage, pageSize);

        fileService.page(page, queryWrapper);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("total", page.getTotal());
        map2.put("data", page.getRecords());
        return map2;

    }

    @PostMapping("upload")
    public Map<String, Object> fileUpload(MultipartFile file, String path, Long id) throws IOException {
        Account account = (Account) redisTemplate.opsForValue().get("account_" + id);
        assert account != null;
        String filename = file.getOriginalFilename();
        assert filename != null;
        String[] suffix = filename.split("\\.");
        int suffixIndex = suffix.length - 1;
        String name = UUIDUtils.getUUID() + "." + suffix[suffixIndex];
        Double size = (file.getSize() / 1024.0);
        Files files = new Files();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        files.setUpload_time(format);
        files.setUid(id);
        files.setFilename(filename);
        files.setFilepath(uploadPath + name);
        files.setFilesize(size);
        System.out.println(path);
        files.setFolder(path);
        Map<String, Object> map = new HashMap<>();
        if (fileService.save(files)) {
            file.transferTo(new File(uploadPath + name));
            map.put(ResponseService.CODE, 200);
            map.put(ResponseService.SUCCESS, true);
            map.put(ResponseService.MESSAGE, "上传成功");
            map.put("filepath", uploadPath + name);
        } else {
            map.put(ResponseService.CODE, 510);
            map.put(ResponseService.SUCCESS, false);
            map.put(ResponseService.MESSAGE, "上传失败");
        }
        return map;
    }

    @GetMapping("download")
    public void download(String path, HttpServletResponse response) throws IOException {
        File file = new File(path);
        System.out.println(path);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("filepath", path);
            Files files = fileService.getOne(queryWrapper);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(files.getFilename().getBytes(), StandardCharsets.ISO_8859_1));
            byte[] bytes = new byte[256 * 1024];
            int readCount = 0;
            while ((readCount = inputStream.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0, readCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.getOutputStream().flush();
    }

    @GetMapping("delete")
    public boolean delete(String id, HttpServletResponse response) {
        QueryWrapper<Recycle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Recycle recycle = recycleService.getOne(queryWrapper);
        String path = recycle.getFilepath();
        File file = new File(path);
        try {
            if (file.exists()) {
                file.delete();
                return recycleService.remove(queryWrapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("remove")
    public boolean remove(String id, HttpServletResponse response) {
        String waitToDelete = "/Users/peirong/recycle/";
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Files files = fileService.getOne(queryWrapper);
        String path = files.getFilepath();
        File file = new File(path);
        Recycle recycle = new Recycle();
        try {
            if (file.exists()) {
                recycle.setId(files.getId());
                recycle.setUid(files.getUid());
                recycle.setFilename(files.getFilename());
                recycle.setFilesize(files.getFilesize());
                recycle.setFilepath(waitToDelete + files.getFilepath().substring(files.getFilepath().lastIndexOf("/") + 1));
                recycle.setFolder(files.getFolder());
                file.renameTo(new File(waitToDelete + file.getName()));
                fileService.remove(queryWrapper);
                return recycleService.save(recycle);
            } else if (path.equals("is_a_folder")) {
                QueryWrapper<Files> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("folder", files.getFolder() + files.getFilename()).eq("uid", files.getUid());
                List<Files> filesList = fileService.list(queryWrapper1);
                if (filesList.size() != 0) {
                    for (Files files1 : filesList) {
                        File file1 = new File(files1.getFilepath());
                        recycle.setId(files1.getId());
                        recycle.setUid(files1.getUid());
                        recycle.setFilename(files1.getFilename());
                        recycle.setFilesize(files1.getFilesize());
                        recycle.setFilepath(waitToDelete + files1.getFilepath().substring(files1.getFilepath().lastIndexOf("/") + 1));
                        recycle.setFolder("/");
                        recycleService.save(recycle);
                        file1.renameTo(new File(waitToDelete + file1.getName()));
                    }
                    fileService.remove(queryWrapper1);
                }
                return fileService.remove(queryWrapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("rename")
    public RestResponse<String> rename(@RequestBody Map<String, String> map) {
        String id = map.get("id");
        String filename = map.get("filename");
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Files files = fileService.getOne(queryWrapper);
        String oldFilename = files.getFilename();
        String path = files.getFilepath();
        if (path.equals("is_a_folder")) {
            files.setFilename(filename + "/");
        } else {
            String[] suffix = oldFilename.split("\\.");
            int suffixIndex = suffix.length - 1;
            files.setFilename(filename + "." + suffix[suffixIndex]);
        }
        if (fileService.updateById(files)) {
            return RestResponse.success("重命名成功");
        } else
            return RestResponse.failure(401, "重命名失败");
    }

    @PostMapping("folder")
    public boolean folder(@RequestBody Map<String, String> map) {
        Account account = (Account) redisTemplate.opsForValue().get("account_" + map.get("id"));
        assert account != null;
        try {
            Files files = new Files();
            files.setUid(account.getId());
            files.setFilename(map.get("filename") + "/");
            files.setFolder(map.get("path"));
            fileService.save(files);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("getFile")
    public void getImage(String path, HttpServletResponse response) throws FileNotFoundException {
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

    @GetMapping("getPDF")
    public void getPDF(String path, HttpServletResponse response) throws IOException {
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

    @GetMapping("scan")
    public List<Files> scan(String id) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", id);
        List<Files> filesList = fileService.list(queryWrapper);

        FileUtils fileUtils = new FileUtils();

        for (Files files : filesList) {
            File file = new File(files.getFilepath());

        }

        return new ArrayList<>();
        //return fileService.list(queryWrapper);
    }

    @GetMapping("share")
    public String share(String id) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Files files = fileService.getOne(queryWrapper);
        Share share = new Share();
        try {
            share.setUid(files.getUid());
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 60 * 60 * 24 * 7);
            SimpleDateFormat expireTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            share.setExpireTime(expireTime.format(date));
            share.setFileId(files.getId());
            String shareId = UUID.randomUUID().toString().replace("-", "");
            share.setId(shareId);
            shareService.save(share);
            return shareId;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("getShare")
    public String getShare(String id, HttpServletResponse response) throws ParseException, IOException {
        QueryWrapper<Share> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Share share = shareService.getOne(queryWrapper);
        if (share == null) {
            return "error";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (sdf.parse(share.getExpireTime()).compareTo(new Date()) < 0) {
            shareService.remove(queryWrapper);
            return "error";
        }
        try {
            Files files = fileService.getById(share.getFileId());
            File file = new File(files.getFilepath());
            FileInputStream inputStream = new FileInputStream(file);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(files.getFilename().getBytes(), StandardCharsets.ISO_8859_1));
            byte[] bytes = new byte[256 * 1024];
            int readCount = 0;
            while ((readCount = inputStream.read(bytes)) != -1) {
                response.getOutputStream().write(bytes, 0, readCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        response.getOutputStream().flush();
        return "success";

    }

    @GetMapping("recycle")
    public List<Recycle> recycle(String id) {
        QueryWrapper<Recycle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", id);
        return recycleService.list(queryWrapper);
    }

    @GetMapping("restore")
    public boolean restore(String id) {
        QueryWrapper<Recycle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        Recycle recycle = recycleService.getOne(queryWrapper);
        Files files = new Files();
        File file = new File(recycle.getFilepath());
        if (file.exists()) {
            files.setId(recycle.getId());
            files.setUid(recycle.getUid());
            files.setFilename(recycle.getFilename());
            files.setFilesize(recycle.getFilesize());
            files.setFilepath(uploadPath + recycle.getFilepath().substring(recycle.getFilepath().lastIndexOf("/") + 1));
            //回收站的文件统一放回根路径
            files.setFolder("/");
            if (fileService.save(files)) {
                file.renameTo(new File(uploadPath + file.getName()));
                return recycleService.remove(queryWrapper);
            }
        }
        return false;
    }
}
