package com.zbjct.dajiujiu.socks.basics.database.define;


import com.zbjct.dajiujiu.socks.basics.utils.CommonUtils;

import java.io.Serializable;

public interface IVo extends Serializable {

    default <T extends IEntity<T>> void copyProperty(T instance) {
        CommonUtils.copyProperty(instance, this);
    }
}
