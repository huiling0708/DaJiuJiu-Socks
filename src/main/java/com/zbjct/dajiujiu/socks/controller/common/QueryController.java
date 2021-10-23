package com.zbjct.dajiujiu.socks.controller.common;

import com.zbjct.dajiujiu.socks.basics.define.param.PageParam;
import com.zbjct.dajiujiu.socks.basics.define.vo.PageVo;
import com.zbjct.dajiujiu.socks.basics.define.vo.ResultVo;
import com.zbjct.dajiujiu.socks.basics.query.processor.QueryExecuteProcessor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Map;

@Api(tags = "查询")
@RestController
@RequestMapping("query")
@ApiIgnore
public class QueryController {

    @Autowired
    private QueryExecuteProcessor processor;

    @ResponseBody
    @ApiOperation(value = "分页查询",hidden = true)
    @PostMapping(value = "page")
    public ResultVo<PageVo> page(@RequestBody @Valid PageParam<Map<String, Object>> pageParam) {
        return ResultVo.success(processor.executePage(pageParam));
    }

    @ApiOperation(value = "列表查询",hidden = true)
    @PostMapping(value = "list")
    public ResultVo list(@RequestBody Map<String, Object> param) {
        return ResultVo.success(processor.executeList(param));
    }

    @ApiOperation(value = "单条查询",hidden = true)
    @PostMapping(value = "single")
    public ResultVo single(@RequestBody Map<String, Object> param) {
        return ResultVo.success(processor.executeSingle(param));
    }

}
