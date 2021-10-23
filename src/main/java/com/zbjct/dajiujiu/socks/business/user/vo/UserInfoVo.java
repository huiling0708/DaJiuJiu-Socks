package com.zbjct.dajiujiu.socks.business.user.vo;

import com.zbjct.dajiujiu.socks.basics.database.define.IVo;
import com.zbjct.dajiujiu.socks.basics.database.define.constant.SqlExpression;
import com.zbjct.dajiujiu.socks.basics.dict.Enable;
import com.zbjct.dajiujiu.socks.basics.query.QueryField;
import com.zbjct.dajiujiu.socks.basics.query.QueryPresentCondition;
import com.zbjct.dajiujiu.socks.basics.query.QueryProvide;
import com.zbjct.dajiujiu.socks.business.user.entity.UserInfo;
import com.zbjct.dajiujiu.socks.controller.UserController;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户信息视图")
@QueryProvide(value = "公司用户分页查询", entityType = UserInfo.class, controller = UserController.class)
public class UserInfoVo implements IVo {

    private static final long serialVersionUID = 4623234743064815326L;

    public UserInfoVo(UserInfo userInfo) {
        this.copyProperty(userInfo);
    }

    @ApiModelProperty("用户Id")
    private String userId;

    @QueryField(present = QueryPresentCondition.COMPANY)
    @ApiModelProperty("公司Id")
    private String companyId;

    @QueryField(condition = SqlExpression.LIKE)
    @ApiModelProperty("手机号")
    private String phone;

    @QueryField(condition = SqlExpression.LIKE)
    @ApiModelProperty("用户名称")
    private String name;

    @ApiModelProperty("用户状态")
    private Enable state;

}
