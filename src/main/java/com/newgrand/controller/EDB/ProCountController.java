package com.newgrand.controller.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.EDB.EDBResultModel;
import com.newgrand.domain.EDB.ProCountBQModel;
import com.newgrand.domain.EDB.ProCountModel;
import com.newgrand.config.IJdbcTemplate;
import com.newgrand.domain.DataInfo;
import com.newgrand.domain.EDB.*;
import com.newgrand.helper.DateTranslate;
import com.newgrand.helper.EntityConverter;
import com.newgrand.helper.I8Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    protected IJdbcTemplate jdbcTemplate;
    @Autowired
    private I8Request i8Request;
    private String mStr="{\"BillNo\":\"\",\"Title\":\"\",\"BillDt\":\"2020-12-15\",\"PhidPc\":\"\",\"user_xmbm\":\"\",\"AmtSum\":0,\"user_bqsjczhs\":0,\"user_sl\":0,\"user_bqse\":0,\"AmtYearSum\":0,\"user_bnljczhs\":0,\"user_bnljse\":0,\"PhidCycle\":\"\",\"AmtAllSum\":0,\"user_kgljczhs\":0,\"user_kgljse\":0,\"PhidYsfl\":\"11\",\"user_jbr\":\"0\",\"user_jbbm\":\"0\",\"user_zdr\":\"0\",\"user_zdrq\":\"2020-12-15\",\"JdDesc\":\"\",\"PlanType\":3,\"Remarks\":\"\",\"PhidTjOrg\":\"\",\"PhidJsOrg\":\"0\",\"PointId\":\"\",\"PhId\":\"\",\"CurOrgId\":\"\",\"PhidSchemeid\":\"155201102000027\",\"NgUpdateDt\":\"\",\"Editor\":\"\",\"WfFlg\":\"\",\"DaFlg\":\"\",\"PhidChkpsn\":\"\",\"ChkDt\":\"\",\"ChkFlg\":\"0\",\"PhidOcode\":\"438201029000001\",\"LevelCode\":\"\",\"NgInsertDt\":\"\",\"NgRecordVer\":\"\",\"Creator\":\"\",\"PhidTask\":\"\",\"AsrFlg\":\"\",\"key\":\"\"}";
    private String gridStr="{\"PhId\":\"\",\"Pphid\":\"\",\"NgInsertDt\":null,\"NgUpdateDt\":null,\"NgRecordVer\":0,\"Creator\":\"\",\"Editor\":\"\",\"CurOrgId\":\"\",\"ImpInfo\":\"\",\"PhidBoq\":\"\",\"Rflag\":\"5\",\"Cno\":\"\",\"CzName\":\"\",\"BoqMtype\":1,\"ItemNo\":\"\",\"ItemName\":\"\",\"PhidMsunit\":\"0\",\"PlanQty\":0,\"PlanAmt\":0,\"NexplanQty\":0,\"NexplanAmt\":0,\"Prc\":0,\"Qty\":0,\"Amt\":0,\"Ftype\":\"*\",\"PhidWbs\":\"0\",\"PhidWbs_EXName\":\"\",\"PhidCbs\":\"\",\"PhidCbs_EXName\":\"\",\"PhidFbdw\":\"\",\"PhidFbdw_EXName\":\"\",\"DataFrom\":\"1\",\"DataFrom_EXNAME\":\"从BOQ引用\",\"Remarks\":\"\",\"PhidBoqLevel\":\"0\",\"PhidBoqLevel_EXName\":\"\",\"QtyYear\":0,\"AmtYear\":0,\"QtyAll\":0,\"AmtAll\":0,\"Character\":\"\",\"PhidCnt\":\"\",\"PhidCnt_EXName\":\"\",\"PhidCntd\":\"\",\"BoqmQty\":\"0\",\"ConQty\":\"0\",\"ConAmt\":\"0\",\"RestQty\":\"0\",\"RestAmt\":\"0\",\"QtyScale\":\"0\",\"QtyYearScale\":\"\",\"QtyAllScale\":\"\",\"RestBoqQty\":\"0\",\"UnitName\":\"\",\"PlanQtyT\":0,\"PlanQtyT2\":0,\"user_htmc_name\":\"\",\"user_htmc\":\"\",\"user_qdsyl\":0,\"user_qdsycz\":0,\"user_jjsjkid\":\"\",\"key\":null}";


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
            result.setCode(rvInfo.getStatus());
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
        var pcPhid=jdbcTemplate.queryForObject("select phid from project_table where pc_no='"+param.getCode()+"'",String.class);
        if(pcPhid==null)
        {
            rvInfo.setStatus("1");
            rvInfo.setErrorText("项目编码不存在:"+param.getCode());
            return rvInfo;
        }
        var chkflg=jdbcTemplate.queryForObject("select nvl(wf_flg,0)+nvl(chk_flg,0) from pms3_cz_act_m where user_jjsjkid='"+ param.getCode()+ param.getCycle()+"'",Long.class);
        if(chkflg!=null && chkflg!=0)
        {
            rvInfo.setStatus("1");
            rvInfo.setErrorText("此单据已经发起工作流或已审核:"+param.getCode());
            return rvInfo;
        }

        List<String> msunits=new ArrayList<>();
        msunits.add("m");
        msunits.addAll(param.getBqItemProductionInfos().stream().map(m->m.getUnit()).collect(Collectors.toList()));
        msunits.addAll(param.getMeasureItemProductionInfos().stream().map(m->m.getUnit()).collect(Collectors.toList()));
        msunits.addAll(param.getOtherItemProductionInfos().stream().map(m->m.getUnit()).collect(Collectors.toList()));
        msunits=msunits.stream().distinct().collect(Collectors.toList());
        List<Map<String,Object>> msunitPhids=jdbcTemplate.queryForList("select phid,msname from msunit where msname in ('"+ StringUtils.join(msunits,"','")+"')");
//        if(msunits.size()!=msunitPhids.size())
//        {
//            for(Map<String, Object> v:msunitPhids)
//            {
//                if(msunits.contains(v.get("MSNAME")))
//                {
//                    msunits.remove(v.get("MSNAME"));
//                }
//            }
//            rvInfo.setCode("1");
//            rvInfo.setStatus("400");
//            rvInfo.setErrorText("单位不存在:"+JSONObject.toJSONString(msunits));
//            return rvInfo;
//        }

        List<String> boqs=new ArrayList<>();
        boqs.addAll(param.getBqItemProductionInfos().stream().map(m->m.getId()).collect(Collectors.toList()));
        boqs.addAll(param.getMeasureItemProductionInfos().stream().map(m->m.getId()).collect(Collectors.toList()));
        boqs.addAll(param.getOtherItemProductionInfos().stream().map(m->m.getId()).collect(Collectors.toList()));
        boqs=boqs.stream().distinct().collect(Collectors.toList());
        List<Map<String,Object>> boqPhids=jdbcTemplate.queryForList("select phid,user_jjsjk from pms3_boq_m where phid_pc="+pcPhid+" and  phid_cblx="+boqysflIn+" and  user_jjsjk in ('"+ StringUtils.join(boqs,"','")+"')");
        if(boqs.size()!=boqPhids.size())
        {
            for(Map<String, Object> v:boqPhids)
            {
                if(boqs.contains(v.get("USER_JJSJK")))
                {
                    boqs.remove(v.get("USER_JJSJK"));
                }
            }
            rvInfo.setCode("1");
            rvInfo.setStatus("400");
            rvInfo.setErrorText("BOQ清单不存在:"+JSONObject.toJSONString(boqs));
            return rvInfo;
        }

        Long sonPhid=-1L;
        var phid=jdbcTemplate.queryForObject("select phid from pms3_cz_act_m where user_jjsjkid='"+ param.getCode()+ param.getCycle()+"'",Long.class);
        paramMstformData=paramMstformData(param,phid);
        for(ProCountBQModel v :param.getBqItemProductionInfos())
        {
            var tempPhid=jdbcTemplate.queryForObject("select phid from pms3_cz_act_d where user_jjsjkid='"+v.getId()+ param.getCycle()+"'",Long.class);
            if(tempPhid==null) {
                sonPhid--;
                tempPhid = sonPhid;
            }
            Map<String, Object> tempGrid=paramCzactdgridData(v,tempPhid,param.getCycle(),pcPhid,msunitPhids);
            paramCzactdgridData.add(tempGrid);
        }
        for(ProCountBQModel v :param.getMeasureItemProductionInfos())
        {
            var tempPhid=jdbcTemplate.queryForObject("select phid from pms3_cz_act_d where user_jjsjkid='"+v.getId()+ param.getCycle()+"'",Long.class);
            if(tempPhid==null) {
                sonPhid--;
                tempPhid = sonPhid;
            }
            Map<String, Object> tempGrid=paramCzactdgridData(v,tempPhid,param.getCycle(),pcPhid,msunitPhids);
            paramCzactdgridData.add(tempGrid);
        }
        for(ProCountBQModel v :param.getOtherItemProductionInfos())
        {
            var tempPhid=jdbcTemplate.queryForObject("select phid from pms3_cz_act_d where user_jjsjkid='"+v.getId()+ param.getCycle()+"'",Long.class);
            if(tempPhid==null) {
                sonPhid--;
                tempPhid = sonPhid;
            }
            Map<String, Object> tempGrid=paramCzactdgridData(v,tempPhid,param.getCycle(),pcPhid,msunitPhids);
            paramCzactdgridData.add(tempGrid);
        }
        StringBuilder mStr=new StringBuilder();
        StringBuilder gridStr=new StringBuilder();

        ///region 表头拼接
        mStr.append("{\"form\":{\"key\":\"PhId\"");
        if(phid==null||phid.equals("")) {
            mStr.append(",\"newRow\":" + JSONObject.toJSONString(paramMstformData));
        }
        else {
            mStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(paramMstformData));
        }
        mStr.append("}}");
        ///endregion
        gridStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> gridNew= (ArrayList<Map<String, Object>>) paramCzactdgridData.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")==null||((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> gridModify= (ArrayList<Map<String, Object>>) paramCzactdgridData.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")!=null&&!((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
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
//        urlParameters.add(new BasicNameValuePair("mstformData", "{\"form\":{\"key\":\"PhId\",\"modifiedRow\":{\"BillNo\":\"202012150003\",\"Title\":\"测试2\",\"BillDt\":\"2020-12-15\",\"PhidPc\":\"403201128000001\",\"user_xmbm\":\"202011280001\",\"AmtSum\":1200,\"user_bqsjczhs\":0,\"user_sl\":0,\"user_bqse\":0,\"AmtYearSum\":1200,\"user_bnljczhs\":0,\"user_bnljse\":0,\"PhidCycle\":\"603201111003058\",\"AmtAllSum\":1500,\"user_kgljczhs\":0,\"user_kgljse\":0,\"PhidYsfl\":\"11\",\"user_jbr\":\"930201102000001\",\"user_jbbm\":\"438201029000007\",\"user_zdr\":\"438201029000011\",\"user_zdrq\":\"2020-12-15\",\"JdDesc\":\"\",\"PlanType\":3,\"Remarks\":\"\",\"PhidTjOrg\":\"0\",\"PhidJsOrg\":\"0\",\"PointId\":\"\",\"PhId\":\"217201215000003\",\"CurOrgId\":\"438201029000001\",\"PhidSchemeid\":\"155201102000027\",\"NgUpdateDt\":\"2020-12-15 20:18:25\",\"Editor\":\"438201029000011\",\"WfFlg\":\"\",\"DaFlg\":\"0\",\"PhidChkpsn\":\"0\",\"ChkDt\":\"\",\"ChkFlg\":\"0\",\"PhidOcode\":\"438201029000001\",\"LevelCode\":\"0\",\"NgInsertDt\":\"2020-12-15 20:18:21\",\"NgRecordVer\":\"6\",\"Creator\":\"438201029000011\",\"PhidTask\":\"\",\"AsrFlg\":\"\",\"key\":\"217201215000003\"}}}"));
//        urlParameters.add(new BasicNameValuePair("czactdgridData", "{\"table\":{\"key\":\"PhId\"}}"));
        urlParameters.add(new BasicNameValuePair("bustype", "CZ_ACT_M"));
        urlParameters.add(new BasicNameValuePair("isContinue",  "false"));
        urlParameters.add(new BasicNameValuePair("attchmentGuid",  "0"));
        try {
            String i8rv = i8Request.PostFormSync("/PMS/PMS/CZ/CzActM/Save", urlParameters);
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
    public Map<String, Object> paramMstformData(ProCountModel itemInfo, Long phid) {
        String cycleValue= itemInfo.getCycle()+"-01";
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String pcPhid=jdbcTemplate.queryForObject("select phid from project_table where pc_no='"+itemInfo.getCode()+"'",String.class);
        String jbrPhid=jdbcTemplate.queryForObject("select phid from hr_epm_main where cno='"+itemInfo.getUserCode()+"'",String.class);
        String jbrDeptPhid=jdbcTemplate.queryForObject("select dept from hr_epm_main where cno='"+itemInfo.getUserCode()+"'",String.class);
        String cyclePhid=jdbcTemplate.queryForObject("select phid from fg3_workcycle where ctype='GCMONTH' and  bdt >=to_date('"+cycleValue+"', 'yyyy-MM-dd') and  bdt <=to_date('"+cycleValue+"', 'yyyy-MM-dd')",String.class);
        String curOrgId=jdbcTemplate.queryForObject("select cur_orgid from fg3_user where userno='"+i8user+"'",String.class);
        String ngRecordVer=jdbcTemplate.queryForObject("select ng_record_ver from pms3_cz_act_m where phid="+phid,String.class);
        ngRecordVer=ngRecordVer==null?"0":ngRecordVer;
        String billDate=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd");

        HashMap<String, Object> row = new HashMap<>();
        row.put("PhId", "");
        row.put("BillNo", itemInfo.getCode());
        row.put("Title", itemInfo.getName());
        row.put("BillDt", billDate);
        row.put("PhidPc", pcPhid);
        row.put("user_xmbm", itemInfo.getCode());
//        row.put("AmtSum", "");
//        row.put("user_bqsjczhs", "");
//        row.put("user_sl", "");
//        row.put("user_bqse", "");
//        row.put("AmtYearSum", "");
//        row.put("user_bnljczhs", "");
//        row.put("user_bnljse", "");
        row.put("PhidCycle", cyclePhid);
//        row.put("AmtAllSum", "");
//        row.put("user_kgljczhs", "");
//        row.put("user_kgljse", "");
        row.put("PhidYsfl", boqysflIn);
        row.put("user_jbr",  jbrPhid);
        row.put("user_jbbm",  jbrDeptPhid);
        row.put("user_zdr",  userId);
        row.put("user_zdrq",  billDate);
//        row.put("JdDesc", "");
//        row.put("Remarks", "");
        row.put("PlanType",  3);
//        row.put("PhidTjOrg",  "0");//统计单位
//        row.put("PhidJsOrg",  "");//建设单位
//        row.put("PointId", "");
        row.put("CurOrgId", curOrgId);
        row.put("PhidSchemeid",  proCountScheme);
        row.put("NgUpdateDt", billDate);
        row.put("Editor",  userId);
//        row.put("WfFlg", "");
//        row.put("DaFlg", "0");
//        row.put("PhidChkpsn", "0");
//        row.put("ChkDt", "");
//        row.put("ChkFlg", "0");
        row.put("PhidOcode", orgId);
        row.put("LevelCode", "0");
        row.put("NgInsertDt", billDate);
        row.put("NgRecordVer", ngRecordVer);
        row.put("Creator", userId );
//        row.put("PhidTask", "");
//        row.put("AsrFlg", "");
        row.put("key", "");
        row.put("user_jjsjkid", itemInfo.getCode()+ itemInfo.getCycle());//项目编码+期间 作为唯一值
        if(phid!=null&&phid>0)
        {;
            row.put("key", phid);
            row.put("PhId", phid);
        }

        row=new EntityConverter().SetFieldMap(mStr, row);
        return row;
    }

    /**
     * 封装参数paramCzactdgridData 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> paramCzactdgridData(ProCountBQModel itemInfo, Long phid,String cycle,String pcPhid,List<Map<String,Object>> msunitPhids) {

//        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        String msunitPhid="";
        for (Map<String,Object> v: msunitPhids) {
            if(v.get("MSNAME").equals(itemInfo.getUnit())||v.get("MSNAME").equals("m"))
            {
                msunitPhid=String.valueOf(v.get("PHID"));
                break;
            }
        }
        String billTime=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String curOrgId=jdbcTemplate.queryForObject("select cur_orgid from fg3_user where userno='"+i8user+"'",String.class);
        String boqPhid=jdbcTemplate.queryForObject("select  phid from pms3_boq_m where phid_pc="+pcPhid+" and   user_jjsjk='"+itemInfo.getId()+"' and phid_cblx="+boqysflIn,String.class);
        String boqMtype=jdbcTemplate.queryForObject("select  m_type from pms3_boq_m where phid_pc="+pcPhid+" and  user_jjsjk='"+itemInfo.getId()+"' and phid_cblx="+boqysflIn,String.class);
        String wbsPhid=jdbcTemplate.queryForObject("select  phid_wbs from pms3_boq_m where phid_pc="+pcPhid+" and  user_jjsjk='"+itemInfo.getId()+"' and phid_cblx="+boqysflIn,String.class);
        String ngRecordVer=jdbcTemplate.queryForObject("select ng_record_ver from pms3_cz_act_m where phid="+phid,String.class);
        ngRecordVer=ngRecordVer==null?"0":ngRecordVer;

        Map<String, Object> newRow = new HashMap<>();
        HashMap<String, Object> row = new HashMap<>();
        row.put("PhId", phid);
        row.put("NgInsertDt", billTime);
        row.put("NgUpdateDt", billTime);
        row.put("NgRecordVer", ngRecordVer);
        row.put("Creator", userId);
        row.put("Editor", userId);
        row.put("CurOrgId", curOrgId);
//        row.put("ImpInfo", "");
        row.put("PhidBoq", boqPhid );
        row.put("Rflag", "5");
//        row.put("Cno",  "" );
//        row.put("CzName", "" );
        row.put("BoqMtype", boqMtype );
        row.put("ItemNo", itemInfo.getCode());
        row.put("ItemName", itemInfo.getName() );
        row.put("PhidMsunit",  msunitPhid);
//        row.put("PlanQty", 0 );
//        row.put("PlanAmt", 0 );
//        row.put("NexplanQty", 0 );
//        row.put("NexplanAmt", 0 );
//        row.put("Prc",  0);
//        row.put("Qty",  0);
//        row.put("Amt", 0 );
//        row.put("Ftype",  "*");
        row.put("PhidWbs",  wbsPhid);
//        row.put("PhidWbs_EXName", "" );
//        row.put("PhidCbs", "0" );
//        row.put("PhidCbs_EXName",  "" );
//        row.put("PhidFbdw", "0");
//        row.put("PhidQuota_EXName", "");
        row.put("DataFrom", "1" );
        row.put("DataFrom_EXNAME",  "从BOQ引用");
//        row.put("Remarks",  "");
//        row.put("PhidBoqLevel", 0);
//        row.put("PhidBoqLevel_EXName", 0);
//        row.put("QtyYear", 0 );
//        row.put("AmtYear",   0);
//        row.put("QtyAll", 0 );
        row.put("AmtAll", itemInfo.getAccumulativeProductionValue() );
//        row.put("Character", "" );
//        row.put("PhidCnt",  "0");
//        row.put("PhidCnt_EXName", "" );
//        row.put("PhidCntd", "0" );
//        row.put("BoqmQty", 0);
        row.put("ConQty", itemInfo.getMonthQuantity());
        row.put("ConAmt", itemInfo.getMonthProductionValue());
//        row.put("RestQty", 0);
        row.put("RestAmt", itemInfo.getSurplusProductionValue());
//        row.put("QtyScale", 0);
//        row.put("RestBoqQty", 0);
//        row.put("UnitName", 0);
//        row.put("PlanQtyT", 0);
//        row.put("PlanQtyT2", 0);
        row.put("user_jjsjkid", itemInfo.getId()+ cycle);//项目编码+期间 作为唯一值
        row.put("key", "");
        if(phid>0)
        {
            row.put("key", phid);
        }

        row=new EntityConverter().SetFieldMap(gridStr, row);
        newRow.put("row", row);
        return newRow;
    }

}
