package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * 产值模型
 * @Author ChenXiangLu
 * @Date 2020/11/25 21:12
 * @Version 1.0
 */
@Getter
@Setter
public class ProCountModel {
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
     * 期间
     */
    @JsonProperty(value = "YDDate")
    private String cycle;
    /**
     * 提交人员编码（从主数据库获取跟PM系统统一）
     */
    @JsonProperty(value = "UserCode")
    private String userCode;
    /**
     * 明细
     */
    @JsonProperty(value = "BQItemProductionInfos")
    private ArrayList<ProCountBQModel> bqItemProductionInfos;
    /**
     * 明细
     */
    @JsonProperty(value = "MeasureItemProductionInfos")
    private ArrayList<ProCountBQModel> measureItemProductionInfos;
    /**
     * 明细
     */
    @JsonProperty(value = "OtherItemProductionInfos")
    private ArrayList<ProCountBQModel> otherItemProductionInfos;
}
