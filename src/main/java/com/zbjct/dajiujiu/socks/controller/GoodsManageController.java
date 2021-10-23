package com.zbjct.dajiujiu.socks.controller;

import com.zbjct.dajiujiu.socks.basics.define.param.SingleParam;
import com.zbjct.dajiujiu.socks.basics.define.vo.ResultVo;
import com.zbjct.dajiujiu.socks.business.demo.GoodsTemplateService;
import com.zbjct.dajiujiu.socks.business.demo.entity.GoodsTemplate;
import com.zbjct.dajiujiu.socks.business.demo.param.SaveGoodsTemplateParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import com.zbjct.dajiujiu.socks.business.demo.entity.GoodsInfo;
import com.zbjct.dajiujiu.socks.business.demo.param.UpdateGoodsStateParam;
import com.zbjct.dajiujiu.socks.business.demo.GoodsInfoService;
import com.zbjct.dajiujiu.socks.business.demo.param.AddGoodsInfoParam;

@Api(tags = "商品管理-GoodsInfo")
@RestController
@RequestMapping("/goods/info")
public class GoodsManageController {

    @Autowired
    private GoodsInfoService goodsInfoService;
    @Autowired
    private GoodsTemplateService goodsTemplateService;

    @ApiOperation(value = "新增商品信息")
    @PostMapping(value = "add/goods/info")
    public ResultVo<GoodsInfo> addGoodsInfo(@Valid @RequestBody AddGoodsInfoParam param) {
        return ResultVo.success(goodsInfoService.addGoodsInfo(param));
    }

    @ApiOperation(value = "更新商品状态")
    @PostMapping(value = "update/goods/state")
    public ResultVo<Integer> updateGoodsState(@Valid @RequestBody UpdateGoodsStateParam param) {
        return ResultVo.success(goodsInfoService.updateGoodsState(param));
    }

    @ApiOperation(value = "保存商品模板")
    @PostMapping(value = "save/goods/template")
    public ResultVo<GoodsTemplate> saveGoodsTemplate(@Valid @RequestBody SaveGoodsTemplateParam param) {
        return ResultVo.success(goodsTemplateService.saveGoodsTemplate(param));
    }

    @ApiOperation(value = "删除商品信息")
    @PostMapping(value = "delete/goods/info")
    public ResultVo<Integer> deleteGoodsInfo(@Valid @RequestBody SingleParam<Long> param) {
        return ResultVo.success(goodsInfoService.deleteGoodsInfo(param.getKey()));
    }
}