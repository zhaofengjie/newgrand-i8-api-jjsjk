package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 临时设施分摊模型
 * @Author ChenXiangLu
 * @Date 2020/12/4 10:58
 * @Version 1.0
 */
@Getter
@Setter
public class DeviceSharedModel {
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
     * 组织
     */
    @JSONField(name = "OrgCode")
    private String orgCode;
    /**
     * 明细
     */
    @JSONField(name = "Item")
    private List<DeviceSharedItemModel> item;
}
