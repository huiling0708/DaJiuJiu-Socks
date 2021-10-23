package com.zbjct.dajiujiu.socks.basics.query;


import com.zbjct.dajiujiu.socks.basics.database.define.IVo;

/**
 * 查询 其它加载
 */
public interface IQueryOtherLoad<T> extends IVo {

    /**
     * 加载
     *
     * @param param 参数类
     */
    void load(T param);
}
