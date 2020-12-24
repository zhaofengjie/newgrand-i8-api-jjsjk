package com.newgrand.secdev.domain.EDB;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ChenXiangLu
 * @Date 2020/12/10 21:43
 * @Version 1.0
 */
@Getter
@Setter
public class SubPackageApplyModel {
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
     * 提交人员编码（从主数据库获取跟PM系统统一)
     */
    @JsonProperty(value = "UserCode")
    private String userCode;
    /**
     * 分包申请表头
     */
    @JsonProperty(value = "SubpackageDetailInfos")
    private ArrayList<SubPackageApplyDetailModel> detais;
}
