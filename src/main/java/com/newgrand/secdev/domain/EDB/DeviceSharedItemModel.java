package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

/**
 * 临时设施分摊明细模型
 * @Author ChenXiangLu
 * @Date 2020/12/4 10:58
 * @Version 1.0
 */
@Getter
@Setter
public class DeviceSharedItemModel {
    /**
     * 主键
     */
    @JSONField(name = "Id")
    private String id;
    /**
     * 名称
     */
    @JSONField(name = "Name")
    private String name;
    /**
     * 父级主键
     */
    @Ignore
    private String parentId;
    /**
     * 不含税金额
     */
    @JSONField(name = "AmtNoTax")
    private String amtNoTax;
    /**
     * 累计不含税金额
     */
    @JSONField(name = "AmtNoTaxSum")
    private String amtNoTaxSum;
    /**
     * 分摊成本科目
     */
    @JSONField(name = "CostAccount")
    private String costAccount;
    /**
     * 备注
     */
    @JSONField(name = "Remarks")
    private String remarks;
}
