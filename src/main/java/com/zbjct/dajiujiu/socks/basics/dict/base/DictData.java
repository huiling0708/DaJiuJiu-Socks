package com.zbjct.dajiujiu.socks.basics.dict.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@ApiModel(description = "字典")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictData {

    @ApiModelProperty(value = "字典值")
    private String value;
    @ApiModelProperty(value = "字典值描述")
    private String describe;

    @ApiModelProperty(value = "子集map")
    private Map<String, DictData> childMap;
    @ApiModelProperty(value = "子集list")
    private List<DictData> childList;

    public DictData(IDict<? extends Enum<?>> dict) {
        Enum en = (Enum) dict;
        this.value = en.name();
        this.describe = dict.getDescribe();
    }
}
