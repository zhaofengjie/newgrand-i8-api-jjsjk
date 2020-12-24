package com.newgrand.controller.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.EDB.CbsModel;
import com.newgrand.config.IJdbcTemplate;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 成本科目
 * @Author ChenXiangLu
 * @Date 2020/11/27 17:46
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class CbsController {

    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected IJdbcTemplate jdbcTemplate;
    @Autowired
    private CloseableHttpClient httpClient;

    @Value("${i8.url}")
    private String i8url;
    @Value("${i8.edb.url.cbs}")
    private String cbsUrl;


    @ApiOperation(value="推送CBS数据到经济数据库", notes="推送CBS数据到经济数据库", produces="application/json")
    @RequestMapping(value = "/getCbs",method = RequestMethod.GET)
    public String syncCbs()
    {
        ///cbs_status这个是区分cbs清单库和项目cbs的
        // cbs_status='0' 表示清单库，
        // cbs_status='1'表示项目cbs引用清单库，
        // cbs_status=''  表示项目cbs自己新增
        var sql="select t1.phid,t1.cbs_code,t1.cbs_name,t1.note,t1.cbs,t3.cbs_code parent_code,t1.remarks,t2.pc_no\n" +
                "from bd_cbs t1\n" +
                "left join project_table t2 on t2.phid=t1.pcid\n" +
                "left join bd_cbs t3 on t3.phid=t1.parentphid\n" +
                "where t1.cbs_status!='0' and (t1.remarks is null or t1.remarks!='1') ";
        RowMapper<CbsModel> rowMapper=new BeanPropertyRowMapper(CbsModel.class);
        List<CbsModel> cbs= jdbcTemplate.query(sql, rowMapper);
        if(cbs==null||cbs.size()==0)
        {
            log.info("CBS同步接口:无需要推送的项目CBS数据");
            return "无需要推送的项目CBS数据";
        }
        HttpPost httpPost  = new HttpPost(cbsUrl);
        httpPost.addHeader("Content-Type","application/json");
        HttpEntity entity = new StringEntity("{\"cbs_data\":"+JSONObject.toJSONString(cbs)+"}","utf-8");
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
                List<String> phids=cbs.stream().map(m->m.getCbs_code()).collect(Collectors.toList());
                jdbcTemplate.execute("update bd_cbs set remarks='1' where phid in ("+ StringUtils.join(phids,",") +")");
            }
            else
                {
                System.out.println("推送失败"+ EntityUtils.toString(response.getEntity()));
            }
        }
        catch (Exception e)
        {
            System.out.println("推送异常"+e.getMessage());
        }
        return  "测试成功";
    }
}
