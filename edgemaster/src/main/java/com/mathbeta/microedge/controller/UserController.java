package com.mathbeta.microedge.controller;

import com.mathbeta.alphaboot.controller.BaseController;
import com.mathbeta.alphaboot.service.IService;
import com.mathbeta.microedge.entity.User;
import com.mathbeta.microedge.service.IUserService;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* 用户
*
* Created by xiuyou.xu on 2023/09/07.
*/
@Api(value = "用户", description = "用户"/*, authorizations = {@Authorization(value = "mesoauth", scopes = {@AuthorizationScope(scope = "user", description = "用户")})}*/)
@RestController
@RequestMapping("/user")
public class UserController extends BaseController<User> {
    @Resource
    private IUserService userService;

    @Override
    protected IService<User> getService() {
        return userService;
    }
}
