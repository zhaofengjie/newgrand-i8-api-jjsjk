package com.newgrand.domain.EDB;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * WBS一级模型
 * @Author ChenXiangLu
 * @Date 2020/11/25 15:56
 * @Version 1.0
 */
@Getter
@Setter
public class WBSLevel1Model {

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
     * 二级WBS
     */
    @JsonProperty(value = "ProjectDWInfos")
    private ArrayList<WBSLevel2Model> ProjectDWInfos;
}
