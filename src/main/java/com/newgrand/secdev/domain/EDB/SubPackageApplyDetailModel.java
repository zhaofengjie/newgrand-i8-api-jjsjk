package com.newgrand.secdev.domain.EDB;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 分包申请表头
 * @Author ChenXiangLu
 * @Date 2020/12/11 9:28
 * @Version 1.0
 */
@Getter
@Setter
public class SubPackageApplyDetailModel {

    /**
     * 主键
     */
    @JsonProperty(value = "ID")
    private String id;
    /**
     * 名称
     */
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 1-劳务分包 2-材料费 3-周转材料费 4-机械费 8-专业分包
     */
    @JsonProperty(value = "FeeType")
    private String feeType;
    /**
     * 分包申请表头
     */
    @JsonProperty(value = "FeeItemInfos")
    private ArrayList<SubPackageApplyDetailFeeModel> feeItemInfos;
}
