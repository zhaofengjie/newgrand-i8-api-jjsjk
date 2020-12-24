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
import java.util.stream.Collectors;

/**
 * 分包计划变更
 * @Author ChenXiangLu
 * @Date 2020/12/7 14:45
 * @Version 1.0
 */
@Service
public class SubPackagePlanChangeService {

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
    public DataInfo saveSubPackagePlanChange(SubPackagePlanModel param)
    {
        DataInfo rvInfo = new DataInfo();
        String amt="0";//无税金额
        String rate="0";//税额
        ArrayList<SubPackagePlanFeeModel> feeItemInfos=param.getFeeItemInfos();
        if(feeItemInfos!=null&&feeItemInfos.size()>0)
        {
            amt= String.valueOf(feeItemInfos.stream().mapToDouble(m->Double.parseDouble(m.getNoTaxTotal())).sum());
            rate= String.valueOf(feeItemInfos.stream().mapToDouble(m->Double.parseDouble(m.getSjTotal())).sum());
        }
        String pcPhid=jdbcTemplate.queryForObject("select phid from project_table where pc_no='"+param.getCode()+"'",String.class);
        Long phid=jdbcTemplate.queryForObject("select phid from pms3_subc_change_m where bill_type='1' and chk_flg!=1 and  phid_pc="+pcPhid,Long.class);
        phid=phid==null?0:phid;
        List<NameValuePair> urlParameters = new ArrayList<>();
        Map<String, Object> mstformDatas=paramMstformData(param,pcPhid,phid,amt,rate);

        ArrayList<Map<String, Object>> subitemcData=new ArrayList<>();
        for(SubPackagePlanFeeModel v:param.getFeeItemInfos())
        {
            Long itemPhid=jdbcTemplate.queryForObject("select phid from pms3_subc_change_d where pphid="+phid+" and user_jjsjkid='"+v.getId()+"'",Long.class);
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
        ArrayList<Map<String, Object>> gfNew= (ArrayList<Map<String, Object>>) subitemcData.stream().filter(f->((Map<String, Object>)f.get("row")).get("key")==null||((Map<String, Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> gfModify= (ArrayList<Map<String, Object>>) subitemcData.stream().filter(f->((Map<String, Object>)f.get("row")).get("key")!=null&&!((Map<String, Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
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


        urlParameters.add(new BasicNameValuePair("subcchgmformData", mStr.toString()));
        urlParameters.add(new BasicNameValuePair("subitemcchgData", subStr.toString()));
        urlParameters.add(new BasicNameValuePair("squitemcchgData", ""));
        urlParameters.add(new BasicNameValuePair("subcchgddData", ""));
        urlParameters.add(new BasicNameValuePair("isContinue", "false"));
        urlParameters.add(new BasicNameValuePair("attchmentGuid", "0"));
        try {
            String i8rv = i8Request.PostFormSync("/PMS/PMS/ZY/SubCChgM/Save", urlParameters);
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
     * @param rate 税额
     * @return
     */
    public Map<String, Object> paramMstformData(SubPackagePlanModel param, String pcPhid, Long phid,String amt,String rate) {
        String phidOrigin=jdbcTemplate.queryForObject("select phid from pms3_subc_m where bill_type='1' and  phid_pc="+pcPhid,String.class);
        String org=jdbcTemplate.queryForObject("select phid_org from project_table where phid="+pcPhid,String.class);
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String jbr=jdbcTemplate.queryForObject("select phid from hr_epm_main where cno='"+param.getUserCode()+"'",String.class);
        String jbrDept=jdbcTemplate.queryForObject("select dept from hr_epm_main where cno='"+param.getUserCode()+"'",String.class);
        Long ngRecordVer=jdbcTemplate.queryForObject("select ng_record_ver from pms3_subc_change_m where bill_type='1' and Chk_Flg=0 and  phid_pc="+pcPhid,Long.class);
        ngRecordVer=ngRecordVer==null?0:ngRecordVer;
        Map<String, Object> row = new HashMap<>();
        row.put("BillNo", param.getCode());
        row.put("BillTitle", param.getName());
        row.put("user_djrq",  DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd"));
        row.put("PhidPc",  pcPhid);
        row.put("user_xmbm",  param.getCode());
        row.put("PhidBudgetType", boqysflCost);
        row.put("PhidOrigin", phidOrigin);
        row.put("OriginTitle", "");
        row.put("ChangeAmt", amt );
        row.put("user_se",  rate);
        row.put("user_qj", "" );
        row.put("user_zz",  org);
        row.put("user_jbr", jbr );
        row.put("user_jbbm",  jbrDept);
        row.put("user_zdr", userId );
        row.put("BillDt ",  DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd"));
        row.put("Remarks", "");
        row.put("user_sl",  0);
        row.put("NgRecordVer", "0" );
        row.put("ChkFlg", "0");
        row.put("PhidOcode", org );
        row.put("AsrFlg", "");
        row.put("PhId", "");
        row.put("WfFlg", "");
        row.put("BillType",  "1");
        row.put("DataFrom", "");
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
    public Map<String, Object> gridDataFB(SubPackagePlanFeeModel itemInfo, Long phid, Long parentPhid, String pcPhid) {
        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        Long ngRecordVer=jdbcTemplate.queryForObject("select t1.ng_record_ver from pms3_subc_change_d t1 left join pms3_subc_change_m t2 on t1.pphid=t2.phid and t2.chk_flg!=1 where t1.phid="+phid+" and t1.user_jjsjkid='"+itemInfo.getId()+"'",Long.class);
        String org=jdbcTemplate.queryForObject("select phid_org from project_table where phid="+pcPhid,String.class);
        ngRecordVer=ngRecordVer==null?0:ngRecordVer;
        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("PhId", "0");
        row.put("Pphid", parentPhid);
        row.put("NgInsertDt", DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss"));
        row.put("NgUpdateDt", DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss"));
        row.put("CurOrgId",  org);
        row.put("Creator",  "");
        row.put("Editor", "");
        row.put("NgRecordVer",  "");
        row.put("PhidItemData",  "0");
        row.put("PhidItemData_EXName", "" );
        row.put("PhidItemDetail",  "0");
        row.put("ResMasterData", "");
        row.put("PhidWork", "0" );
        row.put("PhidBoq",  "0");
        row.put("ChchgName", itemInfo.getName() );
        row.put("ChchgCode",  itemInfo.getCode());
        row.put("ChchgContent",  "");//项目特征
        row.put("PhidMsunit",msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
        row.put("PhidWbs",  "0");
        row.put("PhidWbs_EXName", "" );
        row.put("PhidWbsOrgin",  "0");
        row.put("PhidWbsOrgin_EXName", "" );
        row.put("PhidCbs",  "0");
        row.put("PhidCbs_EXName", "" );
        row.put("PhidCbsOrgin",  "0");
        row.put("PhidCbsOrgin_EXName", "" );
        row.put("OriginQty", itemInfo.getQuantity() );
        row.put("ChangeQty", itemInfo.getQuantity() );
        row.put("AfterChangeQty", itemInfo.getQuantity() );
        row.put("OriginPrice", itemInfo.getTaxRate() );
        row.put("ChangePrice", itemInfo.getQuantity() );
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
        row.put("TaxRate",   0 );
        row.put("TaxAmt", itemInfo.getSjTotal() );
        row.put("VatPrice", itemInfo.getNoTaxRate() );
        row.put("ChangeVatPrice", itemInfo.getNoTaxRate() );
        row.put("AfterVatPrice", itemInfo.getNoTaxRate() );
        row.put("VatAmt", itemInfo.getNoTaxTotal());
        row.put("ChangeVatAmt", itemInfo.getNoTaxTotal());
        row.put("AfterVatAmt", itemInfo.getNoTaxTotal() );
        row.put("QualiReq", "");
        row.put("PlanDtOrgin", "");
        row.put("PlanDt", "");
        row.put("RemarksOrgin", "");
        row.put("Remarks", "");
        row.put("Source", "1");
        row.put("ResPropertys", "");
        row.put("PhidResBs", "0");
        row.put("PhidResBs_EXName", "");
        row.put("BillType", "1");
        row.put("DataFrom", "");
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
        row.put("PhidOrigin", "0");
        row.put("user_mbcbze", "0");
        row.put("user_mbcbye", "0");
        row.put("key", "");
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
