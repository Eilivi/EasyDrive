package com.peirong.service.Implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peirong.entity.Recycle;
import com.peirong.mapper.RecycleMapper;
import com.peirong.service.RecycleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecycleServiceImpl extends ServiceImpl<RecycleMapper, Recycle> implements RecycleService {
}
