package com.zbjct.dajiujiu.socks.basics.dict.base;

/**
 * 字典基础
 */
public interface IDict<E extends Enum<E>> {

    /**
     * 描述
     *
     * @return 描述
     */
    String getDescribe();

    static <E extends Enum<E> & IDict> E findByName(String enumName, Class<E> clazz) {
        return Enum.valueOf(clazz, enumName);
    }
}
