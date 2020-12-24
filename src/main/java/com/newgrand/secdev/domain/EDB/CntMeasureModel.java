package com.newgrand.secdev.domain.EDB;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 合同计量结算模型
 * @Author ChenXiangLu
 * @Date 2020/12/2 10:48
 * @Version 1.0
 */
@Getter
@Setter
public class CntMeasureModel {
    /**
     * 主键
     */
        private String phid;
    /**
     * 单据编码
     */
    private String bill_no;
    /**
     * 单据名称
     */
    private String title;
    /**
     * 单据日期
     */
    private String bill_dt;
    /**
     * 项目编码
     */
    private String phid_pc;
    /**
     * 合同编码
     */
    private String phid_cnt;
    /**
     * 期间
     */
    private String cycle;
    /**
     * 乙方单位
     */
    private String phid_app;
    /**
     * 本期结算金额(含税)
     */
    private String app_amt_vat_fc;
    /**
     * 币种
     */
    private String curr_type;
    /**
     * 汇率
     */
    private String exch_rate;
    /**
     * 预算分类
     */
    private String phid_ysfl;
    /**
     * 结算开始时间
     */
    private String dt_start;
    /**
     * 结算结束时间
     */
    private String dt_end;
    /**
     * 组织
     */
    private String phid_ocode;
    /**
     * 默认财务组织
     */
    private String phid_tr_ocode;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 单据类型 5_劳务分包 4_专业分包
     */
    private String billType;
    /**
     * 明细
     */
    private List<CntMeasureItemModel> pay_data;
}
