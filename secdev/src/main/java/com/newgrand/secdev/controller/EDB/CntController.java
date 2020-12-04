package com.newgrand.secdev.controller.EDB;

import com.newgrand.secdev.domain.EDB.CntModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 合同
 * @Author ChenXiangLu
 * @Date 2020/11/26 17:55
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class CntController {

    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected JdbcTemplate jdbcTemplate;

    @ApiOperation(value="推送合同数据到经济数据库", notes="推送合同数据到经济数据库", produces="application/json")
    @RequestMapping(value = "/getCnt",method = RequestMethod.GET)
    public String syncProCount()
    {
        var sql="select t1.phid,t1.bill_dt,t1.bill_no,t1.title,t2.pc_no phid_pc,t3.compno phid_reccomp,\n" +
                "t3.compno phid_sencomp,t1.senemp,t1.phid_senemp,t1.cnt_sum_vat_fc,t1.signdt,\n" +
                "t5.name cnt_type,t1.start_dt,t1.stat,t6.bill_no phid_parentid,t1.end_dt,\n" +
                "t7.cno phid_cm,t8.compno phid_schcomp,t1.csenemp,t9.ocode phid_ocode,\n" +
                "t10.compno phid_thdcmp,t14.item_no phid_ysfl,t1.iscb,t11.fc_code curr_type,\n" +
                "t1.exch_rate,t12.cno phid_pm,t13.cno phid_pe,t1.record_stat,t1.record_dt,\n" +
                "t1.needrecord,t1.is_fix_price_con,t1.is_amt_con\n" +
                "from pcm3_cnt_m t1\n" +
                "left join project_table t2 on t2.phid=t1.phid_pc\n" +
                "left join fg3_enterprise t3 on t3.phid=t1.phid_reccomp\n" +
                "left join fg3_enterprise t4 on t4.phid=t1.phid_sencomp\n" +
                "left join pcm3_cnt_type t5 on t5.phid=t1.cnt_type\n" +
                "left join pcm3_cnt_m t6 on t6.phid=t1.phid_parentid\n" +
                "left join hr_epm_main t7 on t7.phid=t1.phid_cm\n" +
                "left join fg3_enterprise t8 on t8.phid=t1.phid_schcomp\n" +
                "left join fg_orglist t9 on t9.phid=t1.phid_ocode\n" +
                "left join fg3_enterprise t10 on t10.phid=t1.phid_thdcmp\n" +
                "left join fg_fcur t11 on t11.phid=t1.curr_type\n" +
                "left join hr_epm_main t12 on t12.phid=t1.phid_pm\n" +
                "left join hr_epm_main t13 on t13.phid=t1.phid_pe\n" +
                "left join bs_data t14 on t14.phid=t1.phid_ysfl and t14.oper_type = 'budget_classify'";
        RowMapper<CntModel> rowMapper=new BeanPropertyRowMapper(CntModel.class);
        List<CntModel> cnts= jdbcTemplate.query(sql, rowMapper);
        return  "测试成功";
    }
}
