package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * BOQ单价措施费
 * @Author ChenXiangLu
 * @Date 2020/11/25 16:53
 * @Version 1.0
 */
@Getter
@Setter
public class BOQMeasureModel {

    /**
     * 主键
     */
    @JsonProperty(value = "ID")
    private String id;
    /**
     * 分部ID
     */
    @JsonProperty(value = "PID")
    private String pId;
    /**
     * 单位工程id_入股分部id不存在,此处把单位工程作为wbs编码
     */
    @JsonProperty(value = "BidNodeID")
    private String bidNodeID;
    /**
     * 清单编码
     */
    @JsonProperty(value = "Code")
    private String code;
    /**
     * 清单名称
     */
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 清单特征
     */
    @JsonProperty(value = "Spec")
    private String spec;
    /**
     * 单位
     */
    @JsonProperty(value = "Unit")
    private String unit;
    /**
     * 清单工程量
     */
    @JsonProperty(value = "Quantity")
    private String quantity;
    /**
     * 清单单价
     */
    @JsonProperty(value = "Rate")
    private String rate;
    /**
     * 清单合价
     */
    @JsonProperty(value = "Total")
    private String total;
    /**
     * 合同编码
     */
    @JsonProperty(value = "ContractCode")
    private String contractCode;

    /**
     * 状态：0. 未修改 1.修改 2.新增
     */
    @JsonProperty(value = "Status")
    private String status;
    /**
     * 明细
     */
    @JsonProperty(value = "FeeItemInfos")
    private ArrayList<BOQMeasureFeeModel> feeItemInfos;
}
