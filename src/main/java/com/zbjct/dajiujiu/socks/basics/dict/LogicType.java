package com.zbjct.dajiujiu.socks.basics.dict;

import com.zbjct.dajiujiu.socks.basics.dict.base.DictDescribe;
import com.zbjct.dajiujiu.socks.basics.dict.base.IDict;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@DictDescribe("逻辑")
public enum LogicType implements IDict {

    FALSE(0, "否", false),
    TRUE(1, "是", true);

    private Integer code;
    private String describe;
    private boolean booleanValue;
}
