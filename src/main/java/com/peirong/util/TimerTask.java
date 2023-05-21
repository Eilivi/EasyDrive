package com.peirong.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.peirong.entity.Recycle;
import com.peirong.service.RecycleService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@EnableAsync
public class TimerTask {
    @Resource
    private RecycleService recycleService;

    @Scheduled(cron = "0 0 12 * * ?")
    public void run() throws ParseException {
        List<Recycle> list = recycleService.list();
        for (Recycle recycle : list) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(recycle.getUploadTime());
            int days = (int) ((new Date().getTime() - date.getTime()) / (1000 * 3600 * 24));
            if (days >= 15) {
                File file = new File(recycle.getFilepath());
                if (file.exists()) {
                    file.delete();
                    recycleService.remove(new QueryWrapper<Recycle>().eq("id", recycle.getId()));
                }
            }
        }

    }
}
