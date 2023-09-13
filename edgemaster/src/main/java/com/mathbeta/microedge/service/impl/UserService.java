package com.mathbeta.microedge.service.impl;


import com.mathbeta.alphaboot.service.impl.BaseService;
import com.mathbeta.microedge.entity.User;
import com.mathbeta.microedge.mapper.UserMapper;
import com.mathbeta.microedge.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户
 *
 * @author xuxiuyou
 * @date 2023/09/07
 */
@Slf4j
@Service
public class UserService extends BaseService<User> implements IUserService {
    @Resource
    private UserMapper userMapper;

    @Override
    protected UserMapper getMapper() {
        return userMapper;
    }
}
