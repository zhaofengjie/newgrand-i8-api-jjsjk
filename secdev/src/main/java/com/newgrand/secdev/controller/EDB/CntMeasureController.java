package com.newgrand.secdev.controller.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.config.IJdbcTemplate;
import com.newgrand.secdev.domain.EDB.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    protected IJdbcTemplate jdbcTemplate;
    @Autowired
    private CloseableHttpClient httpClient;

    @Value("${i8.edb.url.cntMeasureUrl}")
    private String cntMeasureUrl;

    /**
     * 推送分包合同结算到经济数据库
     * @return
     */
    @ApiOperation(value="推送分包合同结算到经济数据库", notes="推送分包合同结算到经济数据库", produces="application/json")
    @RequestMapping(value = "/syncCntMeasure",method = RequestMethod.GET)
    public String syncCntMeasure()
    {
        //合同结算类型: 4_专业分包,5_劳务分包
        var sqlM="select t1.phid, t1.bill_no,t1.title,t1.bill_dt,t2.pc_no phid_pc,t3.bill_no phid_cnt,\n" +
                "to_char(t4.bdt,'yyyy-mm') cycle,t5.compno phid_app,t1.app_amt_vat_fc,t6.fc_code curr_type,\n" +
                "t1.exch_rate,t7.item_no phid_ysfl,t1.dt_start,t1.dt_end,t8.ocode phid_ocode,t9.ocode phid_tr_ocode,\n" +
                "t1.remarks,t1.bill_type billtype\n" +
                "from pcm3_cnt_pay_m t1\n" +
                "left join project_table t2 on t2.phid=t1.phid_pc\n" +
                "left join pcm3_cnt_m t3 on t3.phid=t1.phid_cnt\n" +
                "left join fg3_workcycle t4 on t4.phid=t1.cycle\n" +
                "left join fg3_enterprise t5 on t5.phid=t1.phid_app\n" +
                "left join fg_fcur t6 on t6.phid=t1.curr_type\n" +
                "left join bs_data t7 on t7.phid=t1.phid_ysfl\n" +
                "left join fg_orglist t8 on t8.phid=t1.phid_ocode\n" +
                "left join fg_orglist t9 on t9.phid=t1.phid_tr_ocode\n" +
                "where (t1.user_tbjjsjk!='1' or user_tbjjsjk is null)  and t1.bill_type in ('4','5')";

        RowMapper<CntMeasureModel> rowMapper=new BeanPropertyRowMapper(CntMeasureModel.class);
        List<CntMeasureModel> cntMeasures= jdbcTemplate.query(sqlM, rowMapper);
        List<String> phids=cntMeasures.stream().map(f->f.getPhid()).collect(Collectors.toList());
        if(phids.size()<1)
        {
            log.info("无分包合同结算数据需要同步");
            return "";
        }
        var sqlD="select t1.pphid,t1.item_no,t3.cname item_name,t1.user_wlflbm resBsNo,t1.prc_fc,t1.Prc_vat_fc,t1.rep_qty,t1.amt_vat_fc,\n" +
                "t1.taxrate,t1.taxamt,t2.fc_code curr_type,t1.exch_rate,t1.remarks,t1.total_amt,t4.msname unit\n" +
                "from pcm3_cnt_pay_d t1\n" +
                "left join fg_fcur t2 on t2.phid=t1.curr_type\n" +
                "left join pms3_boq_m t3 on t3.code=t1.item_no  and t3.phid_cblx=11 and t1.phid_pc=t3.phid_pc\n" +
                "left join msunit t4 on t4.phid=t1.unit\n" +
                "where t1.pphid in ("+ StringUtils.join(phids,",")+")";
        RowMapper<CntMeasureItemModel> rowDMapper=new BeanPropertyRowMapper(CntMeasureItemModel.class);
        List<CntMeasureItemModel> cntMeasureItems= jdbcTemplate.query(sqlD, rowDMapper);
        if(cntMeasureItems.size()>0)
        {
            for(CntMeasureModel v:cntMeasures)
            {
                List<CntMeasureItemModel> itemTemp=cntMeasureItems.stream().filter(f->f.getPphid().equals(v.getPhid())).collect(Collectors.toList());
                v.setPay_data(itemTemp);
            }
        }
        HttpPost httpPost  = new HttpPost(cntMeasureUrl);
        httpPost.addHeader("Content-Type","application/json");
        for (CntMeasureModel v:cntMeasures) {
            HttpEntity entity = new StringEntity(JSONObject.toJSONString(v),"utf-8");
            httpPost.setEntity(entity);
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
                StatusLine status = response.getStatusLine();
                int state = status.getStatusCode();
                if (state == HttpStatus.SC_OK) {
                    HttpEntity responseEntity = response.getEntity();
                    String jsonString = EntityUtils.toString(responseEntity);
                    JSONObject root = JSON.parseObject(jsonString);
                    String form = root.get("code").toString();
                    if(form.equals("0"))
                    {
                        System.out.println("推送成功");
                        jdbcTemplate.execute("update pcm3_cnt_pay_m set user_tbjjsjk='1' where phid="+v.getPhid());
                    }
                    else
                    {
                        System.out.println("推送失败"+root.getJSONObject("message").toJSONString());
                    }
                } else {
                    log.error("请求返回:" + state + "(" + cntMeasureUrl + ")");
                }
            }
            catch (Exception e)
            {
                System.out.println("推送异常"+e.getMessage());
            }
        }
        return "测试成功";
    }

    /**
     * 推送其他合同结算到经济数据库
     * @return
     */
    @ApiOperation(value="推送其他合同结算到经济数据库", notes="推送其他合同结算到经济数据库", produces="application/json")
    @RequestMapping(value = "/syncCntMeasureOther",method = RequestMethod.GET)
    public String syncCntMeasureOther()
    {
        //合同结算类型: 11-周材/10-设备/6-其他支出
        var sqlM="select t1.phid, t1.bill_no,t1.title,t1.bill_dt,t2.pc_no phid_pc,t3.bill_no phid_cnt,\n" +
                "to_char(t4.bdt,'yyyy-mm') cycle,t5.compno phid_app,t1.app_amt_vat_fc,t6.fc_code curr_type,\n" +
                "t1.exch_rate,t7.item_no phid_ysfl,t1.dt_start,t1.dt_end,t8.ocode phid_ocode,t9.ocode phid_tr_ocode,\n" +
                "t1.remarks,t1.bill_type billtype\n" +
                "from pcm3_cnt_pay_m t1\n" +
                "left join project_table t2 on t2.phid=t1.phid_pc\n" +
                "left join pcm3_cnt_m t3 on t3.phid=t1.phid_cnt\n" +
                "left join fg3_workcycle t4 on t4.phid=t1.cycle\n" +
                "left join fg3_enterprise t5 on t5.phid=t1.phid_app\n" +
                "left join fg_fcur t6 on t6.phid=t1.curr_type\n" +
                "left join bs_data t7 on t7.phid=t1.phid_ysfl\n" +
                "left join fg_orglist t8 on t8.phid=t1.phid_ocode\n" +
                "left join fg_orglist t9 on t9.phid=t1.phid_tr_ocode\n" +
                "where (t1.user_tbjjsjk!='1' or user_tbjjsjk is null)  and t1.bill_type in ('6','10','11')";

        RowMapper<CntMeasureOtherModel> rowMapper=new BeanPropertyRowMapper(CntMeasureOtherModel.class);
        List<CntMeasureOtherModel> cntMeasureOthers= jdbcTemplate.query(sqlM, rowMapper);
        List<String> phids=cntMeasureOthers.stream().map(f->f.getPhid()).collect(Collectors.toList());
        if(phids.size()<1)
        {
            log.info("无其他类型的合同结算数据需要同步");
            return "";
        }
        var sqlD="select t1.pphid,t1.item_no,t3.cname item_name,t1.user_wlflbm resBsNo,t1.prc_fc,t1.Prc_vat_fc,t1.rep_qty,t1.amt_vat_fc,\n" +
                "t1.taxrate,t1.taxamt,t2.fc_code curr_type,t1.exch_rate,t1.remarks,t1.total_amt,t4.msname unit\n" +
                "from pcm3_cnt_pay_d t1\n" +
                "left join fg_fcur t2 on t2.phid=t1.curr_type\n" +
                "left join pms3_boq_m t3 on t3.code=t1.item_no  and t3.phid_cblx=11 and t1.phid_pc=t3.phid_pc\n" +
                "left join msunit t4 on t4.phid=t1.unit\n" +
                "where t1.pphid in ("+ StringUtils.join(phids,",")+")";
        RowMapper<CntMeasureOtherItemModel> rowDMapper=new BeanPropertyRowMapper(CntMeasureOtherItemModel.class);
        List<CntMeasureOtherItemModel> cntMeasureOtherItems= jdbcTemplate.query(sqlD, rowDMapper);
        if(cntMeasureOtherItems.size()>0)
        {
            for(CntMeasureOtherModel v:cntMeasureOthers)
            {
                List<CntMeasureOtherItemModel> itemTemp=cntMeasureOtherItems.stream().filter(f->f.getPphid()==v.getPhid()).collect(Collectors.toList());
                v.setPay_data(itemTemp);
            }
        }
        HttpPost httpPost  = new HttpPost(cntMeasureUrl);
        httpPost.addHeader("Content-Type","application/json");
        for (CntMeasureOtherModel v:cntMeasureOthers) {
            HttpEntity entity = new StringEntity(JSONObject.toJSONString(v),"utf-8");
            httpPost.setEntity(entity);
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
                StatusLine status = response.getStatusLine();
                int state = status.getStatusCode();
                if (state == HttpStatus.SC_OK) {
                    HttpEntity responseEntity = response.getEntity();
                    String jsonString = EntityUtils.toString(responseEntity);
                    JSONObject root = JSON.parseObject(jsonString);
                    String form = root.get("code").toString();
                    if(form.equals("0"))
                    {
                        System.out.println("推送成功");
                        jdbcTemplate.execute("update pcm3_cnt_pay_m set user_tbjjsjk='1' where phid="+v.getPhid());
                    }
                    else
                    {
                        System.out.println("推送失败"+root.getJSONObject("message").toJSONString());
                    }
                } else {
                    log.error("请求返回:" + state + "(" + cntMeasureUrl + ")");
                }
            }
            catch (Exception e)
            {
                System.out.println("推送异常"+e.getMessage());
            }
        }
        return "测试成功";
    }
}
