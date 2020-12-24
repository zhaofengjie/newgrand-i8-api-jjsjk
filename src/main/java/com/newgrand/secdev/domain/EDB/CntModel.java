package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * 合同模型
 * @Author ChenXiangLu
 * @Date 2020/11/26 17:36
 * @Version 1.0
 */
@Getter
@Setter
public class CntModel {
    /**
     *主键
     */
    private String phid;
    /**
     * 单据日期
     */
    private String bill_dt;
    /**
     * 合同编码
     */
    private String bill_no;
    /**
     * 合同名称
     */
    private String title;
    /**
     * 项目编码
     */
    private String phid_pc;
    /**
     * 甲方单位
     */
    private String phid_reccomp;
    /**
     * 乙方单位
     */
    private String phid_sencomp;
    /**
     * 甲方代表
     */
    private String senemp;
    /**
     * 乙方代表
     */
    private String phid_senemp;
    /**
     * 合同金额
     */
    private String cnt_sum_vat_fc;
    /**
     * 签订日期
     */
    private String signdt;
    /**
     * 合同类型
     */
    private String cnt_type;
    /**
     * 开始时间
     */
    private String start_dt;
    /**
     * 合同状态
     */
    private String stat;
    /**
     * 关联主合同
     */
    private String phid_parentid;
    /**
     * 结束时间
     */
    private String end_dt;
    /**
     * 现场经理
     */
    private String phid_cm;
    /**
     * 实施单位
     */
    private String phid_schcomp;
    /**
     * 甲方现场代表
     */
    private String csenemp;
    /**
     * 组织
     */
    private String phid_ocode;
    /**
     * 丙方单位
     */
    private String phid_thdcmp;
    private String thdemp;
    /**
     * 预算分类
     */
    private String phid_ysfl;
    /**
     * 成本类型
     */
    private String iscb;
    /**
     * 币种
     */
    private String curr_type;
    /**
     * 汇率
     */
    private String exch_rate;
    /**
     * 项目经理
     */
    private String phid_pm;
    /**
     * 项目责任人
     */
    private String phid_pe;
    /**
     * 备案状态
     */
    private String record_stat;
    /**
     * 备案时间
     */
    private String record_dt;
    /**
     * 是否需备案
     */
    private String needrecord;
    /**
     * 固定单价合同
     */
    private String is_fix_price_con;
    /**
     * 总价合同
     */
    private String is_amt_con;
}
