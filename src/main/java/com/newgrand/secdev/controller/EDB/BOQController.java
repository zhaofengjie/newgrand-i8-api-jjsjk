package com.newgrand.secdev.controller.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.config.IJdbcTemplate;
import com.newgrand.secdev.domain.DataInfo;
import com.newgrand.secdev.domain.EDB.*;
import com.newgrand.secdev.service.EDB.BOQChangeService;
import com.newgrand.secdev.service.EDB.BOQService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * BOQ
 * @Author ChenXiangLu
 * @Date 2020/11/25 16:42
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class BOQController {

    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected IJdbcTemplate jdbcTemplate;
    @Autowired
    private CloseableHttpClient httpClient;
    @Value("${i8.edb.url.boqChkUrl}")
    private String boqChkUrl;
    @Autowired
    private BOQService boqService;
    /**
     * 预算分类 成本
     */
    @Value("${i8.edb.boqysflCost}")
    private String boqysflCost;

    @Autowired
    private BOQChangeService boqChangeService;

    /**
     * 接收BOQ数据
     * @param param
     * @return
     */
    @ApiOperation(value="接收BOQ数据", notes="接收BOQ数据", produces="application/json")
    @RequestMapping(value = "/syncBoq",method = RequestMethod.POST)
    public EDBResultModel<BOQResultModel> syncBoq(@RequestBody BOQModel param) {
        log.info("接收BOQ数据"+ JSONObject.toJSONString(param));
        System.out.println("接收BOQ数据"+ JSONObject.toJSONString(param));
        var result = new EDBResultModel<BOQResultModel>();
        var resData=new BOQResultModel();
        DataInfo dataInfo=new DataInfo();
        try {
            //需要判断标识是申请还是变更,如果变更执行变更 1_申请 2_变更
            if(param.getApplyType().equals("1")) {//申请
                //同步收入清单
                dataInfo = boqService.saveBoqCost(param, false);
//                dataInfo = boqService.saveBoqCost(param, true);//直接测试成本
                //同步成本清单,如果收入清单成功了,继续同步成本清单,都成功再返回成功,
                // 如果收入清单成功,成本失败,并且收入清单已审核,提是需要取消审核后再同步
                if (dataInfo.getStatus().equals("0")) {
                    dataInfo = boqService.saveBoqCost(param, true);
                }
            }
            else//变更
            {
                //同步收入清单
                dataInfo = boqChangeService.saveBoqCostChg(param, false);
//                dataInfo = boqService.saveBoqCost(param, true);//直接测试成本
                //同步成本清单,如果收入清单成功了,继续同步成本清单,都成功再返回成功,
                // 如果收入清单成功,成本失败,并且收入清单已审核,提是需要取消审核后再同步
                if (dataInfo.getStatus().equals("0")) {
                    dataInfo = boqChangeService.saveBoqCostChg(param, true);
                }
            }
            resData.setId(dataInfo.getCode());
            resData.setUrl("单点接口开发完提供");
            result.setData(resData);
            result.setCode(dataInfo.getStatus());
            result.setMessage(dataInfo.getErrorText());
        } catch (Exception e)
        {
            result.setCode("1");
            result.setMessage(e.getMessage());
            result.setData(resData);
        }
        return result;
    }


    /**
     * 返回成本BOQ状态
     * @return
     */
    @ApiOperation(value="返回成本BOQ状态", notes="返回成本BOQ状态", produces="application/json")
    @RequestMapping(value = "/syncBoqChk",method = RequestMethod.GET)
    public String syncBoqChk()
    {
        System.out.println("查询成本BOQ状态");
        var sql="select t1.phid billno,t1.chk_flg,t1.wf_flg,t2.bill_no pcno from \n" +
                "(\n" +
                "select phid,chk_flg,wf_flg,phid_pc from pms3_boq_bill where \n" +
                "phid not in (select phid from pms3_boq_bill where pms3_boq_bill_phid!=0 )   and   \n" +
                "phid not in (select pms3_boq_bill_phid from pms3_boq_bill where pms3_boq_bill_phid!=0 ) \n" +
                "and phid_cblx="+boqysflCost+"\n" +
                "union\n" +
                "select  pms3_boq_bill_phid phid,chk_flg,wf_flg,phid_pc from (select pms3_boq_bill.*,row_number() over (partition by pms3_boq_bill_phid  order by NG_INSERT_DT desc) as group_idx from pms3_boq_bill where  pms3_boq_bill_phid!=0 )\n" +
                "where  group_idx = '1' and phid_cblx="+boqysflCost+")t1\n" +
                "left join project_table t2 on t2.phid=t1.phid_pc";
//                "where (t1.user_tbjjsjk!='1' or user_tbjjsjk is null) and t1.cnt_type=5";
        RowMapper<BOQChkModel> rowMapper=new BeanPropertyRowMapper(BOQChkModel.class);
        List<BOQChkModel> boqChkModels= jdbcTemplate.query(sql, rowMapper);
        HttpPost httpPost  = new HttpPost(boqChkUrl);
        httpPost.addHeader("Content-Type","application/json");
        for (BOQChkModel v:boqChkModels) {
            if(v.getChk_flg()!=null&&v.getChk_flg().equals("1"))
            {
                v.setState("2");
            }
            else if(v.getWf_flg()!=null &&v.getWf_flg().equals("1"))
            {
                v.setState("1");
            }
            else
                v.setState("0");
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
                    }
                    else
                    {
                        System.out.println("推送失败"+root.getJSONObject("message").toJSONString());
                    }
                } else {
                    log.error("请求返回:" + state + "(" + boqChkUrl + ")");
                }
            }
            catch (Exception e)
            {
                System.out.println("推送异常"+e.getMessage());
            }
        }
        return "成功";
    }

}
