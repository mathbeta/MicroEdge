package com.mathbeta.microedge.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mathbeta.alphaboot.mapper.IMapper;
import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.User;
import com.mathbeta.microedge.mapper.UserMapper;
import com.mathbeta.microedge.service.IUserService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 用户
 * Created by xiuyou.xu on 2023/09/07.
 */
@Slf4j
@Service
public class UserService extends BaseService<User> implements IUserService {
    // use lombok log
    // private static Logger log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Override
    protected IMapper<User> getMapper() {
        return userMapper;
    }
}
