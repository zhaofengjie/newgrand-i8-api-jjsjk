package com.newgrand.secdev.domain.EDB;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * BOQ模型
 * @Author ChenXiangLu
 * @Date 2020/11/25 16:52
 * @Version 1.0
 */
@Getter
@Setter
public class BOQModel {
    /**
     * 主键
     */
    @JsonProperty(value = "ID")
    private String id;
    /**
     *  状态 1.申请 2.变更
     */
    @JsonProperty(value = "ApplyType")
    private String applyType;
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
     * 对方单据链接
     */
    @JsonProperty(value = "Url")
    private String url;
    /**
     * BOQ工程清单
     */
    @JsonProperty(value = "BQItemInfos")
    private ArrayList<BOQBQModel> bQItemInfos;
    /**
     * BOQ单价措施费
     */
    @JsonProperty(value = "MeasureItemInfos")
    private ArrayList<BOQMeasureModel> measureItemInfos;
    /**
     * BOQ其他费用
     */
    @JsonProperty(value = "OtherItemInfos")
    private ArrayList<BOQOtherModel> otherItemInfos;
    /**
     * BOQ规费与税金
     */
    @JsonProperty(value = "FeeItemInfos")
    private ArrayList<BOQFeeModel> feeItemInfos;


}
