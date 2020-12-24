package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

/**
 * 分包计划
 * @Author ChenXiangLu
 * @Date 2020/11/25 17:35
 * @Version 1.0
 */
@Getter
@Setter
public class SubPackagePlanModel {
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
     * 提交人员编码（从主数据库获取跟PM系统统一）
     */
    @JsonProperty(value = "UserCode")
    private String userCode;
    /**
     * 明细
     */
    @JsonProperty(value = "FeeItemInfos")
    private ArrayList<SubPackagePlanFeeModel> feeItemInfos;
}
