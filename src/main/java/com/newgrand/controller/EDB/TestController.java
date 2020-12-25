package com.newgrand.controller.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ChenXiangLu
 * @Date 2020/12/24 17:53
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class TestController {

    @Value("${spring.datasource.orcle.url}")
    private String orcle_url;
    @Value("${spring.datasource.orcle.username}")
    private String orcle_username;
    @Value("${spring.datasource.orcle.password}")
    private String orcle_password;
    @Value("${i8.url}")
    private String i8url;
    @Value("${i8.user}")
    private String user;
    @Value("${i8.database}")
    private String database;
    @Value("${i8.orgId}")
    private String orgId;
    @Value("${i8.edb.boqInScheme}")
    private String boqInScheme;
    @Value("${i8.edb.boqInChangeScheme}")
    private String boqInChangeScheme;
    @Value("${i8.edb.boqCostScheme}")
    private String boqCostScheme;
    @Value("${i8.edb.boqCostChangeScheme}")
    private String boqCostChangeScheme;
    @Value("${i8.edb.proCountScheme}")
    private String proCountScheme;
    @Value("${i8.edb.subPackagePlanScheme}")
    private String subPackagePlanScheme;
    @Value("${i8.edb.boqysflCost}")
    private String boqysflCost;
    @Value("${i8.edb.boqysflIn}")
    private String boqysflIn;
    @Value("${i8.edb.url.cnt}")
    private String cntUrl;
    @Value("${i8.edb.url.cbs}")
    private String cbsUrl;
    @Value("${i8.edb.url.repositoryLL}")
    private String repositoryLLUrl;
    @Value("${i8.edb.url.cntMeasureUrl}")
    private String cntMeasureUrl;
    @Value("${i8.edb.url.oddBillUrl}")
    private String oddBillUrl;
    @Value("${i8.edb.url.deviceUrl}")
    private String deviceUrl;
    @Value("${i8.edb.url.secureFeeBillUrl}")
    private String secureFeeBillUrl;
    @Value("${i8.edb.url.otherRealCostUrl}")
    private String otherRealCostUrl;
    @Value("${i8.edb.url.boqChkUrl}")
    private String boqChkUrl;

    @ApiOperation(value="查询环境变量", notes="查询环境变量")
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String test()
    {
        Map<String,Object> config=new HashMap<>();
        config.put("数据库链接串orcle_url",orcle_url);
        config.put("数据库登录名orcle_username",orcle_username);
        config.put("数据库密码orcle_password",orcle_password);
        config.put("i8地址i8url",i8url);
        config.put("模拟登录用户user",user);
        config.put("模拟登录账套database",database);
        config.put("组织orgId",orgId);
        config.put("收入BOQ方案boqInScheme",boqInScheme);
        config.put("收入BOQ变更方案boqInChangeScheme",boqInChangeScheme);
        config.put("成本BOQ方案boqCostScheme",boqCostScheme);
        config.put("成本BOQ变更方案boqCostChangeScheme",boqCostChangeScheme);
        config.put("产值方案proCountScheme",proCountScheme);
        config.put("分包计划方案subPackagePlanScheme",subPackagePlanScheme);
        config.put("预算分类成本boqysflCost",boqysflCost);
        config.put("预算分类收入boqysflIn",boqysflIn);
        config.put("经济数据库 合同地址cntUrl",cntUrl);
        config.put("经济数据库 CBS地址cbsUrl",cbsUrl);
        config.put("经济数据库 领料单地址repositoryLLUrl",repositoryLLUrl);
        config.put("经济数据库 分包结算地址cntMeasureUrl",cntMeasureUrl);
        config.put("经济数据库 零星费用单地址oddBillUrl",oddBillUrl);
        config.put("经济数据库 临时设施摊销地址deviceUrl",deviceUrl);
        config.put("经济数据库 安全费用计提地址secureFeeBillUrl",secureFeeBillUrl);
        config.put("经济数据库 其他实际成本地址otherRealCostUrl",otherRealCostUrl);
        config.put("经济数据库 boq审核状态回填地址boqChkUrl",boqChkUrl);
        for (String key : config.keySet()) {
            System.out.println(key + " :" + config.get(key));
        }
        return  JSONObject.toJSONString(config);
    }
}
