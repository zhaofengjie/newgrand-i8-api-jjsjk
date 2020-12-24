package com.newgrand.service.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.EDB.SubPackagePlanFeeModel;
import com.newgrand.domain.EDB.SubPackagePlanModel;
import com.newgrand.config.IJdbcTemplate;
import com.newgrand.domain.DataInfo;
import com.newgrand.helper.DateTranslate;
import com.newgrand.helper.I8Request;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分包计划
 * @Author ChenXiangLu
 * @Date 2020/12/7 10:18
 * @Version 1.0
 */
@Service
public class SubPackagePlanService {

    /**
     * 模拟登录的用户
     */
    @Value("${i8.user}")
    private String i8user;
    /**
     * 预算分类_目标成本的phid
     */
    @Value("${i8.edb.boqysflCost}")
    private String boqysflCost;
    /**
     * 分包计划方案phid
     */
    @Value("${i8.edb.subPackagePlanScheme}")
    private String subPackagePlanScheme;


    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected IJdbcTemplate jdbcTemplate;

    @Autowired
    private I8Request i8Request;

    /**
     * 同步分包计划
     * @param param
     * @return
     */
    public DataInfo saveSubPackagePlan(SubPackagePlanModel param)
    {
        DataInfo rvInfo = new DataInfo();
        String amt="0";//无税金额
        String sum="0";//含税金额
        String rate="0";//税额
        ArrayList<SubPackagePlanFeeModel> feeItemInfos=param.getFeeItemInfos();
        if(feeItemInfos!=null&&feeItemInfos.size()>0)
        {
            amt= String.valueOf(feeItemInfos.stream().mapToDouble(m->Double.parseDouble(m.getNoTaxTotal())).sum());
            sum= String.valueOf(feeItemInfos.stream().mapToDouble(m->Double.parseDouble(m.getTaxTotal())).sum());
            rate= String.valueOf(feeItemInfos.stream().mapToDouble(m->Double.parseDouble(m.getSjTotal())).sum());
        }
        String pcPhid=jdbcTemplate.queryForObject("select phid from project_table where pc_no='"+param.getCode()+"'",String.class);
        Long phid=jdbcTemplate.queryForObject("select phid from pms3_subc_m where bill_type='1' and  phid_pc="+pcPhid,Long.class);
        phid=phid==null?0:phid;
        List<NameValuePair> urlParameters = new ArrayList<>();
        Map<String, Object> mstformDatas=paramMstformData(param,pcPhid,phid,amt,sum,rate);

        ArrayList<Map<String, Object>> subitemcData=new ArrayList<>();
        for(SubPackagePlanFeeModel v:param.getFeeItemInfos())
        {
            Long itemPhid=jdbcTemplate.queryForObject("select phid from pms3_subc_d where user_jjsjkid='"+v.getId()+"'",Long.class);
            itemPhid=itemPhid==null?0:itemPhid;
            Map<String, Object> tempFee=gridDataFB(v,itemPhid,phid,pcPhid);
            subitemcData.add(tempFee);
        }
        StringBuilder mStr=new StringBuilder();
        StringBuilder subStr=new StringBuilder();

        ///region 表头拼接
        mStr.append("{\"form\":{\"key\":\"PhId\"");
        if(mstformDatas.get("key")==null||mstformDatas.get("key").equals("")) {
            mStr.append(",\"newRow\":" + JSONObject.toJSONString(mstformDatas));
        }
        else {
            mStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(mstformDatas));
        }
        mStr.append("}}");
        ///endregion

        ///region 表体拼接
        subStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> gfNew=new ArrayList<>();//subitemcData.stream().filter(f->f.get("key")==""||f.get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> gfModify= new ArrayList<>();// subitemcData.stream().filter(f->f.get("key")!=""&&!f.get("key").equals("")).collect(Collectors.toList());

        for(Map<String, Object> v :subitemcData)
        {
            if(((Map<String, Object>)v.get("row")).get("key").equals(""))
            {
                gfNew.add(v);
            }
            else
            {
                gfModify.add(v);
            }
        }
        if(gfNew.size()>0)
        {
            subStr.append(",\"newRow\":" + JSONObject.toJSONString(gfNew));
        }
        if(gfModify.size()>0)
        {
            subStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(gfModify));
        }
        subStr.append("},\"isChanged\":true}");
        ///endregion


        urlParameters.add(new BasicNameValuePair("subcmformData", mStr.toString()));
        urlParameters.add(new BasicNameValuePair("subitemcData", subStr.toString()));
        urlParameters.add(new BasicNameValuePair("squitemcData", ""));
        urlParameters.add(new BasicNameValuePair("subcddData", ""));
        urlParameters.add(new BasicNameValuePair("isContinue", "false"));
        urlParameters.add(new BasicNameValuePair("attchmentGuid", "0"));
        try {
            String i8rv = i8Request.PostFormSync("/PMS/PMS/ZY/SubCM/Save", urlParameters);
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
     * 封装参数MstformData 是否是新增判断key是否有值
     * @param param
     * @param pcPhid
     * @param phid
     * @param amt 无税金额
     * @param sum 含税金额
     * @param rate 税额
     * @return
     */
    public Map<String, Object> paramMstformData(SubPackagePlanModel param, String pcPhid, long phid,String amt,String sum,String rate) {
        String org=jdbcTemplate.queryForObject("select phid_org from project_table where phid="+pcPhid,String.class);
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String jbr=jdbcTemplate.queryForObject("select phid from hr_epm_main where cno='"+param.getUserCode()+"'",String.class);
        String jbrDept=jdbcTemplate.queryForObject("select dept from hr_epm_main where cno='"+param.getUserCode()+"'",String.class);
        Long ngRecordVer=jdbcTemplate.queryForObject("select ng_record_ver from pms3_subc_m where bill_type='1' and  phid_pc="+pcPhid,Long.class);
        ngRecordVer=ngRecordVer==null?0:ngRecordVer;
        Map<String, Object> row = new HashMap<>();
//        row.put("BillNo", SnowflakeIdWorker.generateId());
        row.put("BillNo", param.getCode());
        row.put("BillTitle", param.getName());
        row.put("user_djrq",  DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd"));
        row.put("PhidPc",  pcPhid);
        row.put("user_xmbm",  param.getCode());
        row.put("PhidBudgetType", boqysflCost);
        row.put("CVatAmt", amt );
        row.put("CPriSum",  sum);
        row.put("user_se",  rate);
        row.put("user_qj", "" );
        row.put("user_zz",  org);
        row.put("user_jbr", jbr );
        row.put("user_jbbm",  jbrDept);
        row.put("user_zdr", userId );
        row.put("BillDt ",  DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd"));
        row.put("Remarks", "");
        row.put("user_sl",  "");
        row.put("NgRecordVer", "" );
        row.put("ChkFlg", "0");
        row.put("PhidOcode", org );
        row.put("AsrFlg", "");
        row.put("PhId", "");
        row.put("WfFlg", "");
        row.put("BillType",  "1");
        row.put("PhidSchemeid", subPackagePlanScheme);
        row.put("PhidSourcemid","" );
        row.put("ItemResource", "");
        row.put("Creator", userId);
        row.put("PhidChkpsn", "");
        row.put("DaFlg", "");
        row.put("PhidTask", "");
        row.put("key", "");
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
            row.put("NgRecordVer", ngRecordVer);
        }
        return row;
    }

    /**
     * 封装参数boqdgridDataFB 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> gridDataFB(SubPackagePlanFeeModel itemInfo, long phid, long parentPhid, String pcPhid) {
        String timeValue=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        Long ngRecordVer=jdbcTemplate.queryForObject("select ng_record_ver from pms3_subc_d where user_jjsjkid='"+itemInfo.getId()+"'",Long.class);
        String org=jdbcTemplate.queryForObject("select phid_org from project_table where phid="+pcPhid,String.class);
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        ngRecordVer=ngRecordVer==null?0:ngRecordVer;
        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("PhId", "0");
        row.put("Pphid", parentPhid);
        row.put("NgInsertDt", timeValue);
        row.put("NgUpdateDt", timeValue);
        row.put("CurOrgId",  org);
        row.put("Creator",  userId);
        row.put("Editor", userId);
        row.put("NgRecordVer",  0);
        row.put("PhidItemData",  "0");
        row.put("PhidItemData_EXName", "" );
        row.put("PhidItemDetail",  "0");
        row.put("ResMasterData", "");
        row.put("PhidWork", "0" );
        row.put("PhidBoq",  "0");
        row.put("ChName", itemInfo.getName() );
        row.put("ChCode",  itemInfo.getCode());
        row.put("ChContent",  "");//项目特征
        row.put("PhidMsunit",msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
        row.put("PhidWbs",  "0");
        row.put("PhidWbs_EXName", "" );
        row.put("PhidCbs",  "0");
        row.put("PhidCbs_EXName", "" );
        row.put("OriginQty", itemInfo.getQuantity() );
        row.put("AfterChangeQty", itemInfo.getQuantity() );
        row.put("OriginPrice", itemInfo.getTaxRate() );
        row.put("AfterChangePrice", itemInfo.getTaxRate() );
        row.put("OriginAmt", itemInfo.getTaxTotal() );
        row.put("AfterChangeAmt", itemInfo.getTaxTotal() );
        row.put("OriginCalBasic", 0 );
        row.put("RestCalBasic",  0);
        row.put("OriginCalRate",  0);
        row.put("RestCalRate",     0 );
        row.put("GuestRate", 0 );
        row.put("LaborGuestAmt",  0);
        row.put("LaborGuestLaveAmt", 0 );
        row.put("LaborGuestLaveVatAmt",  0);
        row.put("LaveQty", 0 );
        row.put("LaveAmt", 0 );
        row.put("TaxRate", 0   );
        row.put("AfterTaxRate",0 );
        row.put("TaxAmt", itemInfo.getSjTotal() );
        row.put("AfterTaxAmt", itemInfo.getSjTotal() );
        row.put("VatPrice", itemInfo.getNoTaxRate() );
        row.put("AfterVatPrice", itemInfo.getNoTaxRate() );
        row.put("VatAmt", itemInfo.getNoTaxTotal());
        row.put("AfterVatAmt", itemInfo.getNoTaxTotal() );
        row.put("QualiReq", "");
        row.put("PlanDt", timeValue);
        row.put("AfterPlanDt", timeValue);
        row.put("Remarks", "");
        row.put("AfterRemarks", "");
        row.put("Source", "1");
        row.put("ResPropertys", "");
        row.put("PhidResBs", "0");
        row.put("PhidResBs_EXName", "");
        row.put("BillType", "1");
        row.put("DataFrom", "");
        row.put("ChangeAddFlg", "");
        row.put("PhidChange", "0");
        row.put("ImpInfo", "");
        row.put("RestQty", 0);
        row.put("RestAmtVat", 0);
        row.put("RestAmtVatFc", 0);
        row.put("RestAmt", 0);
        row.put("RestAmtFc", 0);
        row.put("ControlQty", 0);
        row.put("ControlAmtVat", 0);
        row.put("ControlAmtVatFc", 0);
        row.put("ControlAmt", 0);
        row.put("ControlAmtFc", 0);
        row.put("PhidSchemeid", "0");
        row.put("PhidSourcemid", "0");
        row.put("PhidSourceid", "0");
        row.put("ItemResource", "");
        row.put("CostName", "");
        row.put("CostDtl", "");
        row.put("user_mbcbze", "0");
        row.put("user_mbcbye", "0");
        row.put("key", "null");
        row.put("user_jjsjkid", itemInfo.getId());
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
            row.put("NgRecordVer", ngRecordVer);
        }
        newRow.put("row", row);
        return newRow;
    }

}
