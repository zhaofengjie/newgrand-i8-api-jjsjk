package com.newgrand.domain.EDB;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

/**
 * 成本BOQ审核状态
 * @Author ChenXiangLu
 * @Date 2020/12/22 14:04
 * @Version 1.0
 */
@Getter
@Setter
public class BOQChkModel {
    /**
     * 单据号
     */
    private String billNo;
    /**
     * 项目编码
     */
    private String pcNo;
    @Ignore
    private String chk_flg;
    @Ignore
    private String wf_flg;
    /**
     * 状态 0_未审核 1_审核中 2_已审核
     */
    private String state;

}
