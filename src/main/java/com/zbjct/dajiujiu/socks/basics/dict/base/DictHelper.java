package com.zbjct.dajiujiu.socks.basics.dict.base;


import com.zbjct.dajiujiu.socks.basics.constant.Constant;
import com.zbjct.dajiujiu.socks.basics.constant.ResultCode;
import com.zbjct.dajiujiu.socks.basics.exception.PlatformException;
import com.zbjct.dajiujiu.socks.basics.utils.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 字段帮助
 */
public class DictHelper {

    private final static Map<String, DictData> DICT_CACHE = new HashMap<>(20);

    public static void init() {
        Set<Class<? extends IDict>> load = ClassUtils.load(Constant.DICT_PRE_FIX,
                IDict.class);
        load.forEach(i -> {
            String simpleName = i.getSimpleName();
            DictDescribe ann = i.getAnnotation(DictDescribe.class);
            if (StringUtils.isBlank(simpleName) || ann == null) {
                return;
            }
            Map<String, DictData> childMap = new HashMap<>();
            List<DictData> childList = new ArrayList<>();
            IDict[] enumConstants = i.getEnumConstants();

            for (IDict dict : enumConstants) {
                DictData child = new DictData(dict);
                childList.add(child);
                childMap.put(child.getValue(), child);
            }
            DictData parent = new DictData(simpleName, ann.value(), childMap, childList);
            DICT_CACHE.put(simpleName, parent);
        });
    }

    public static Map<String, DictData> getDict() {
        return DICT_CACHE;
    }

    public static void checkDictType(String type) {
        if (!DICT_CACHE.containsKey(type)) {
            throw new PlatformException(ResultCode.D00000,"无效的字典类型[%s]",type);
        }
    }

    public static void getValue(String type,String value){
        DictData dictData = DICT_CACHE.get(type);
        if (dictData==null){
            throw new PlatformException(ResultCode.D00000,"无效的字典类型[%s]",type);
        }
        DictData child = dictData.getChildMap().get(value);
        if (child==null){
            throw new PlatformException(ResultCode.D00000,"无效的字典值[%s]-[%s]",type,value);
        }
    }
}
