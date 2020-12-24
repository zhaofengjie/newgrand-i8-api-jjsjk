package com.newgrand.domain.EDB;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 分包申请明细
 * @Author ChenXiangLu
 * @Date 2020/12/11 9:31
 * @Version 1.0
 */
@Getter
@Setter
public class SubPackageApplyDetailFeeModel {
    /**
     * 主键
     */
    @JsonProperty(value = "ID")
    private String id;
    /**
     * 费用项编码
     */
    @JsonProperty(value = "Code")
    private String code;
    /**
     * 费用项名称
     */
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 费用项特征
     */
    @JsonProperty(value = "Spec")
    private String spec;
    /**
     * 费用项单位
     */
    @JsonProperty(value = "Unit")
    private String unit;
    /**
     * 费用项工程量
     */
    @JsonProperty(value = "Quantity")
    private String quantity;
    /**
     * 费用项含税单价
     */
    @JsonProperty(value = "TaxRate")
    private String taxRate;
    /**
     * 费用项除税单价
     */
    @JsonProperty(value = "NoTaxRate")
    private String noTaxRate;
    /**
     * 费用项含税合价
     */
    @JsonProperty(value = "TaxTotal")
    private String taxTotal;
    /**
     * 费用项除税合价
     */
    @JsonProperty(value = "NoTaxTotal")
    private String noTaxTotal;
    /**
     * 税金单价
     */
    @JsonProperty(value = "SjRate")
    private String sjRate;
    /**
     * 税金合价
     */
    @JsonProperty(value = "SjTotal")
    private String sjTotal;
    /**
     * 费用项对应科目编码
     */
    @JsonProperty(value = "CourseCode")
    private String courseCode;
    /**
     * 说明见备注
     */
    @JsonProperty(value = "FeeType")
    private String feeType;
    /**
     * 物料编码
     */
    @JsonProperty(value = "MaterialCode")
    private String materialCode;
    /**
     * 人工费含税单价
     */
    @JsonProperty(value = "LaborTaxRate")
    private String laborTaxRate;
    /**
     * 人工费除税单价
     */
    @JsonProperty(value = "LaborNoTaxRate")
    private String laborNoTaxRate;
    /**
     * 人工费含税合价
     */
    @JsonProperty(value = "LaborTaxTotal")
    private String laborTaxTotal;
    /**
     * 人工费除税合价
     */
    @JsonProperty(value = "LaborNoTaxTotal")
    private String laborNoTaxTotal;
    /**
     * 工作内容
     */
    @JsonProperty(value = "WorkScope")
    private String workScope;
    /**
     * 计量规则
     */
    @JsonProperty(value = "CalcRule")
    private String calcRule;
    /**
     * 材料供应及品牌
     */
    @JsonProperty(value = "Brand")
    private String brand;
}
