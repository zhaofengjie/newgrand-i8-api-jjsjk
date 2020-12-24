package com.newgrand.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 零星单模型
 * @Author ChenXiangLu
 * @Date 2020/12/4 9:42
 * @Version 1.0
 */
@Getter
@Setter
public class OddBillModel {
    /**
     * 主键
     */
    @JSONField(name = "Id")
    private String id;
    /**
     * 单据编号
     */
    @JSONField(name = "BIllNo")
    private String billNo;
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
     * 期间
     */
    @JSONField(name = "Period")
    private String period;
    /**
     * 结算方式
     */
    @JSONField(name = "SettleMethod")
    private String settleMethod;
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
     * 支付对象
     */
    @JSONField(name = "PayAccount")
    private String payAccount;
    /**
     * 累计金额含税
     */
    @JSONField(name = "AmtSum")
    private String amtSum;
    /**
     * 明细
     */
    @JSONField(name = "Item")
    private List<OddBillItemModel> item;
}
