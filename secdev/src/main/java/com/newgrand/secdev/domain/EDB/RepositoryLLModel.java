package com.newgrand.secdev.domain.EDB;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 库存_领料单模型
 * @Author ChenXiangLu
 * @Date 2020/12/1 14:20
 * @Version 1.0
 */
@Getter
@Setter
public class RepositoryLLModel {
    /**
     * 主键
     */
    private String phid;
    /**
     * 单据编码
     */
    private String billno;
    /**
     * 单据日期
     */
    private String transdt;
    /**
     * 项目编码
     */
    private String phid_tr_proj;
    /**
     * 合同编码
     */
    private String phid_contractno;
    /**
     * 期间
     */
    private String phid_cycle;
    /**
     * 仓库
     */
    private String phid_warehouse;
    /**
     * 库管员
     */
    private String phid_chkpsn;
    /**
     * 领料班组
     */
    private String phid_classno;
    /**
     * 领用人
     */
    private String phid_userpsn;
    /**
     * 备注
     */
    private String descript;
    /**
     *明细
     */
    private List<RepositoryLLItemModel> kcbill_data;
}
