package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * WBS 模型
 * @Author ChenXiangLu
 * @Date 2020/11/25 15:55
 * @Version 1.0
 */
@Getter
@Setter
public class WBSModel {
    /**
     * 主键
     */
    @JsonProperty(value = "ID")
    private String id;
    /**
     * 项目编码
     */
    @JsonProperty(value = "Code")
    private String code;
    /**
     * 项目名称
     */
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 经济库项目查看地址
     */
    @JsonProperty(value = "Url")
    private String url;
    /**
     * 提交人员编码（从主数据库获取跟PM系统统一）
     */
    @JsonProperty(value = "UserCode")
    private String userCode;
    /**
     * 一级WBS
     */
    @JsonProperty(value = "ProjectDXInfos")
    private ArrayList<WBSLevel1Model> projectDXInfos;
}
