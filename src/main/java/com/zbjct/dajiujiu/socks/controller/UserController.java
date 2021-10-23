package com.zbjct.dajiujiu.socks.controller;

import com.zbjct.dajiujiu.socks.basics.define.vo.ResultVo;
import com.zbjct.dajiujiu.socks.business.user.CompanyService;
import com.zbjct.dajiujiu.socks.business.user.UserService;
import com.zbjct.dajiujiu.socks.business.user.entity.UserInfo;
import com.zbjct.dajiujiu.socks.business.user.param.LoginParam;
import com.zbjct.dajiujiu.socks.business.user.param.RegCompanyParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = " 用户服务-User")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;


    @ApiOperation(value = " 登陆")
    @PostMapping(value = "login")
    public ResultVo<UserInfo> login(@Valid @RequestBody LoginParam param) {
        return ResultVo.success(userService.login(param));
    }

    @ApiOperation(value = " 公司注册")
    @PostMapping(value = "reg/company")
    public ResultVo regCompany(@Valid @RequestBody RegCompanyParam param) {
        companyService.regCompany(param);
        return ResultVo.success();
    }
}