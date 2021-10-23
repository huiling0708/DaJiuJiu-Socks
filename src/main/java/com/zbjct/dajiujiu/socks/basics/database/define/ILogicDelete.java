package com.zbjct.dajiujiu.socks.basics.database.define;

/**
 * 需要支持逻辑删除的实体类实现此接口
 *
 */
public interface ILogicDelete {

    int TRUE = 1;
    int FALSE = 0;
    String LOGIC_DELETE_FIELD_NAME = "deleteFlag";//逻辑删除字段名称


// 复制下面的属性到实体类中

//    @ApiModelProperty(value = "删除标识", hidden = true)
//    @Column(nullable = false)
//    private int deleteFlag;

    int getDeleteFlag();

    void setDeleteFlag(int deleteFlag);
}
