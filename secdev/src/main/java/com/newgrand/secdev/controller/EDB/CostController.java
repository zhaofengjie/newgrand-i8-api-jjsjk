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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成本数据推送
 * @Author ChenXiangLu
 * @Date 2020/12/4 9:28
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class CostController {

    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected IJdbcTemplate jdbcTemplate;
    @Autowired
    private CloseableHttpClient httpClient;

    @Value("${i8.edb.url.oddBillUrl}")
    protected String oddBillUrl;
    @Value("${i8.edb.url.deviceUrl}")
    protected String deviceUrl;
    @Value("${i8.edb.url.secureFeeBillUrl}")
    protected String secureFeeBillUrl;
    @Value("${i8.edb.url.otherRealCostUrl}")
    protected String otherRealCostUrl;

    /**
     * 推送零星单到经济数据库
     * @return
     */
    @ApiOperation(value="推送零星单到经济数据库", notes="推送零星单到经济数据库", produces="application/json")
    @RequestMapping(value = "/syncOddBill",method = RequestMethod.GET)
    public String syncOddBill() {

        var sqlM="select  t1.phid id,t1.bill_no billno,t1.bill_dt billdt,t1.title billname,t1.u_xmbm pcNo,t2.project_name pcname,\n" +
                "t1.u_jews amt,t1.u_jehs AmtNoTax,to_char(t3.bdt,'yyyy-mm') Period,t4.payway SettleMethod,t1.u_se tax,\n" +
                "t1.u_sl Rate,t5.compno PayAccount,t1.u_ljjehs AmtSum\n" +
                "from p_form_cbgl_lxfyd_m t1\n" +
                "left join project_table t2 on t2.pc_no=t1.u_xmbm\n" +
                "left join fg3_workcycle t3 on t3.phid=t1.u_qj\n" +
                "left join payway t4 on t4.phid=t1.u_jsfs\n" +
                "left join fg3_enterprise t5 on t5.phid=t1.u_zfdx\n" +
                "where (t1.u_tbjjsjk!='1' or u_tbjjsjk is null) and ischeck='1'";

        RowMapper<OddBillModel> rowMapper=new BeanPropertyRowMapper(OddBillModel.class);
        List<OddBillModel> oddBills= jdbcTemplate.query(sqlM, rowMapper);
        List<String> phids=oddBills.stream().map(f->f.getId()).collect(Collectors.toList());
        if(phids.size()<1)
        {
            return "";
        }
        var sqlD="select t1.phid id,t1.m_code parentId,t2.c_name NoCntType,t3.c_name Properties,t1.u_ywsm Description,t1.u_fsrq BeginDate,\n" +
                "t1.u_jews Amt,t1.u_jehs AmtNoTax,t1.u_se Tax, t1.u_sl Rate,t4.cbs_code CostAccount,t1.u_bz Remarks\n" +
                "from p_form_cbgl_lxfyd_d t1\n" +
                "left join fg_simple_data t2 on t2.phid=t1.u_whtywlx\n" +
                "left join fg_simple_data t3 on t3.phid=t1.u_ywlxsx\n" +
                "left join bd_cbs t4 on t4.phid=t1.u_cbkm\n" +
                "where t1.m_code in ("+ StringUtils.join(phids,",")+")";
        RowMapper<OddBillItemModel> rowDMapper=new BeanPropertyRowMapper(OddBillItemModel.class);
        List<OddBillItemModel> oddBillItems= jdbcTemplate.query(sqlD, rowDMapper);
        if(oddBillItems.size()>0)
        {
            for(OddBillModel v:oddBills)
            {
                List<OddBillItemModel> itemTemp=oddBillItems.stream().filter(f->f.getParentId()==v.getId()).collect(Collectors.toList());
                v.setItem(itemTemp);
            }
        }
        HttpPost httpPost  = new HttpPost(oddBillUrl);
        httpPost.addHeader("Content-Type","application/json");
        for (OddBillModel v:oddBills) {
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
//                        jdbcTemplate.execute("update p_form_cbgl_lxfyd_m set u_tbjjsjk='1' where phid="+v.getPhid());
                    }
                    else
                    {
                        System.out.println("推送失败"+root.getJSONObject("message").toJSONString());
                    }
                } else {
                    log.error("请求返回:" + state + "(" + oddBillUrl + ")");
                }
            }
            catch (Exception e)
            {
                System.out.println("推送异常"+e.getMessage());
            }
        }
        jdbcTemplate.execute("update kc_billhead set user_tbjjsjk='1' where phid=");
        return  "测试成功";
    }

    /**
     * 推送临时设施摊销到经济数据库
     * @return
     */
    @ApiOperation(value="推送临时设施摊销到经济数据库", notes="推送临时设施摊销到经济数据库", produces="application/json")
    @RequestMapping(value = "/syncDevice",method = RequestMethod.GET)
    public String syncDevice() {

        var sqlM="select t1.phid id,t1.bill_no BIllNo,t1.bill_dt BillDt,t1.title BIllName,\n" +
                "t2.pc_no pcno,t2.project_name pcname,t1.u_ftjews AmtNoTax,\n" +
                "to_char(t3.bdt,'yyyy-mm') Period,t4.ocode OrgCode\n" +
                "from p_form_cbgl_lsssftb_m t1\n" +
                "left join project_table t2 on t2.phid=t1.pc\n" +
                "left join fg3_workcycle t3 on t3.phid=t1.u_qj\n" +
                "left join fg_orglist t4 on t4.phid=t1.ocode\n" +
                "where (t1.u_tbjjsjk!='1' or u_tbjjsjk is null) and ischeck='1'";

        RowMapper<DeviceSharedModel> rowMapper=new BeanPropertyRowMapper(DeviceSharedModel.class);
        List<DeviceSharedModel> deviceSharedModels= jdbcTemplate.query(sqlM, rowMapper);
        List<String> phids=deviceSharedModels.stream().map(f->f.getId()).collect(Collectors.toList());
        if(phids.size()<1)
        {
            return "";
        }
        var sqlD="select t1.phid id,t1.m_code parentId,t1.u_bqftjews AmtNoTax,t1.u_ljyftjews1 AmtNoTaxSum,\n" +
                "t2.cbs_code CostAccount,t1.u_bz Remarks,t1.u_lsssmc name\n" +
                "from p_form_cbgl_lsssftb_d t1\n" +
                "left join bd_cbs t2 on t2.phid=t1.u_ftcbkm\n" +
                "where t1.m_code in ("+ StringUtils.join(phids,",")+")";
        RowMapper<DeviceSharedItemModel> rowDMapper=new BeanPropertyRowMapper(DeviceSharedItemModel.class);
        List<DeviceSharedItemModel> deviceSharedItems= jdbcTemplate.query(sqlD, rowDMapper);
        if(deviceSharedItems.size()>0)
        {
            for(DeviceSharedModel v:deviceSharedModels)
            {
                List<DeviceSharedItemModel> itemTemp=deviceSharedItems.stream().filter(f->f.getParentId()==v.getId()).collect(Collectors.toList());
                v.setItem(itemTemp);
            }
        }
        HttpPost httpPost  = new HttpPost(deviceUrl);
        httpPost.addHeader("Content-Type","application/json");
        for (DeviceSharedModel v:deviceSharedModels) {
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
//                        jdbcTemplate.execute("update p_form_cbgl_lxfyd_m set u_tbjjsjk='1' where phid="+v.getPhid());
                    }
                    else
                    {
                        System.out.println("推送失败"+root.getJSONObject("message").toJSONString());
                    }
                } else {
                    log.error("请求返回:" + state + "(" + deviceUrl + ")");
                }
            }
            catch (Exception e)
            {
                System.out.println("推送异常"+e.getMessage());
            }
        }
        return  "测试成功";
    }

    /**
     * 推送安全费用计提单到经济数据库
     * @return
     */
    @ApiOperation(value="推送安全费用计提单到经济数据库", notes="推送安全费用计提单到经济数据库", produces="application/json")
    @RequestMapping(value = "/SyncSecureFeeBill",method = RequestMethod.GET)
    public String SyncSecureFeeBill() {

        var sqlM="select t1.phid id, t1.bill_no bIllNo,t1.bill_dt billdt,t1.title billName,t2.pc_no pcNo,\n" +
                "t2.project_name pcName,t1.u_bqsr Income,t1.u_ljsr IncomeSum,t1.u_jtbl OutPersent,\n" +
                "t1.u_bqjtje Expenditure,t1.u_ljyjt ExpenditureSum,t3.cbs_code FeeAccount,\n" +
                "t4.cbs_code CostAccount,t5.c_name Type,t1.u_bz remarks\n" +
                "from p_form_cbgl_aqfyjtd_m\tt1\n" +
                "left join project_table t2 on t2.phid=t1.pc\n" +
                "left join bd_cbs t3 on t3.phid=t1.u_aqfykm \n" +
                "left join bd_cbs t4 on t4.phid=t1.u_cbkm \n" +
                "left join fg_simple_data t5 on t5.phid=t1.u_ywlx\n" +
                "where (t1.u_tbjjsjk!='1' or u_tbjjsjk is null) and ischeck='1'";

        RowMapper<DeviceSharedModel> rowMapper=new BeanPropertyRowMapper(DeviceSharedModel.class);
        List<DeviceSharedModel> deviceSharedModels= jdbcTemplate.query(sqlM, rowMapper);

        HttpPost httpPost  = new HttpPost(secureFeeBillUrl);
        httpPost.addHeader("Content-Type","application/json");
        for (DeviceSharedModel v:deviceSharedModels) {
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
//                        jdbcTemplate.execute("update p_form_cbgl_lsssftb_m set u_tbjjsjk='1' where phid="+v.getPhid());
                    }
                    else
                    {
                        System.out.println("推送失败"+root.getJSONObject("message").toJSONString());
                    }
                } else {
                    log.error("请求返回:" + state + "(" + secureFeeBillUrl + ")");
                }
            }
            catch (Exception e)
            {
                System.out.println("推送异常"+e.getMessage());
            }
        }
        return  "测试成功";
    }

    /**
     * 推送其他实际成本到经济数据库
     * @return
     */
    @ApiOperation(value="推送其他实际成本到经济数据库", notes="推送其他实际成本到经济数据库", produces="application/json")
    @RequestMapping(value = "/SyncOtherRealCost",method = RequestMethod.GET)
    public String SyncOtherRealCost() {

        var sqlM="select t1.phid id, t1.bill_no bIllNo,t1.bill_dt billdt,t1.title billName,t2.pc_no pcNo,\n" +
                "t2.project_name pcName,to_char(t3.bdt,'yyyy-mm') period\n" +
                "from pco3_cost_other_m t1\n" +
                "left join project_table t2 on t2.phid=t1.phid_pc\n" +
                "left join fg3_workcycle t3 on t3.phid=t1.phid_cycle \n" +
                "where t1.chk_flg='1' and (t1.user_tbjjsjk!='1' or user_tbjjsjk is null)";

        RowMapper<OtherRealCostModel> rowMapper=new BeanPropertyRowMapper(OtherRealCostModel.class);
        List<OtherRealCostModel> otherRealCostModels= jdbcTemplate.query(sqlM, rowMapper);
        List<String> phids=otherRealCostModels.stream().map(f->f.getId()).collect(Collectors.toList());
        if(phids.size()<1)
        {
            return "";
        }
        var sqlD="select t1.phid id,t1. pphid parentId,t2.item_name feename,t1.qty count,t1.user_djhs PriceTax,t1.prc price,\n" +
                "t1.money amtnotax,t1.tax_mony amt,t1.tax tax,t1.taxrate rate,t3.cbs_code CbsCode\n" +
                "from pco3_cost_other_d t1\n" +
                "left join bs_data t2 on t2.phid=t1.phid_cost\n" +
                "left join bd_cbs t3 on t3.phid=t1.phid_cbs\n" +
                "where t1.pphid in ("+ StringUtils.join(phids,",")+")";
        RowMapper<OtherRealCostItemModel> rowDMapper=new BeanPropertyRowMapper(OtherRealCostItemModel.class);
        List<OtherRealCostItemModel> otherRealCostItems= jdbcTemplate.query(sqlD, rowDMapper);
        if(otherRealCostItems.size()>0)
        {
            for(OtherRealCostModel v:otherRealCostModels)
            {
                List<OtherRealCostItemModel> itemTemp=otherRealCostItems.stream().filter(f->f.getParentId()==v.getId()).collect(Collectors.toList());
                v.setItem(itemTemp);
            }
        }
        HttpPost httpPost  = new HttpPost(otherRealCostUrl);
        httpPost.addHeader("Content-Type","application/json");
        for (OtherRealCostModel v:otherRealCostModels) {
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
//                        jdbcTemplate.execute("update p_form_cbgl_lsssftb_m set u_tbjjsjk='1' where phid="+v.getPhid());
                    }
                    else
                    {
                        System.out.println("推送失败"+root.getJSONObject("message").toJSONString());
                    }
                } else {
                    log.error("请求返回:" + state + "(" + otherRealCostUrl + ")");
                }
            }
            catch (Exception e)
            {
                System.out.println("推送异常"+e.getMessage());
            }
        }
        return  "测试成功";
    }


}
