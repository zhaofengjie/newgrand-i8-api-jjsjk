package com.newgrand.domain.EDB;

import com.alibaba.fastjson.annotation.JSONField;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author ChenXiangLu
 * @Date 2020/12/1 15:10
 * @Version 1.0
 */
@Getter
@Setter
public class RepositoryLLItemModel {
    /**
     * 主键
     */
    private String phid;
    /**
     * 父级编码
     */
    @Ignore
    private String pphid;
    /**
     * 物料编码
     */
    private String item_no;
    /**
     *物料名称
     */
    private String item_name;
    /**
     * 物料分类编码
     */
    private String resBsNo;
    /**
     *批号
     */
    private String batchno;
    /**
     *入库单价(不含税)
     */
    private String rk_prc_notax;
    /**
     *入库单价(含税)
     */
    private String rk_prc;
    /**
     *入库金额(含税)
     */
    private String rk_sum;
    /**
     *税率
     */
    private String taxrate;
    /**
     *税额
     */
    private String tax;
    /**
     *币种
     */
    private String phid_curr_type;
    /**
     *汇率
     */
    private String exch_rate;
    /**
     *WBS
     */
    private String phid_wbs;
    /**
     *成本科目
     */
    private String phid_cbs;
    /**
     *质量要求
     */
    private String zlyq;
    /**
     *主要技术（性能）要求
     */
    private String zyjs;
    /**
     *备注
     */
    private String remarks;
    /**
     *单位
     */
    private String unit;
    /**
     *数量
     */
    private String qty;
}
