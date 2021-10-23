package com.zbjct.dajiujiu.socks.controller.common;


import com.zbjct.dajiujiu.socks.basics.define.param.SingleParam;
import com.zbjct.dajiujiu.socks.basics.define.vo.ResultVo;
import com.zbjct.dajiujiu.socks.basics.dict.base.DictData;
import com.zbjct.dajiujiu.socks.basics.dict.base.DictHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Api(tags = "平台-Platform")
@RestController
@RequestMapping("platform")
public class PlatformController {

    @GetMapping(value = "load/dict")
    @ApiOperation(value = "加载数据字典")
    public ResultVo<Map<String, DictData>> loadDict() {
        return ResultVo.success(DictHelper.getDict());
    }

    @PostMapping(value = "find/single/dict")
    @ApiOperation(value = "根据字典类型获取单组字典")
    public ResultVo<DictData> loadSingleDict(@RequestBody @Valid SingleParam<String> param) {
        return ResultVo.success(DictHelper.getDict().get(param.getKey()));
    }
}
