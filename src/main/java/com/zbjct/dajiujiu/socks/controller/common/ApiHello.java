package com.zbjct.dajiujiu.socks.controller.common;

import com.zbjct.dajiujiu.socks.basics.define.param.SingleParam;
import com.zbjct.dajiujiu.socks.basics.define.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "连接测试-Hello")
@RestController
public class ApiHello {

    @ResponseBody
    @GetMapping(value = "hello")
    @ApiOperation(value = "GET请求测试")
    public String helloGet() {
        return "Hello World!";
    }

    @ResponseBody
    @PostMapping(value = "hello")
    @ApiOperation(value = "POST请求测试")
    public ResultVo<String> helloPost(@RequestBody SingleParam<String> param) {
        return ResultVo.success(param.getKey());
    }

}
