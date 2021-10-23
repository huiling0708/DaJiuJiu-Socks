package com.zbjct.dajiujiu.socks.basics.dict;

import com.zbjct.dajiujiu.socks.basics.dict.base.DictDescribe;
import com.zbjct.dajiujiu.socks.basics.dict.base.IDict;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DictDescribe("商品状态")
public enum GoodsState implements IDict<GoodsState> {
    ON_THE_SHELF("上架"),
    OFF_THE_SHELF("下架"),
    DEACTIVATE("停用"),
    ;

    private String describe;

}