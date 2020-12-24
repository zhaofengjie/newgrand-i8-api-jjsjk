package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 其他实际成本模型
 * @Author ChenXiangLu
 * @Date 2020/12/14 20:02
 * @Version 1.0
 */
@Getter
@Setter
public class OtherRealCostModel {
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
     * 期间
     */
    @JSONField(name = "Period")
    private String period;
    /**
     * 明细
     */
    @JSONField(name = "Item")
    private List<OtherRealCostItemModel> item;
}
