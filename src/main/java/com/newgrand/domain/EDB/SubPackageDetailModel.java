package com.newgrand.domain.EDB;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * 分包策划明细
 * @Author ChenXiangLu
 * @Date 2020/11/25 17:36
 * @Version 1.0
 */
@Getter
@Setter
public class SubPackageDetailModel {

    /**
     * 主键
     */
    @JsonProperty(value = "ID")
    private String id;
    /**
     * 分包名称
     */
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 明细
     */
    @JsonProperty(value = "FeeItemInfos")
    private ArrayList<SubPackageDetailFeeModel> feeItemInfos;
}
