package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 *  WBS二级模型
 * @Author ChenXiangLu
 * @Date 2020/11/25 15:57
 * @Version 1.0
 */
@Getter
@Setter
public class WBSLevel2Model {
    /**
     * 主键
     */
    @JsonProperty(value = "ID")
    private String id;
    /**
     * 单项名称
     */
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 状态：0. 未修改 1.修改 2.新增
     */
    @JsonProperty(value = "Status")
    private String status;
    /**
     * 三级WBS_清单
     */
    @JsonProperty(value = "BQItemFBInfos")
    private ArrayList<WBSLevel3Model> bqItemFBInfos;
    /**
     * 三级WBS_措施
     */
    @JsonProperty(value = "MeasureFBInfos")
    private ArrayList<WBSLevel3Model> measureFBInfos;
}
