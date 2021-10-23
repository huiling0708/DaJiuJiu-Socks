package com.zbjct.dajiujiu.socks.controller.common;

import com.zbjct.dajiujiu.socks.basics.define.vo.ResultVo;
import com.zbjct.dajiujiu.socks.basics.validate.param.EffectiveParam;
import com.zbjct.dajiujiu.socks.basics.validate.param.UniqueParam;
import com.zbjct.dajiujiu.socks.basics.validate.param.ValidateType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "验证-Validate")
@RestController
@RequestMapping("validate")
public class ValidateController {


    @ApiOperation(value = "验证值是否已存在 存在则返回错误")
    @PostMapping(value = "exist")
    public ResultVo exist(@RequestBody @Valid UniqueParam param) {
        return ResultVo.success("验证通过!");
    }

    @ApiOperation(value = "验证值是否有效 不存在则返回错误")
    @PostMapping(value = "effective")
    public ResultVo effective(@RequestBody @Valid EffectiveParam param) {
        return ResultVo.success("验证通过!");
    }

    @ApiOperation(value = "获取验证类型")
    @GetMapping(value = "type")
    public List<String> getValidateType() {
        return ValidateType.getValidateTypeList();
    }
}
