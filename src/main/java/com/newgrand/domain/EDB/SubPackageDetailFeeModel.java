package com.newgrand.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 分包策划费用明细
 * @Author ChenXiangLu
 * @Date 2020/11/25 17:36
 * @Version 1.0
 */
@Getter
@Setter
public class SubPackageDetailFeeModel {

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
     * 税金合价
     */
    @JsonProperty(value = "CourseCode")
    private String 费用项对应科目编码;
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
}
