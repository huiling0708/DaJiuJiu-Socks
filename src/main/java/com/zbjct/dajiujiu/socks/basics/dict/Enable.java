package com.zbjct.dajiujiu.socks.basics.dict;

import com.zbjct.dajiujiu.socks.basics.dict.base.DictDescribe;
import com.zbjct.dajiujiu.socks.basics.dict.base.IDict;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DictDescribe("启用")
public enum Enable implements IDict<Enable> {
    ENABLE("启用"),
    DISABLE("停用");

    private String describe;

}
