package com.zbjct.dajiujiu.socks.basics.validate.param;

import com.zbjct.dajiujiu.socks.basics.database.define.IEntity;
import com.zbjct.dajiujiu.socks.business.demo.entity.GoodsInfo;
import com.zbjct.dajiujiu.socks.business.user.entity.CompanyInfo;
import com.zbjct.dajiujiu.socks.business.user.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证类型
 */
@AllArgsConstructor
@Getter
public enum ValidateType {
    ALL("公共校验", null, null),
    USER_PHONE("手机号", UserInfo.class, IEntity.field(UserInfo::getPhone)),
    COMPANY_NAME("公司名称", "注册公司时使用", CompanyInfo.class, IEntity.field(CompanyInfo::getName)),
    GOODS_CODE("商品代码", GoodsInfo.class, IEntity.field(GoodsInfo::getGoodsCode)),
    ;


    private String fieldDescribe;//字段描述
    private String purpose;//用途说明
    private Class<? extends IEntity> entityClassType;//指向的实体类 即数据表
    private IEntity field;//指向实体类的字段

    ValidateType(String fieldDescribe, Class<? extends IEntity> entityClassType, IEntity field) {
        this.fieldDescribe = fieldDescribe;
        this.purpose = "";
        this.entityClassType = entityClassType;
        this.field = field;
    }

    private final static List<String> types;

    static {
        types = new ArrayList<>();
        for (ValidateType type : ValidateType.values()) {
            StringBuilder sub = new StringBuilder();
            handle(sub, type.name(), 20);
            handle(sub, type.getFieldDescribe(), 10);
            sub.append(type.getPurpose());
            types.add(sub.toString());
        }
        types.remove(0);
    }

    private static void handle(StringBuilder sub, String word, int limit) {
        sub.append(word);
        int i = limit - word.length();
        while (i > 0) {
            sub.append(" ");
            i--;
        }
    }

    public static List<String> getValidateTypeList() {
        return types;
    }
}
