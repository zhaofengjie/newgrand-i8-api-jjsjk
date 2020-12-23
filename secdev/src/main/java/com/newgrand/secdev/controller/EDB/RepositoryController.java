package com.newgrand.secdev.controller.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.config.IJdbcTemplate;
import com.newgrand.secdev.domain.EDB.CntModel;
import com.newgrand.secdev.domain.EDB.RepositoryLLItemModel;
import com.newgrand.secdev.domain.EDB.RepositoryLLModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 库存管理_领料单
 * @Author ChenXiangLu
 * @Date 2020/12/1 13:26
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class RepositoryController {

    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected IJdbcTemplate jdbcTemplate;
    @Autowired
    private CloseableHttpClient httpClient;

    @Value("${i8.edb.url.repositoryLL}")
    private String repositoryLLUrl;

    @ApiOperation(value="推送领料单数据到经济数据库", notes="推送领料单数据到经济数据库", produces="application/json")
    @RequestMapping(value = "/syncRepositoryLL",method = RequestMethod.GET)
    public String syncRepositoryLL()
    {
        ///kc_transtype phid=6是班组领料
        var sqlM="select t1.phid,t1.billno,t1.transdt,t2.pc_no phid_tr_proj,t3.bill_no phid_contractno,\n" +
                "to_char(t4.bdt,'yyyy-mm') phid_cycle,t5.whname phid_warehouse,t6.cno phid_chkpsn,\n" +
                "t7.teams_name phid_classno,t8.cno phid_userpsn,t1.descript\n" +
                "from kc_billhead t1\n" +
                "left join project_table t2 on t2.phid=t1.phid_tr_proj\n" +
                "left join pcm3_cnt_m t3 on t3.phid=t1.phid_contractno\n" +
                "left join fg3_workcycle t4 on t4.phid=t1.phid_cycle\n" +
                "left join warehouse t5 on t5.phid=t1.phid_warehouse\n" +
                "left join hr_epm_main t6 on t6.phid=t1.phid_chkpsn\n" +
                "left join teams_gr t7 on t7.phid=t1.phid_classno\n" +
                "left join hr_epm_main t8 on t8.phid=t1.phid_userpsn\n " +
                "left join kc_transtype t9 on t9.phid=t1.phid_transno\n" +
                "where (t1.user_tbjjsjk!='1' or user_tbjjsjk is null) and t9.phid=6";

        RowMapper<RepositoryLLModel> rowMapper=new BeanPropertyRowMapper(RepositoryLLModel.class);
        List<RepositoryLLModel> repositoryLLs= jdbcTemplate.query(sqlM, rowMapper);
        List<String> phids=repositoryLLs.stream().map(f->f.getPhid()).collect(Collectors.toList());
        if(phids.size()<1)
        {
            return "";
        }
        var sqlD="select t1.phid,t1.pphid,t2.itemno item_no,t2.itemname item_name,t1.batchno,t1.user_rkdjhs rk_prc_tax,t1.rk_prc,t1.rk_sum,\n" +
                "t1.taxrate,t1.tax,t3.fc_code phid_curr_type,t1.exch_rate,t4.wbs_realcode phid_wbs,\n" +
                "t5.cbs_code phid_cbs,t1.zlyq,t1.zyjs,t1.remarks,t1.user_wlflbm resBsNo,t6.msname unit,t1.qty\n" +
                "from kc_billbody t1\n" +
                "left join ITEMDATA t2 on t2.phid=t1.phid_itemdata\n" +
                "left join fg_fcur t3 on t3.phid=t1.phid_curr_type\n" +
                "left join bd_wbs t4 on t4.phid=t1.phid_wbs\n" +
                "left join bd_cbs t5 on t5.phid=t1.phid_cbs\n" +
                "left join msunit t6 on t6.phid=t2.phid_msunit\n" +
                "where t1.pphid in ("+ StringUtils.join(phids,",")+")";
        RowMapper<RepositoryLLItemModel> rowDMapper=new BeanPropertyRowMapper(RepositoryLLItemModel.class);
        List<RepositoryLLItemModel> repositoryLLItems= jdbcTemplate.query(sqlD, rowDMapper);
        if(repositoryLLItems.size()>0)
        {
            for(RepositoryLLModel v:repositoryLLs)
            {
                List<RepositoryLLItemModel> itemTemp=repositoryLLItems.stream().filter(f->f.getPphid()==v.getPhid()).collect(Collectors.toList());
                v.setKcbill_data(itemTemp);
            }
        }
        HttpPost httpPost  = new HttpPost(repositoryLLUrl);
        httpPost.addHeader("Content-Type","application/json");
        for (RepositoryLLModel v:repositoryLLs) {
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
                        jdbcTemplate.execute("update kc_billhead set user_tbjjsjk='1' where phid="+v.getPhid());
                    }
                    else
                    {
                        System.out.println("推送失败"+root.getJSONObject("message").toJSONString());
                    }
                    return jsonString;
                } else {
                    log.error("请求返回:" + state + "(" + repositoryLLUrl + ")");
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
