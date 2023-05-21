package com.peirong.service.Implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peirong.entity.Share;
import com.peirong.mapper.ShareMapper;
import com.peirong.service.ShareService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {
}
