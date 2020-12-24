package com.newgrand.secdev.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * BOQ其他费用
 * @Author ChenXiangLu
 * @Date 2020/11/25 16:54
 * @Version 1.0
 */
@Getter
@Setter
public class BOQOtherModel {

    /**
     * 主键
     */
    @JsonProperty(value = "ID")
    private String id;
    /**
     * 单位工程ID
     */
    @JsonProperty(value = "BidNodeID")
    private String bidNodeID;
    /**
     * 编码
     */
    @JsonProperty(value = "Code")
    private String code;
    /**
     * 名称
     */
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 单位
     */
    @JsonProperty(value = "Unit")
    private String unit;

    /**
     * 合价
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

}
