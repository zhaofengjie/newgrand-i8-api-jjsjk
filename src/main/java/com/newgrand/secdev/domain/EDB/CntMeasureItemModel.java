package com.newgrand.secdev.domain.EDB;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

/**
 * 合同计量结算明细模型
 * @Author ChenXiangLu
 * @Date 2020/12/2 10:52
 * @Version 1.0
 */
@Getter
@Setter
public class CntMeasureItemModel {
    /**
     * 编码
     */
    private String phid;
    /**
     * 父级编码
     */
    @Ignore
    private String pphid;
    /**
     * 经济数据库id
     */
    private String jjsjk;
    /**
     * 清单编码
     */
    private String item_no;
    /**
     * 清单名称
     */
    private String item_name;
    /**
     * 单价
     */
    private String prc_fc;
    /**
     * 含税单价
     */
    private String Prc_vat_fc;
    /**
     * 本期结算数量
     */
    private String rep_qty;
    /**
     * 本期结算金额（含税）
     */
    private String amt_vat_fc;
    /**
     * 税率
     */
    private String taxrate;
    /**
     * 税额
     */
    private String taxamt;
    /**
     * 币种
     */
    private String curr_type;
    /**
     * 汇率
     */
    private String exch_rate;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 累计结算金额
     */
    private String total_amt;
    /**
     * 单位
     */
    private String unit;
}

