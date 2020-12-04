package com.newgrand.secdev.controller.EDB;

import com.newgrand.secdev.domain.EDB.*;
import com.sun.deploy.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同结算
 * @Author ChenXiangLu
 * @Date 2020/12/1 17:20
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class CntMeasureController {

    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected JdbcTemplate jdbcTemplate;

    @ApiOperation(value="推送合同结算到经济数据库", notes="推送合同结算到经济数据库", produces="application/json")
    @RequestMapping(value = "/syncCntMeasure",method = RequestMethod.GET)
    public String syncCntMeasure()
    {
        //合同结算类型: 周材租赁/设备租赁/分包/其他支出合同
        var sqlM="select t1.phid, t1.bill_no,t1.title,t1.bill_dt,t2.pc_no phid_pc,t3.bill_no phid_cnt,\n" +
                "to_char(t4.bdt,'yyyy-mm') cycle,t5.compno phid_app,t1.app_amt_vat_fc,t6.fc_code curr_type,\n" +
                "t1.exch_rate,t7.item_no phid_ysfl,t1.dt_start,t1.dt_end,t8.ocode phid_ocode,t9.ocode phid_tr_ocode,\n" +
                "t1.remarks\n" +
                "from pcm3_cnt_pay_m t1\n" +
                "left join project_table t2 on t2.phid=t1.phid_pc\n" +
                "left join pcm3_cnt_m t3 on t3.phid=t1.phid_cnt\n" +
                "left join fg3_workcycle t4 on t4.phid=t1.cycle\n" +
                "left join fg3_enterprise t5 on t5.phid=t1.phid_app\n" +
                "left join fg_fcur t6 on t6.phid=t1.curr_type\n" +
                "left join bs_data t7 on t7.phid=t1.phid_ysfl\n" +
                "left join fg_orglist t8 on t8.phid=t1.phid_ocode\n" +
                "left join fg_orglist t9 on t9.phid=t1.phid_tr_ocode\n" +
                "where (t1.user_tbjjsjk!=1 or user_tbjjsjk is null)  and t1.type=";

        RowMapper<CntMeasureModel> rowMapper=new BeanPropertyRowMapper(CntMeasureModel.class);
        List<CntMeasureModel> cntMeasures= jdbcTemplate.query(sqlM, rowMapper);
        List<String> phids=cntMeasures.stream().map(f->f.getPhid()).collect(Collectors.toList());
        if(phids.size()<1)
        {
            return "";
        }
        var sqlD="select t1.pphid,t1.item_no,t1.item_name,t1.prc_fc,t1.Prc_vat_fc,t1.rep_qty,t1.amt_vat_fc,\n" +
                "t1.taxrate,t1.taxamt,t2.fc_code curr_type,t1.exch_rate,t1.remarks,t1.total_amt\n" +
                "from pcm3_cnt_pay_d t1\n" +
                "left join fg_fcur t2 on t2.phid=t1.curr_type" +
                "where t1.pphid in ("+ StringUtils.join(phids,",")+")";
        RowMapper<CntMeasureItemModel> rowDMapper=new BeanPropertyRowMapper(CntMeasureItemModel.class);
        List<CntMeasureItemModel> cntMeasureItems= jdbcTemplate.query(sqlD, rowDMapper);
        if(cntMeasureItems.size()>0)
        {
            for(CntMeasureModel v:cntMeasures)
            {
                List<CntMeasureItemModel> itemTemp=cntMeasureItems.stream().filter(f->f.getPphid()==v.getPhid()).collect(Collectors.toList());
                v.setPay_data(itemTemp);
            }
        }
        jdbcTemplate.execute("update pcm3_cnt_pay_m set user_tbjjsjk='1' where phid=");
        return "测试成功";
    }
}
