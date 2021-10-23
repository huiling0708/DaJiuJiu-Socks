package com.zbjct.dajiujiu.socks.basics.define.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel(description = "分页查询公共返回值")
@Data
@NoArgsConstructor
public class PageVo<T> {
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int DEFAULT_PAGE_NUMBER = 1;

    @ApiModelProperty(value = "当前页")
    private int number = DEFAULT_PAGE_NUMBER;

    @ApiModelProperty(value = "每页记录数")
    private int size = DEFAULT_PAGE_SIZE;

    @ApiModelProperty(value = "总记录数")
    private long totalElements = -1;

    @ApiModelProperty(value = "总页数")
    private int totalPages = -1;

    @ApiModelProperty(value = "结果集")
    private List<T> content;

    /**
     * 设置总元素个数
     *
     * @param totalElements
     */
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
        computeTotalPage();
    }

    /**
     * 计算总页数
     */
    protected void computeTotalPage() {
        if (getSize() > 0 && getTotalElements() > -1) {
            this.totalPages = (int) (getTotalElements() % getSize() == 0 ? getTotalElements() / getSize() : getTotalElements() / getSize() + 1);
        }
    }
}
