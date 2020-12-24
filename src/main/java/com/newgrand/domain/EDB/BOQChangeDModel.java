package com.newgrand.domain.EDB;

import lombok.Getter;
import lombok.Setter;

/**
 * BOQ变更费用项模型
 * @Author ChenXiangLu
 * @Date 2020/12/21 16:35
 * @Version 1.0
 */
@Getter
@Setter
public class BOQChangeDModel {
    /**
     * 经济数据库唯一值
     */
    private String jjsjk;
    /**
     * 原始单耗量
     */
    private Double oriQty;
    /**
     * 原始单价
     */
    private Double oriPrc;
    /**
     * 原始单耗合价
     */
    private Double oriAmt;
    /**
     * 原始总量
     */
    private Double oriTotQty;
    /**
     * 原始总价
     */
    private Double oriTotAmt;
    /**
     * 原始单据phid
     */
    private String oldPhid;
}
