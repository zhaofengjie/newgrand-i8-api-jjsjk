package com.newgrand.secdev.domain.EDB;

import com.sun.xml.internal.ws.api.FeatureListValidatorAnnotation;
import lombok.Getter;
import lombok.Setter;

/**
 * BOQ变更清单模型
 * @Author ChenXiangLu
 * @Date 2020/12/21 16:35
 * @Version 1.0
 */
@Getter
@Setter
public class BOQChangeMModel {
    /**
     * 经济数据库唯一值
     */
    private String jjsjk;
    /**
     * 原始数量
     */
    private Double oriQty;
    /**
     * 当前数量
     */
    private Double currQty;
    /**
     * 原始单价
     */
    private Double oriPrc;
    /**
     * 当前单价
     */
    private Double currPrc;
    /**
     * 原始价格
     */
    private Double oriAmt;
    /**
     * 当前价格
     */
    private Double currAmt;
    /**
     * 原始单据phid
     */
    private String oldPhid;
}
