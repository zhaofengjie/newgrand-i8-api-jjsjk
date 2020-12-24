package com.newgrand.domain.EDB;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @Author ChenXiangLu
 * @Date 2020/11/25 21:14
 * @Version 1.0
 */
@Getter
@Setter
public class ProCountBQModel {

    /**
     * 主键
     */
    @JsonProperty(value = "ID")
    private String id;
    /**
     * 清单编码
     */
    @JsonProperty(value = "Code")
    private String code;
    /**
     * 清单名称
     */
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 清单特征
     */
    @JsonProperty(value = "Spec")
    private String spec;
    /**
     * 清单单位
     */
    @JsonProperty(value = "Unit")
    private String unit;
    /**
     * 当期工程量
     */
    @JsonProperty(value = "MonthQuantity")
    private String monthQuantity;
    /**
     * 当期产值
     */
    @JsonProperty(value = "MonthProductionValue")
    private String monthProductionValue;
    /**
     * 开累产值
     */
    @JsonProperty(value = "AccumulativeProductionValue")
    private String accumulativeProductionValue;
    /**
     * 剩余产值
     */
    @JsonProperty(value = "SurplusProductionValue")
    private String surplusProductionValue;
    /**
     * 税率
     */
    @JsonProperty(value = "TaxRatio")
    private String taxRatio;
    /**
     * 含税产值
     */
    @JsonProperty(value = "MonthTaxProductionValue")
    private String monthTaxProductionValue;
    /**
     * 形象进度描述
     */
    @JsonProperty(value = "Description")
    private String description;
    /**
     * 管理费及利润
     */
    @JsonProperty(value = "OverheadAndProfit")
    private String overheadAndProfit;

    /**
     * 明细
     */
    @JsonProperty(value = "FeeItemInfos")
    private ArrayList<ProCountBQFeeModel> feeItemInfos;
}
