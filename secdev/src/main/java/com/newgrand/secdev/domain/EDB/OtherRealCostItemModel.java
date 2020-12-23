package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

/**
 * 其他实际成本明细模型
 * @Author ChenXiangLu
 * @Date 2020/12/14 20:03
 * @Version 1.0
 */
@Getter
@Setter
public class OtherRealCostItemModel {
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
     * 费用名称
     */
    @JSONField(name = "FeeName")
    private String feeName;
    /**
     * 数量
     */
    @JSONField(name = "Count")
    private String count;
    /**
     * 含税单价
     */
    @JSONField(name = "priceTax")
    private String priceTax;
    /**
     * 单价
     */
    @JSONField(name = "Price")
    private String price;
    /**
     * 不含税金额
     */
    @JSONField(name = "AmtNoTax")
    private String amtNoTax;
    /**
     * 含税金额
     */
    @JSONField(name = "Amt")
    private String amt;
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
    @JSONField(name = "CbsCode")
    private String cbsCode;
}
