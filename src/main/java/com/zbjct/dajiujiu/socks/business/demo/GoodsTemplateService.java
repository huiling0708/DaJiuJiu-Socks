package com.zbjct.dajiujiu.socks.business.demo;

import com.zbjct.dajiujiu.socks.basics.helper.SessionHelper;
import com.zbjct.dajiujiu.socks.basics.utils.CommonUtils;
import com.zbjct.dajiujiu.socks.business.demo.entity.GoodsTemplate;
import com.zbjct.dajiujiu.socks.business.demo.param.SaveGoodsTemplateParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* 商品模板服务
*/
@Service
public class GoodsTemplateService {


    /**
     * 保存商品模板
     *
     * @param param
     * @return
     */
    @Transactional
    public GoodsTemplate saveGoodsTemplate(SaveGoodsTemplateParam param) {

        GoodsTemplate goodsTemplate = CommonUtils.copyProperty(param, GoodsTemplate.class);
        //设置公司id
        goodsTemplate.setCompanyId(SessionHelper.getCompanyId());
        //设置商品使用个数
        goodsTemplate.setGoodsUsedCount(0);

        //保存商品模板
        return goodsTemplate.createWrapper().doSave();
    }
}
