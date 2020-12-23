package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * BOQ工程清单明细
 * @Author ChenXiangLu
 * @Date 2020/11/25 16:54
 * @Version 1.0
 */
@Getter
@Setter
public class BOQBQFeeModel {

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
     * 增值税率
     */
    @JsonProperty(value = "Zzsl")
    private String zzsl;
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
     * 状态：0. 未修改 1.修改 2.新增
     */
    @JsonProperty(value = "Status")
    private String status;
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
