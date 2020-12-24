package com.newgrand.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFilter;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

/**
 * 零星单明细模型
 * @Author ChenXiangLu
 * @Date 2020/12/4 9:45
 * @Version 1.0
 */
@Getter
@Setter
public class OddBillItemModel {
    /**
     * 主键
     */
    @JSONField(name = "Id")
    private String id;
    /**
     * 父级主键
     */
    @Ignore
    private String parentId;
    /**
     * 无合同类型
     */
    @JSONField(name = "NoCntType")
    private String noCntType;
    /**
     * 业务类型属性
     */
    @JSONField(name = "Properties")
    private String properties;
    /**
     * 业务说明
     */
    @JSONField(name = "Description")
    private String description;
    /**
     * 发生日期
     */
    @JSONField(name = "BeginDate")
    private String beginDate;
    /**
     * 含税金额
     */
    @JSONField(name = "Amt")
    private String amt;
    /**
     * 不含税金额
     */
    @JSONField(name = "AmtNoTax")
    private String amtNoTax;
    /**
     * 税额
     */
    @JSONField(name = "Tax")
    private String tax;
    /**
     * 税率
     */
    @JSONField(name = "Rate")
    private String rate;
    /**
     * 成本科目
     */
    @JSONField(name = "CostAccount")
    private String costAccount;
    /**
     * 备注
     */
    @JSONField(name = "Remarks")
    private String remarks;
}
