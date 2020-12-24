package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 安全费用计提单
 * @Author ChenXiangLu
 * @Date 2020/12/4 16:43
 * @Version 1.0
 */
public class SecureFeeBillModel {
    /**
     * 主键
     */
    @JSONField(name = "Id")
    private String id;
    /**
     * 单据编号
     */
    @JSONField(name = "BIllNo")
    private String bIllNo;
    /**
     * 单据日期
     */
    @JSONField(name = "BillDt")
    private String billDt;
    /**
     * 单据名称
     */
    @JSONField(name = "BIllName")
    private String bIllName;
    /**
     * 项目编码
     */
    @JSONField(name = "PcNo")
    private String pcNo;
    /**
     * 项目名称
     */
    @JSONField(name = "PcName")
    private String pcName;
    /**
     * 本期收入
     */
    @JSONField(name = "Income")
    private String income;
    /**
     * 累计收入
     */
    @JSONField(name = "IncomeSum")
    private String incomeSum;
    /**
     * 计提比例
     */
    @JSONField(name = "OutPersent")
    private String outPersent;
    /**
     * 计提金额
     */
    @JSONField(name = "Expenditure")
    private String expenditure;
    /**
     * 累计已计提金额
     */
    @JSONField(name = "ExpenditureSum")
    private String expenditureSum;
    /**
     * 安全费用科目
     */
    @JSONField(name = "FeeAccount")
    private String feeAccount;
    /**
     * 成本科目
     */
    @JSONField(name = "CostAccount")
    private String costAccount;
    /**
     * 业务类型
     */
    @JSONField(name = "Type")
    private String type;
    /**
     * 备注
     */
    @JSONField(name = "Remarks")
    private String remarks;
}
