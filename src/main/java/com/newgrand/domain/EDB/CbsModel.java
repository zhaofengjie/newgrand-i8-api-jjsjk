package com.newgrand.domain.EDB;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

/**
 * CBS模型
 * @Author ChenXiangLu
 * @Date 2020/11/27 17:48
 * @Version 1.0
 */
@Getter
@Setter
public class CbsModel {
    @Ignore
    private String phid;
    /**
     * 成本科目编码
     */
    private String cbs_code;
    /**
     * 成本科目名称
     */
    private String cbs_name;
    /**
     * 成本科目说明
     */
    private String note;
    /**
     * 对应财务科目
     */
    private String cbs;
    /**
     * 父项目成本科目
     */
    private String parent_code;
    /**
     * remarks
     */
    private String remarks;
    /**
     * pc_no
     */
    private String pc_no;
}
