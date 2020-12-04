package com.newgrand.secdev.controller.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.domain.DataInfo;
import com.newgrand.secdev.domain.EDB.*;
import com.newgrand.secdev.helper.DateTranslate;
import com.newgrand.secdev.helper.I8Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产值接口
 * @Author ChenXiangLu
 * @Date 2020/11/25 21:06
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class ProCountController {

    /**
     * 默认组织
     */
    @Value("${i8.orgId}")
    private String orgId;
    /**
     * 模拟登录的用户
     */
    @Value("${i8.user}")
    private String i8user;
    /**
     * 产值方案id
     */
    @Value("${i8.edb.proCountScheme}")
    private String proCountScheme;
    /**
     * 预算分类_合同收入的phid
     */
    @Value("${i8.edb.boqysflIn}")
    private String boqysflIn;
    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    private I8Request i8Request;

    @ApiOperation(value="测试", notes="测试", produces="application/json")
    @RequestMapping(value = "/Get",method = RequestMethod.GET)
    public String syncProCount()
    {
        return  "测试成功";
    }

    @ApiOperation(value="接收产值数据", notes="接收产值数据", produces="application/json")
    @RequestMapping(value = "/syncProCount",method = RequestMethod.POST)
    public EDBResultModel<ArrayList<EDBResultModel>> syncProCount(@RequestBody ProCountModel param)
    {
        log.info("接收产值数据"+ JSONObject.toJSONString(param));
        System.out.println("接收产值数据"+ JSONObject.toJSONString(param));
        var result=new EDBResultModel<ArrayList<EDBResultModel>>();
        try {
            DataInfo rvInfo = saveProCount(param);
            result.setCode(rvInfo.getCode());
            result.setMessage(rvInfo.getErrorText());
        }
        catch (Exception e) {
            result.setCode("1");
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 同步数据
     * @param param
     * @return
     */
    public DataInfo saveProCount(ProCountModel param)
    {
        Map<String, Object> paramMstformData=new HashMap<>();
        ArrayList<Map<String, Object>> paramCzactdgridData=new ArrayList<>();
        DataInfo rvInfo = new DataInfo();
        long sonPhid=-1;
        var pcPhid=jdbcTemplate.queryForObject("select phid from project_table where bil_no='"+param.getCode()+"'",String.class);
        var phid=jdbcTemplate.queryForObject("select phid from pms3_cz_act_m where bil_no='"+param.getCode()+"'",Long.class);
        String parentPhid=phid==0?"":phid.toString();
        paramMstformData=paramMstformData(param,phid);
        for(ProCountBQModel v :param.getBqItemProductionInfos())
        {
            sonPhid-=1;
            Map<String, Object> tempGrid=paramCzactdgridData(v,sonPhid,parentPhid,pcPhid);
            paramCzactdgridData.add(tempGrid);
        }
        for(ProCountBQModel v :param.getMeasureItemProductionInfos())
        {
            sonPhid-=1;
            Map<String, Object> tempGrid=paramCzactdgridData(v,sonPhid,parentPhid,pcPhid);
            paramCzactdgridData.add(tempGrid);
        }
        for(ProCountBQModel v :param.getOtherItemProductionInfos())
        {
            sonPhid-=1;
            Map<String, Object> tempGrid=paramCzactdgridData(v,sonPhid,parentPhid,pcPhid);
            paramCzactdgridData.add(tempGrid);
        }
        StringBuilder mStr=new StringBuilder();
        StringBuilder gridStr=new StringBuilder();

        ///region 表头拼接
        mStr.append("{\"table\":{\"key\":\"PhId\"");
        if(phid==null||phid.equals("")) {
            mStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(paramMstformData.get(0)));
        }
        else {
            mStr.append(",\"newRow\":" + JSONObject.toJSONString(paramMstformData.get(0)));
        }
        mStr.append("}}");
        ///endregion
        gridStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> gridNew= (ArrayList<Map<String, Object>>) paramCzactdgridData.stream().filter(f->f.get("key")==null||f.get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> gridModify= (ArrayList<Map<String, Object>>) paramCzactdgridData.stream().filter(f->f.get("key")!=null&&!f.get("key").equals("")).collect(Collectors.toList());
        if(gridNew.size()>0)
        {
            gridStr.append(",\"newRow\":" + JSONObject.toJSONString(gridNew));
        }
        if(gridModify.size()>0)
        {
            gridStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(gridModify));
        }
        gridStr.append("},\"isChanged\":true}");
        ///region 表体拼接

        ///endregion

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("mstformData", mStr.toString()));
        urlParameters.add(new BasicNameValuePair("czactdgridData", gridStr.toString()));
        urlParameters.add(new BasicNameValuePair("bustype", "CZ_ACT_M"));
        urlParameters.add(new BasicNameValuePair("isContinue",  "false"));
        urlParameters.add(new BasicNameValuePair("attchmentGuid",  "0"));
        try {
            String i8rv = i8Request.PostFormSync("/PMS/PCO/BOQEQ/BoqBill/save", urlParameters);
            JSONObject i8rvJson = JSON.parseObject(i8rv);
            if (i8rvJson != null && i8rvJson.getString("Status").toLowerCase().equals("success")) {
                rvInfo.setStatus("0");
                rvInfo.setErrorText("记录保存成功");
            } else {
                rvInfo.setStatus("1");
                rvInfo.setErrorText(i8rv);
            }
        }
        catch (Exception e)
        {
            rvInfo.setStatus("1");
            rvInfo.setErrorText(e.getMessage());
        }
        return rvInfo;
    }


    /**
     * 封装参数paramMstformData 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> paramMstformData(ProCountModel itemInfo, long phid) {
        String cycleValue= itemInfo.getCycle()+"-01";
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String pcPhid=jdbcTemplate.queryForObject("select phid from project_table where pc_no='"+itemInfo.getCode()+"'",String.class);
        String jbrPhid=jdbcTemplate.queryForObject("select phid from hr_epm_main where userno='"+itemInfo.getUserCode()+"'",String.class);
        String jbrDeptPhid=jdbcTemplate.queryForObject("select dept from hr_epm_main where userno='"+itemInfo.getUserCode()+"'",String.class);
        String cyclePhid=jdbcTemplate.queryForObject("select   * from fg3_workcycle where bdt >=TO_DATE('"+cycleValue+"', 'yyyy-MM-dd') and  bdt <TO_DATE('"+cycleValue+"', 'yyyy-MM-dd') and =1",String.class);
        String curOrgId=jdbcTemplate.queryForObject("select cur_orgid from fg3_user where userno='"+i8user+"'",String.class);

        String billDate=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd");

        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("PhId", "");
        row.put("BillNo", itemInfo.getCode());
        row.put("Title", itemInfo.getName());
        row.put("BillDt", billDate);
        row.put("PhidPc", pcPhid);
        row.put("user_xmbm", itemInfo.getCode());
        row.put("AmtSum", "");
        row.put("user_bqsjczhs", "");
        row.put("user_sl", "");
        row.put("user_bqse", "");
        row.put("AmtYearSum", "");
        row.put("user_bnljczhs", "");
        row.put("user_bnljse", "");
        row.put("PhidCycle", cyclePhid);
        row.put("AmtAllSum", "");
        row.put("user_kgljczhs", "");
        row.put("user_kgljse", "");
        row.put("PhidYsfl", boqysflIn);
        row.put("user_jbr",  jbrPhid);
        row.put("user_jbbm",  jbrDeptPhid);
        row.put("user_zdr",  userId);
        row.put("user_zdrq",  billDate);
        row.put("JdDesc", "");
        row.put("Remarks", "");
        row.put("PlanType",  3);
        row.put("PhidTjOrg",  "0");//统计单位
        row.put("PhidJsOrg",  "");//建设单位
        row.put("PointId", "");
        row.put("CurOrgId", curOrgId);
        row.put("PhidSchemeid",  proCountScheme);
        row.put("NgUpdateDt", billDate);
        row.put("Editor",  userId);
        row.put("WfFlg", "");
        row.put("DaFlg", "0");
        row.put("PhidChkpsn", "0");
        row.put("ChkDt", "");
        row.put("ChkFlg", "0");
        row.put("PhidOcode", orgId);
        row.put("LevelCode", "0");
        row.put("NgInsertDt", billDate);
        row.put("NgRecordVer", "0");
        row.put("Creator", userId );
        row.put("PhidTask", "");
        row.put("AsrFlg", "");
        row.put("key", "");
        if(phid>0)
        {;
            row.put("key", phid);
            row.put("PhId", phid);
        }
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装参数paramCzactdgridData 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> paramCzactdgridData(ProCountBQModel itemInfo, long phid, String parentPhid, String pcPhid) {

        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        String billTime=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String curOrgId=jdbcTemplate.queryForObject("select cur_orgid from fg3_user where userno='"+i8user+"'",String.class);
        String boqPhid=jdbcTemplate.queryForObject("select  phid from pms3_boq_m where phid_pc="+pcPhid+" and code='"+itemInfo.getCode()+"'",String.class);
        String boqMtype=jdbcTemplate.queryForObject("select  m_type from pms3_boq_m where phid_pc="+pcPhid+" and code='"+itemInfo.getCode()+"'",String.class);
        String wbsPhid=jdbcTemplate.queryForObject("select  phid_wbs from pms3_boq_m where phid_pc="+pcPhid+" and code='"+itemInfo.getCode()+"'",String.class);

        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("PhId", "");
        row.put("Pphid", parentPhid);
        row.put("Ppphid", "");
        row.put("NgInsertDt", billTime);
        row.put("NgUpdateDt", billTime);
        row.put("NgRecordVer", 1);
        row.put("Creator", userId);
        row.put("Editor", userId);
        row.put("CurOrgId", curOrgId);
        row.put("ImpInfo", "");
        row.put("PhidBoq", boqPhid );
        row.put("Rflag", "5");
        row.put("Cno",  "" );
        row.put("CzName", "" );
        row.put("BoqMtype", boqMtype );
        row.put("ItemNo", itemInfo.getCode());
        row.put("ItemName", itemInfo.getName() );
        row.put("PhidMsunit",  msunitPhid);
        row.put("PlanQty", 0 );
        row.put("PlanAmt", 0 );
        row.put("NexplanQty", 0 );
        row.put("NexplanAmt", 0 );
        row.put("Prc",  0);
        row.put("Qty",  0);
        row.put("Amt", 0 );
        row.put("Ftype",  "*");
        row.put("PhidWbs",  wbsPhid);
        row.put("PhidWbs_EXName", "" );
        row.put("PhidCbs", "0" );
        row.put("PhidCbs_EXName",  "" );
        row.put("PhidFbdw", "0");
        row.put("PhidQuota_EXName", "");
        row.put("DataFrom", "1" );
        row.put("DataFrom_EXNAME",  "从BOQ引用");
        row.put("Remarks",  "");
        row.put("PhidBoqLevel", 0);
        row.put("PhidBoqLevel_EXName", 0);
        row.put("QtyYear", 0 );
        row.put("AmtYear",   0);
        row.put("QtyAll", 0 );
        row.put("AmtAll", itemInfo.getAccumulativeProductionValue() );
        row.put("Character", "" );
        row.put("PhidCnt",  "0");
        row.put("PhidCnt_EXName", "" );
        row.put("PhidCntd", "0" );
        row.put("BoqmQty", 0);
        row.put("ConQty", itemInfo.getMonthQuantity());
        row.put("ConAmt", itemInfo.getMonthProductionValue());
        row.put("RestQty", 0);
        row.put("RestAmt", itemInfo.getSurplusProductionValue());
        row.put("QtyScale", 0);
        row.put("RestBoqQty", 0);
        row.put("UnitName", 0);
        row.put("PlanQtyT", 0);
        row.put("PlanQtyT2", 0);
        row.put("key", null);
        if(phid>0)
        {;
            row.put("key", phid);
        }
        newRow.put("row", row);
        return newRow;
    }

}
