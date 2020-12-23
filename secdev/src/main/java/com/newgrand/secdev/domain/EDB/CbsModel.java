package com.newgrand.secdev.domain.EDB;

import lombok.Getter;
import lombok.Setter;

/**
 * CBS模型
 * @Author ChenXiangLu
 * @Date 2020/11/27 17:48
 * @Version 1.0
 */
@Getter
@Setter
public class CbsModel {
    private String cbs_code;
    private String cbs_name;
    private String note;
    private String cbs;
    private String parent_code;
    private String remarks;
    private String pc_no;
}
