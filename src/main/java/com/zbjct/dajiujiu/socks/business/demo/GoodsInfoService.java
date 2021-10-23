package com.zbjct.dajiujiu.socks.business.demo;

import com.zbjct.dajiujiu.socks.basics.database.impl.JpaWrapper;
import com.zbjct.dajiujiu.socks.basics.dict.GoodsState;
import com.zbjct.dajiujiu.socks.basics.utils.CommonUtils;
import com.zbjct.dajiujiu.socks.business.demo.entity.GoodsInfo;
import com.zbjct.dajiujiu.socks.business.demo.entity.GoodsTemplate;
import com.zbjct.dajiujiu.socks.business.demo.param.AddGoodsInfoParam;
import com.zbjct.dajiujiu.socks.business.demo.param.UpdateGoodsStateParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品服务
 */
@Service
public class GoodsInfoService {


    /**
     * 新增商品信息
     *
     * @param param
     * @return
     */
    @Transactional
    public GoodsInfo addGoodsInfo(AddGoodsInfoParam param) {
        //验证商品模版
        GoodsTemplate goodsTemplate = JpaWrapper.create(GoodsTemplate.class)
                .where(GoodsTemplate::getTemplateId, param.getTemplateId())
                .presentCompany()
                .doGetOneAndCheckNull();

        GoodsInfo goodsInfo = CommonUtils.copyProperty(param, GoodsInfo.class);
        //设置状态
        goodsInfo.setGoodsState(GoodsState.OFF_THE_SHELF);

        //保存商品信息
        goodsInfo = goodsInfo.createWrapper().presentCompany().doSave();


        //更新商品使用个数
        goodsTemplate.createWrapper()
                .addUpdateValue(GoodsTemplate::getGoodsUsedCount, goodsTemplate.getGoodsUsedCount() + 1)
                .where(GoodsTemplate::getTemplateId)
                .presentCompany().doUpdate();

        return goodsInfo;
    }

    /**
     * 更新商品状态
     *
     * @param param
     * @return
     */
    @Transactional
    public int updateGoodsState(UpdateGoodsStateParam param) {
        //检查商品信息是否存在
        JpaWrapper.create(GoodsInfo.class)
                .where(GoodsInfo::getGoodsId, param.getGoodsId())
                .presentCompany()//设置当前公司ID
                .doCheckNotExists("无效的商品信息");

        //更新
        return JpaWrapper.create(GoodsInfo.class)
                .addUpdateValue(GoodsInfo::getGoodsState, param.getGoodsState())
                .where(GoodsInfo::getGoodsId, param.getGoodsId())
                .presentCompany()
                .doUpdate();
    }

    /**
     * 删除商品信息
     *
     * @return
     */
    @Transactional
    public int deleteGoodsInfo(long goodsId) {
        //检查商品信息是否存在
        JpaWrapper.create(GoodsInfo.class)
                .where(GoodsInfo::getGoodsId, goodsId)
                .presentCompany()
                .doGetOneAndCheckNull()
                .checkNotEquals(GoodsInfo::getGoodsState, GoodsState.ON_THE_SHELF,
                        "商品已上架，无法删除噢～");

        //更新
        return JpaWrapper.create(GoodsInfo.class)
                .where(GoodsInfo::getGoodsId, goodsId)
                .presentCompany()
                .doDelete();
    }


}
