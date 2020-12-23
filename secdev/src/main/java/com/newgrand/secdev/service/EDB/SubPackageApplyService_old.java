package com.newgrand.secdev.service.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.config.IJdbcTemplate;
import com.newgrand.secdev.domain.DataInfo;
import com.newgrand.secdev.domain.EDB.SubPackageApplyDetailFeeModel;
import com.newgrand.secdev.domain.EDB.SubPackageApplyDetailModel;
import com.newgrand.secdev.helper.DateTranslate;
import com.newgrand.secdev.helper.I8Request;
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
 * 分包申请
 * @Author ChenXiangLu
 * @Date 2020/12/11 10:45
 * @Version 1.0
 */
@Service
public class SubPackageApplyService_old {
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
    @Value("${i8.edb.subPackageApplyDetailScheme}")
    private String subPackageApplyDetailScheme;


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
    public DataInfo saveSubPackageApplyDetail(SubPackageApplyDetailModel param,String pcPhid,String userCode)
    {
        DataInfo rvInfo = new DataInfo();
        String amt="0";//无税金额
        String sum="0";//含税金额
        String rate="0";//税额
        ArrayList<SubPackageApplyDetailFeeModel> feeItemInfos=param.getFeeItemInfos();
        if(feeItemInfos!=null&&feeItemInfos.size()>0)
        {
            amt= String.valueOf(feeItemInfos.stream().mapToDouble(m->Double.parseDouble(m.getNoTaxTotal())).sum());
            sum= String.valueOf(feeItemInfos.stream().mapToDouble(m->Double.parseDouble(m.getTaxTotal())).sum());
            rate= String.valueOf(feeItemInfos.stream().mapToDouble(m->Double.parseDouble(m.getSjTotal())).sum());
        }
        Long phid=jdbcTemplate.queryForObject("select phid from pms3_subc_m where bill_type='1' and  phid_pc="+pcPhid,Long.class);
        phid=phid==null?0:phid;
        List<NameValuePair> urlParameters = new ArrayList<>();
        Map<String, Object> mstformDatas=paramMstformData(param,pcPhid,phid,amt,sum,rate,userCode);

        ArrayList<Map<String, Object>> subitemcData=new ArrayList<>();
        for(SubPackageApplyDetailFeeModel v:param.getFeeItemInfos())
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
    public Map<String, Object> paramMstformData(SubPackageApplyDetailModel param, String pcPhid, long phid,String amt,String sum,String rate,String userCode) {
        String org=jdbcTemplate.queryForObject("select phid_org from project_table where phid="+pcPhid,String.class);
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String jbr=jdbcTemplate.queryForObject("select phid from hr_epm_main where cno='"+userCode+"'",String.class);
        String jbrDept=jdbcTemplate.queryForObject("select dept from hr_epm_main where cno='"+userCode+"'",String.class);
        String pcName=jdbcTemplate.queryForObject("select project_name from project_table where phid="+pcPhid,String.class);
        Long ngRecordVer=jdbcTemplate.queryForObject("select ng_record_ver from pms3_subc_m where bill_type='1' and  phid_pc="+pcPhid,Long.class);
        ngRecordVer=ngRecordVer==null?0:ngRecordVer;
        String billDt=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd");
        Map<String, Object> row = new HashMap<>();
//        row.put("BillNo", param.getId());
//        row.put("BillTitle", param.getName());
//        row.put("user_djrq",  billDt);
//        row.put("PhidPc",  pcPhid);
//        row.put("user_xmbm", pcName);
//        row.put("user_zbdw",  );
//        row.put("user_zyfl",  );
//        row.put("user_jhgq",  );
//        row.put("user_zbfzr",  );
//        row.put("user_jsdw",  );
//        row.put("user_pbbf",  );
//        row.put("JAmtSum",  );
//        row.put("JAmtVatSum",  );
//        row.put("user_se",  );
//        row.put("user_qj",  );
//        row.put("user_zbkzjws",  );
//        row.put("user_zbkzjhs",  );
//        row.put("user_zbfs",  );
//        row.put("user_jhzbrq",  );
//        row.put("user_bjfs",  );
//        row.put("PhidBudgetType",  );
//        row.put("user_jbr",  );
//        row.put("user_jbbm",  );
//        row.put("user_zdr",  );
//        row.put("BillDt",  );
//        row.put("user_fblyjsm",  );
//        row.put("user_jstk",  );
//        row.put("user_fktk",  );
//        row.put("Remarks",  );
//        row.put("PhidDept",  );
//        row.put("PhidCmid",  );
//        row.put("PhId",  );
//        row.put("PhidCmidTitle",  );
//        row.put("NgRecordVer",  );
//        row.put("PhidOcode",  );
//        row.put("Creator",  );
//        row.put("ImpInfo",  );
//        row.put("AsrFlg",  );
//        row.put("WfFlg",  );
//        row.put("ChkFlg",  );
//        row.put("PhidChkpsn",  );
//        row.put("BillType",  );
//        row.put("DaFlg",  );
//        row.put("user_zz",  );
//        row.put("PhidTask",  );
        row.put("key", "" );

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
    public Map<String, Object> gridDataFB(SubPackageApplyDetailFeeModel itemInfo, long phid, long parentPhid, String pcPhid) {
        String timeValue=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        Long ngRecordVer=jdbcTemplate.queryForObject("select ng_record_ver from pms3_subc_d where user_jjsjkid='"+itemInfo.getId()+"'",Long.class);
        String org=jdbcTemplate.queryForObject("select phid_org from project_table where phid="+pcPhid,String.class);
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        ngRecordVer=ngRecordVer==null?0:ngRecordVer;
        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("PhId","0");
        row.put("Pphid",parentPhid);
//        row.put("Qdml",);
//        row.put("PhidChd",);
//        row.put("PhidWork",0);
//        row.put("NgInsertDt", null);
//        row.put("NgUpdateDt", null);
//        row.put("CurOrgId",);
//        row.put("Creator",);
//        row.put("Editor",);
//        row.put("NgRecordVer", );
//        row.put("Character",);
//        row.put("Cname",);
//        row.put("Code",);
//        row.put("PhidWbs",);
//        row.put("PhidWbs_EXName",);
//        row.put("PhidCbs",);
//        row.put("PhidCbs_EXName",);
//        row.put("PhidUnit",);
//        row.put("PhidUnit_EXName",);
//        row.put("OriginQty", );
//        row.put("QtyNew", );
//        row.put("OriginPrice", );
//        row.put("PriceNew", );
//        row.put("OriginAmt", );
//        row.put("AmtNew", );
//        row.put("LaveQty", );
//        row.put("AveAmt", );
//        row.put("TaxRate", );
//        row.put("TaxAmt", );
//        row.put("VatPrice", );
//        row.put("VatAmtNew", );
//        row.put("VatPriceNew", );
//        row.put("VatAmt", );
//        row.put("QualiReq",);
//        row.put("PlanDt", null);
//        row.put("Remarks",);
//        row.put("Source",);
//        row.put("BillType",);
//        row.put("ImpInfo",);
//        row.put("RestQty", );
//        row.put("RestAmtVat", );
//        row.put("RestAmtVatFc", );
//        row.put("RestAmt", );
//        row.put("RestAmtFc", );
//        row.put("ControlQty", );
//        row.put("ControlAmtVat", );
//        row.put("ControlAmtVatFc", );
//        row.put("ControlAmt", );
//        row.put("ControlAmtFc", );
//        row.put("PhidSchemeid",0);
//        row.put("PhidSourcemid",0);
//        row.put("PhidSourceid",);
//        row.put("ItemResource",);
//        row.put("CostName",);
//        row.put("user_qzrgfdjws", );
//        row.put("user_qzrgfdjhs", );
//        row.put("user_qzrgfjews", );
//        row.put("user_qzrgfjehs", );
//        row.put("user_jlgz",);
//        row.put("user_gznr",);
//        row.put("user_clgyjpp",);
//        row.put("user_mbcbze", );
//        row.put("user_mbcbye", );
        row.put("key", null);
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
