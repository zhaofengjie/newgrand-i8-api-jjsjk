package com.newgrand.secdev.service.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.domain.DataInfo;
import com.newgrand.secdev.domain.EDB.*;
import com.newgrand.secdev.helper.DateTranslate;
import com.newgrand.secdev.helper.I8Request;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BOQ 清单保存
 * @Author ChenXiangLu
 * @Date 2020/11/28 17:28
 * @Version 1.0
 */

@Slf4j
@Service
public class BOQService {
    /**
     * 模拟登录的用户
     */
    @Value("${i8.user}")
    private String i8user;
    /**
     * boq收入清单方案id
     */
    @Value("${i8.edb.boqInScheme}")
    private String boqInScheme;
    /**
     * boq收入清单变更方案id
     */
    @Value("${i8.edb.boqInChangeScheme}")
    private String boqInChangeScheme;
    /**
     * boq成本清单方案id
     */
    @Value("${i8.edb.boqCostScheme}")
    private String boqCostScheme;
    /**
     * boq成本清单变更方案id
     */
    @Value("${i8.edb.boqCostChangeScheme}")
    private String boqCostChangeScheme;
    /**
     * 预算分类_合同收入的phid
     */
    @Value("${i8.edb.boqysflIn}")
    private String boqysflIn;
    /**
     * 预算分类_目标成本的phid
     */
    @Value("${i8.edb.boqysflCost}")
    private String boqysflCost;

    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    private I8Request i8Request;

    /**
     * 同步BOQ
     * @param param
     * @param isCost true表示是成本,需要添加表体明细
     * @return
     */
    public DataInfo saveBoqCost(BOQModel param,boolean isCost)
    {
        DataInfo rvInfo = new DataInfo();

        List<NameValuePair> urlParameters = new ArrayList<>();
        Map<String, ArrayList<Map<String, Object>>> allData=paramBoqCost(param,isCost);
        ArrayList<Map<String, Object>> mstformDatas=allData.get("MstformData");
        ArrayList<Map<String, Object>> boqmtreeDataFBs=allData.get("boqmtreeDataFB");
        ArrayList<Map<String, Object>> boqdgridDataFBs=allData.get("boqdgridDataFB");
        ArrayList<Map<String, Object>> boqmtreeDataDJs=allData.get("boqmtreeDataDJ");
        ArrayList<Map<String, Object>> boqdgridDataDJs=allData.get("boqdgridDataDJ");
        ArrayList<Map<String, Object>> boqmtreeDataQTs=allData.get("boqmtreeDataQT");
        ArrayList<Map<String, Object>> boqmtreeDataGFs=allData.get("boqmtreeDataGF");
        StringBuilder mStr=new StringBuilder();
        StringBuilder fbStr=new StringBuilder();
        StringBuilder fbGridStr=new StringBuilder();
        StringBuilder djStr=new StringBuilder();
        StringBuilder djGridStr=new StringBuilder();
        StringBuilder qtStr=new StringBuilder();
        StringBuilder gfStr=new StringBuilder();

        ///region 表头拼接
        mStr.append("{\"table\":{\"key\":\"PhId\"");
        if(mstformDatas.get(0).get("key")==null||mstformDatas.get(0).get("key").equals("")) {
            mStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(mstformDatas.get(0)));
        }
        else {
            mStr.append(",\"newRow\":" + JSONObject.toJSONString(mstformDatas.get(0)));
        }
        mStr.append("}}");
        ///endregion

        ///region 分部分项工程量清单拼接
        fbStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> fbNew= (ArrayList<Map<String, Object>>) boqmtreeDataFBs.stream().filter(f->f.get("key")==null||f.get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> fbModify= (ArrayList<Map<String, Object>>) boqmtreeDataFBs.stream().filter(f->f.get("key")!=null&&!f.get("key").equals("")).collect(Collectors.toList());
        if(fbNew.size()>0)
        {
            fbStr.append(",\"newRow\":" + JSONObject.toJSONString(fbNew));
        }
        if(fbModify.size()>0)
        {
            fbStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(fbModify));
        }
        fbStr.append("},\"isChanged\":true}");
        ///endregion

        ///region 分部分项工程量清单明细拼接
        fbGridStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> fbGridNew= (ArrayList<Map<String, Object>>) boqdgridDataFBs.stream().filter(f->f.get("key")==null||f.get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> fbGridModify= (ArrayList<Map<String, Object>>) boqdgridDataFBs.stream().filter(f->f.get("key")!=null&&!f.get("key").equals("")).collect(Collectors.toList());
        if(fbGridNew.size()>0)
        {
            fbGridStr.append(",\"newRow\":" + JSONObject.toJSONString(fbGridNew));
        }
        if(fbGridModify.size()>0)
        {
            fbGridStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(fbGridModify));
        }
        fbGridStr.append("},\"isChanged\":true}");
        ///endregion

        ///region 单价措施费拼接
        djStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> djNew= (ArrayList<Map<String, Object>>) boqmtreeDataDJs.stream().filter(f->f.get("key")==null||f.get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> djModify= (ArrayList<Map<String, Object>>) boqmtreeDataDJs.stream().filter(f->f.get("key")!=null&&!f.get("key").equals("")).collect(Collectors.toList());
        if(djNew.size()>0)
        {
            djStr.append(",\"newRow\":" + JSONObject.toJSONString(djNew));
        }
        if(djModify.size()>0)
        {
            djStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(djModify));
        }
        djStr.append("},\"isChanged\":true}");
        ///endregion

        ///region 单价措施费明细拼接
        djGridStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> djGridNew= (ArrayList<Map<String, Object>>) boqdgridDataDJs.stream().filter(f->f.get("key")==null||f.get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> djGridModify= (ArrayList<Map<String, Object>>) boqdgridDataDJs.stream().filter(f->f.get("key")!=null&&!f.get("key").equals("")).collect(Collectors.toList());
        if(djGridNew.size()>0)
        {
            djGridStr.append(",\"newRow\":" + JSONObject.toJSONString(djGridNew));
        }
        if(djGridModify.size()>0)
        {
            djGridStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(djGridModify));
        }
        djGridStr.append("},\"isChanged\":true}");
        ///endregion

        ///region 其他项清单
        qtStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> qtNew= (ArrayList<Map<String, Object>>) boqmtreeDataQTs.stream().filter(f->f.get("key")==null||f.get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> qtModify= (ArrayList<Map<String, Object>>) boqmtreeDataQTs.stream().filter(f->f.get("key")!=null&&!f.get("key").equals("")).collect(Collectors.toList());
        if(qtNew.size()>0)
        {
            qtStr.append(",\"newRow\":" + JSONObject.toJSONString(qtNew));
        }
        if(qtModify.size()>0)
        {
            qtStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(qtModify));
        }
        qtStr.append("},\"isChanged\":true}");
        ///endregion

        ///region 规费与税金
        gfStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> gfNew= (ArrayList<Map<String, Object>>) boqmtreeDataGFs.stream().filter(f->f.get("key")==null||f.get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> gfModify= (ArrayList<Map<String, Object>>) boqmtreeDataGFs.stream().filter(f->f.get("key")!=null&&!f.get("key").equals("")).collect(Collectors.toList());
        if(gfNew.size()>0)
        {
            gfStr.append(",\"newRow\":" + JSONObject.toJSONString(gfNew));
        }
        if(gfModify.size()>0)
        {
            gfStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(gfModify));
        }
        gfStr.append("},\"isChanged\":true}");
        ///endregion




        urlParameters.add(new BasicNameValuePair("mstformData", mStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataFB", fbStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataDJ", djStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataQT", qtStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataGF", gfStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataSP", "{\"table\":{\"key\":\"PhId\"}}"));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataZJ", "{\"table\":{\"key\":\"PhId\"}}"));
        urlParameters.add(new BasicNameValuePair("boqdgridDataFB", fbGridStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqdgridDataDJ", fbGridStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqdgridDataQT", "{\"table\":{\"key\":\"PhId\"}}"));
        urlParameters.add(new BasicNameValuePair("boqmrelwbs", "[]"));
        urlParameters.add(new BasicNameValuePair("isContinue", "false"));
        urlParameters.add(new BasicNameValuePair("attchmentGuid", "0"));
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
     * 拼接整体参数
     * @param param
     * @param isCost true表示是成本,需要添加表体明细
     * @return
     */
    public Map<String, ArrayList<Map<String, Object>>> paramBoqCost(BOQModel param,boolean isCost)
    {
        Map<String, ArrayList< Map<String, Object>>> result=new HashMap<>();

        long minPhid=-1;
        long phid = -1;//用于新增初始化本级暂存id
        long level2Phid = 0;//初始化本级暂存id
        long level2ParentPhid = 0;//初始化末级暂存id
        long level3Phid = 0;//初始化本级暂存id
        long level3ParentPhid = 0;//初始化末级暂存id
        String pcPhid=jdbcTemplate.queryForObject("select phid from project_table where pc_no='"+param.getCode()+"'",String.class);

        if(!param.getApplyType().equals("1")) {
            phid = jdbcTemplate.queryForObject("select phid from pms3_boq_bill where phid_pc=" + pcPhid + " and phid_cblx="+boqysflCost, Long.class);
        }
        else
        {
            phid=minPhid;
        }
        ArrayList<Map<String, Object>> MstformData=new ArrayList<>();
        MstformData.add(paramMstformData(pcPhid,phid));


        ArrayList<Map<String, Object>> boqmtreeDataFBs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqdgridDataFBs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqmtreeDataDJs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqdgridDataDJs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqmtreeDataQTs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqmtreeDataGFs=new ArrayList<>();
        level2ParentPhid=phid;
        for(BOQBQModel v :param.getBqQItemInfos()) {
            minPhid=minPhid-1;
            level2Phid=minPhid;
            long isExist=jdbcTemplate.queryForObject("select phid from pms3_boq_m where phid_pc=" + pcPhid + " and code='"+v.getCode()+"'", Long.class);
            if(isExist>0)
                level2Phid=isExist;
            Map<String, Object> temp=new HashMap<>();
            temp=boqmtreeDataFB(v,level2Phid,level2ParentPhid);
            boqmtreeDataFBs.add(temp);
            level3ParentPhid=level2Phid;
            if(isCost) {
                for (BOQBQFeeModel f : v.getFeeItemInfos()) {
                    level3Phid = minPhid - 1;
                    long isExistFee = jdbcTemplate.queryForObject("select phid from pms3_boq_d where user_jjsjk='" + f.getId() + "'", Long.class);
                    if (isExistFee > 0)
                        level3Phid = isExistFee;
                    Map<String, Object> tempFee = new HashMap<>();
                    tempFee = boqdgridDataFB(f, level3Phid, level3ParentPhid, pcPhid);
                    boqdgridDataFBs.add(tempFee);
                }
            }
        }
        for(BOQMeasureModel v :param.getMeasureItemInfos()) {
            minPhid=minPhid-1;
            level2Phid=minPhid;
            long isExist=jdbcTemplate.queryForObject("select phid from pms3_boq_m where phid_pc=" + pcPhid + " and code='"+v.getCode()+"'", Long.class);
            if(isExist>0)
                level2Phid=isExist;
            Map<String, Object> temp=new HashMap<>();
            temp=boqmtreeDataDJ(v,level2Phid,level2ParentPhid);
            boqmtreeDataDJs.add(temp);
            level3ParentPhid=level2Phid;
            if(isCost) {
                for (BOQMeasureFeeModel f : v.getFeeItemInfos()) {
                    level3Phid = minPhid - 1;
                    long isExistFee = jdbcTemplate.queryForObject("select phid from pms3_boq_d where user_jjsjk='" + f.getId() + "'", Long.class);
                    if (isExistFee > 0)
                        level3Phid = isExistFee;
                    Map<String, Object> tempFee = new HashMap<>();
                    tempFee = boqdgridDataDJ(f, level3Phid, level3ParentPhid, pcPhid);
                    boqdgridDataDJs.add(tempFee);
                }
            }
        }
        for(BOQOtherModel v :param.getOtherItemInfos()) {
            minPhid=minPhid-1;
            level2Phid=minPhid;
            long isExist=jdbcTemplate.queryForObject("select phid from pms3_boq_m where phid_pc=" + pcPhid + " and code='"+v.getCode()+"'", Long.class);
            if(isExist>0)
                level2Phid=isExist;
            Map<String, Object> temp=new HashMap<>();
            temp=boqmtreeDataQT(v,level2Phid,level2ParentPhid);
            boqmtreeDataQTs.add(temp);
        }
        for(BOQFeeModel v :param.getFeeItemInfos()) {
            minPhid=minPhid-1;
            level2Phid=minPhid;
            long isExist=jdbcTemplate.queryForObject("select phid from pms3_boq_m where phid_pc=" + pcPhid + " and code='"+v.getCode()+"'", Long.class);
            if(isExist>0)
                level2Phid=isExist;
            Map<String, Object> temp=new HashMap<>();
            temp=boqmtreeDataGF(v,level2Phid,level2ParentPhid);
            boqmtreeDataGFs.add(temp);
        }

        result.put("MstformData", MstformData);
        result.put("boqmtreeDataFB",boqmtreeDataFBs);
        result.put("boqdgridDataFB",boqdgridDataFBs);
        result.put("boqmtreeDataDJ",boqmtreeDataDJs);
        result.put("boqdgridDataDJ",boqdgridDataDJs);
        result.put("boqmtreeDataQT",boqmtreeDataQTs);
        result.put("boqmtreeDataGF",boqmtreeDataGFs);
        return result;
    }

    /**
     * 封装参数MstformData 是否是新增判断key是否有值
     * @param pcPhid 项目phid
     * @return
     */
    public Map<String, Object> paramMstformData(String pcPhid,long phid) {
        String org=jdbcTemplate.queryForObject("select phid_org from project_table where phid="+pcPhid,String.class);
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);

        Map<String, Object> row = new HashMap<>();
        row.put("PhidPc", pcPhid);
        row.put("PhidCblx", boqysflCost);
        row.put("ProjectCost", 0);
        row.put("PlannedCost", 0);
        row.put("HisVersion", 0);
        row.put("ChkFlg", "");
        row.put("Version", "0");
        row.put("IsWbs", "2");
        row.put("IsLock", "");
        row.put("AppStatus", "");
        row.put("BillNo", "0");
        row.put("Sortrow", "0");
        row.put("BillDt", "");
        row.put("Title", "合同收入");
        row.put("AsrFlg", "");
        row.put("WfFlg", "");
        row.put("DaFlg", "");
        row.put("ChkDt", "");
        row.put("PhidOcode", org);
        row.put("LevelCode", "0");
        row.put("PrintCount", 0);
        row.put("BillType", "");
        row.put("UserType", "");
        row.put("Remarks", "");
        row.put("PhId", "");
        row.put("NgInsertDt", DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss"));
        row.put("NgUpdateDt", DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss"));
        row.put("Creator", userId);
        row.put("Editor", userId);
        row.put("CurOrgId", org);
        row.put("PhidChkpsn", 0);
        row.put("PhidSchemeId", boqInScheme);
        row.put("PhidTask", "");
        row.put("key", "");
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        return row;
    }

    /**
     * 封装参数boqmtreeDataFB 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqmtreeDataFB(BOQBQModel itemInfo, long phid,long parentPhid) {

        String wbsCode=itemInfo.getPId()==null||itemInfo.getPId().equals("")?itemInfo.getBidNodeID(): itemInfo.getPId();
        String wbsPhid=jdbcTemplate.queryForObject("select phid from bd_wbs  where wbs_realcode='"+wbsCode+"'",String.class);
        String wbsName=jdbcTemplate.queryForObject("select description from bd_wbs  where wbs_realcode='"+wbsCode+"'",String.class);
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        String cntPhid=jdbcTemplate.queryForObject("select phid from pcm3_cnt_m where bill_no='"+itemInfo.getContractCode()+"'",String.class);

        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("user_cbht", cntPhid);
        row.put("PhId", phid);
        row.put("Pphid", parentPhid);
        row.put("NgInsertDt", null);
        row.put("NgUpdateDt", null);
        row.put("Creator", "");
        row.put("Creator_EXName", "");
        row.put("Editor", "");
        row.put("Editor_EXName", "");
        row.put("PhidPc", "");
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        row.put("Descript", itemInfo.getSpec());
        row.put("PhidWbs", wbsPhid);
        row.put("PhidWbs_EXName", wbsName);
        row.put("PhidMsunit", msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
        row.put("PhidLevel", "");
        row.put("PhidLevel_EXName", "");
        row.put("MType", 1);
        row.put("IsCnt", 1);
        row.put("Expression", "");
        row.put("PhidCbs", "");
        row.put("PhidCbs_EXName", "");
        row.put("Rate", 0);
        row.put("Remarks", "");
        row.put("Qty", itemInfo.getQuantity());
        row.put("Prc", itemInfo.getRate());
        row.put("Amt", itemInfo.getTotal());
        row.put("AppStatus", "");
        row.put("Cblx", "");
        row.put("IsSummed", 0);
        row.put("Sortrow", 0);
        row.put("BoqidUniq", "");
        row.put("Version", "");
        row.put("Ftype", "");
        row.put("Parentphid", 0);
        row.put("IsRel", 0);
        row.put("PhidUnitPc", 0);
        row.put("PhidIndiPc", 0);
        row.put("Approval", 0);
        row.put("CbsQdId", "");
        row.put("Bidding", "");
        row.put("WriteSelf", "");
        row.put("Reply", "");
        row.put("ContractIncome", "");
        row.put("PlanCost", "");
        row.put("PhidVisa", "");
        row.put("PhidVisa_EXName", "");
        row.put("Color", "");
        row.put("Import", "");
        row.put("PcoRestQty", 0);
        row.put("PcoRestAmt", 0);
        row.put("PcoControlQty", 0);
        row.put("PcoControlAmt", 0);
        row.put("RestQty", 0);
        row.put("RestAmt", 0);
        row.put("ControlQty", 0);
        row.put("ControlAmt", 0);
        row.put("CntRestQty", 0);
        row.put("CntRestAmt", 0);
        row.put("CntControlQty", 0);
        row.put("CntControlAmt", 0);
        row.put("CntPayRestQty", 0);
        row.put("CntPayRestAmt", 0);
        row.put("CntPayControlQty", 0);
        row.put("CntPayControlAmt", 0);
        row.put("CzPlanRestQty", 0);
        row.put("CzPlanRestAmt", 0);
        row.put("IsChange", 0);
        row.put("SourceType", 0);
        row.put("PhidSource", 0);
        row.put("ImpInfo", 0);
        row.put("Nullify", 0);
        row.put("OriQty", 0);
        row.put("CurrQty", 0);
        row.put("CurrChangeQty", 0);
        row.put("OriPrc", 0);
        row.put("CurrPrc", 0);
        row.put("CurrChangePrc", 0);
        row.put("OriAmt", 0);
        row.put("CurrAmt", 0);
        row.put("CurrChangeAmt", 0);
        row.put("Pms3BoqMPhid", "");
        row.put("ChangeM", 0);
        row.put("ChangeM_EXName", "");
        row.put("ChgData", "");
        row.put("parentId", "root");
        row.put("checked", null);
        row.put("loading", false);
        row.put("key", null);
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装参数boqdgridDataFB 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqdgridDataFB(BOQBQFeeModel itemInfo, long phid,long parentPhid,String pcPhid) {

        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        String cbsPhid=jdbcTemplate.queryForObject("select cbs_code from bd_cbs where bd_cbs='"+itemInfo.getCourseCode()+"' and pcid="+pcPhid,String.class);
        String cbsName=jdbcTemplate.queryForObject("select cbs_name from bd_cbs where bd_cbs='"+itemInfo.getCourseCode()+"' and pcid="+pcPhid,String.class);
        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("PhId", "");
        row.put("Pphid", parentPhid);
        row.put("Ppphid", "");
        row.put("SType", "");
        row.put("IsCost", "1");
        row.put("PhidCbs", cbsPhid);
        row.put("PhidCbs_EXName", cbsName);
        row.put("PhidItemid", "");
        row.put("PhidItemid_EXName", "");
        row.put("PhidRestype", "");
        row.put("PhidResbs", "");
        row.put("PhidResbs_EXName", "");
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        row.put("RcjType", itemInfo.getFeeType());
        row.put("Spec", itemInfo.getSpec());
        row.put("PhidMsunit",msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
        row.put("ResAlias", "");
        row.put("Note", "");
        row.put("Remarks", "");
        row.put("Qty", 0);
        row.put("Prc", 0);
        row.put("Amt", 0);
        row.put("Totqty", 0);
        row.put("Totamt", 0);
        row.put("Ftype", "");
        row.put("MType", 2);
        row.put("Cblx", "");
        row.put("PhidQuotaD", "");
        row.put("PhidQuota", "");
        row.put("PhidQuota_EXName", "");
        row.put("PhidItemdetail", "");
        row.put("ResPropertys", "");
        row.put("PcoRestQty", 0);
        row.put("PcoRestAmt", 0);
        row.put("RestQty", 0);
        row.put("RestAmt", 0);
        row.put("CntRestQty", 0 );
        row.put("CntRestAmt", 0);
        row.put("CntPayRestQty", 0);
        row.put("CntPayRestAmt", 0);
        row.put("IsFarmProduce", 0);
        row.put("PcoControlQty", 0);
        row.put("PcoControlAmt", 0);
        row.put("ControlQty", 0);
        row.put("ControlAmt", 0);
        row.put("CntControlQty", "");
        row.put("CntControlAmt", 0);
        row.put("CntPayControlQty", 0);
        row.put("CntPayControlAmt", 0);
        row.put("CostRefFlg", 0);
        row.put("CzPlanRestQty", 0);
        row.put("CzPlanRestAmt", 0);
        row.put("LossRate", 0);
        row.put("PhidCbsNew", "");
        row.put("CurrQty", 0);
        row.put("CurrChangeQty", 0);
        row.put("CurrPrc", 0);
        row.put("CurrChangePrc", 0);
        row.put("CurrAmt", 0);
        row.put("CurrChangeAmt", 0);
        row.put("CurrTotqty", 0);
        row.put("CurrChangeTotqty", 0);
        row.put("CurrTotamt", 0);
        row.put("CurrChangeTotamt", 0);
        row.put("Pms3BoqMPhid", "");
        row.put("ChangeD", 0);
        row.put("PhidCbsNew_EXName", "");
        row.put("PhidPc", "");
        row.put("PhidLevel", "");
        row.put("PhidLevel_EXName", "");
        row.put("MCname", "");
        row.put("MCode", "");
        row.put("MPhid", "");
        row.put("PhidQuota_EXCode", "");
        row.put("PhidWbs", "");
        row.put("PhidWbs_EXName", "");
        row.put("CbsQdId", "");
        row.put("BoqDIsRel", "");
        row.put("key", null);
        row.put("user_jjsjk", itemInfo.getId());
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装参数boqmtreeDataDJ 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqmtreeDataDJ(BOQMeasureModel itemInfo, long phid,long parentPhid) {

        String wbsCode=itemInfo.getPId()==null||itemInfo.getPId().equals("")?itemInfo.getBidNodeID(): itemInfo.getPId();
        String wbsPhid=jdbcTemplate.queryForObject("select phid from bd_wbs  where wbs_realcode='"+wbsCode+"'",String.class);
        String wbsName=jdbcTemplate.queryForObject("select description from bd_wbs  where wbs_realcode='"+wbsCode+"'",String.class);
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        String cntPhid=jdbcTemplate.queryForObject("select phid from pcm3_cnt_m where bill_no='"+itemInfo.getContractCode()+"'",String.class);

        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("user_cbht", cntPhid);
        row.put("PhId", phid);
        row.put("Pphid", parentPhid);
        row.put("NgInsertDt", null);
        row.put("NgUpdateDt", null);
        row.put("Creator", "");
        row.put("Creator_EXName", "");
        row.put("Editor", "");
        row.put("Editor_EXName", "");
        row.put("PhidPc", "");
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        row.put("Descript", itemInfo.getSpec());
        row.put("PhidWbs", wbsPhid);
        row.put("PhidWbs_EXName", wbsName);
        row.put("PhidMsunit", msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
        row.put("PhidLevel", "");
        row.put("PhidLevel_EXName", "");
        row.put("MType", 1);
        row.put("IsCnt", 1);
        row.put("Expression", "");
        row.put("PhidCbs", "");
        row.put("PhidCbs_EXName", "");
        row.put("Rate", 0);
        row.put("Remarks", "");
        row.put("Qty", itemInfo.getQuantity());
        row.put("Prc", itemInfo.getRate());
        row.put("Amt", itemInfo.getTotal());
        row.put("AppStatus", "");
        row.put("Cblx", "");
        row.put("IsSummed", 0);
        row.put("Sortrow", 0);
        row.put("BoqidUniq", "");
        row.put("Version", "");
        row.put("Ftype", "");
        row.put("Parentphid", 0);
        row.put("IsRel", 0);
        row.put("PhidUnitPc", 0);
        row.put("PhidIndiPc", 0);
        row.put("Approval", 0);
        row.put("CbsQdId", "");
        row.put("Bidding", "");
        row.put("WriteSelf", "");
        row.put("Reply", "");
        row.put("ContractIncome", "");
        row.put("PlanCost", "");
        row.put("PhidVisa", "");
        row.put("PhidVisa_EXName", "");
        row.put("Color", "");
        row.put("Import", "");
        row.put("PcoRestQty", 0);
        row.put("PcoRestAmt", 0);
        row.put("PcoControlQty", 0);
        row.put("PcoControlAmt", 0);
        row.put("RestQty", 0);
        row.put("RestAmt", 0);
        row.put("ControlQty", 0);
        row.put("ControlAmt", 0);
        row.put("CntRestQty", 0);
        row.put("CntRestAmt", 0);
        row.put("CntControlQty", 0);
        row.put("CntControlAmt", 0);
        row.put("CntPayRestQty", 0);
        row.put("CntPayRestAmt", 0);
        row.put("CntPayControlQty", 0);
        row.put("CntPayControlAmt", 0);
        row.put("CzPlanRestQty", 0);
        row.put("CzPlanRestAmt", 0);
        row.put("IsChange", 0);
        row.put("SourceType", 0);
        row.put("PhidSource", 0);
        row.put("ImpInfo", 0);
        row.put("Nullify", 0);
        row.put("OriQty", 0);
        row.put("CurrQty", 0);
        row.put("CurrChangeQty", 0);
        row.put("OriPrc", 0);
        row.put("CurrPrc", 0);
        row.put("CurrChangePrc", 0);
        row.put("OriAmt", 0);
        row.put("CurrAmt", 0);
        row.put("CurrChangeAmt", 0);
        row.put("Pms3BoqMPhid", "");
        row.put("ChangeM", 0);
        row.put("ChangeM_EXName", "");
        row.put("ChgData", "");
        row.put("parentId", "root");
        row.put("checked", null);
        row.put("loading", false);
        row.put("key", null);
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装参数boqdgridDataDJ 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqdgridDataDJ(BOQMeasureFeeModel itemInfo, long phid,long parentPhid,String pcPhid) {

        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        String cbsPhid=jdbcTemplate.queryForObject("select cbs_code from bd_cbs where bd_cbs='"+itemInfo.getCourseCode()+"' and pcid="+pcPhid,String.class);
        String cbsName=jdbcTemplate.queryForObject("select cbs_name from bd_cbs where bd_cbs='"+itemInfo.getCourseCode()+"' and pcid="+pcPhid,String.class);
        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("PhId", "");
        row.put("Pphid", parentPhid);
        row.put("Ppphid", "");
        row.put("SType", "");
        row.put("IsCost", "1");
        row.put("PhidCbs", cbsPhid);
        row.put("PhidCbs_EXName", cbsName);
        row.put("PhidItemid", "");
        row.put("PhidItemid_EXName", "");
        row.put("PhidRestype", "");
        row.put("PhidResbs", "");
        row.put("PhidResbs_EXName", "");
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        row.put("RcjType", itemInfo.getFeeType());
        row.put("Spec", itemInfo.getSpec());
        row.put("PhidMsunit",msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
        row.put("ResAlias", "");
        row.put("Note", "");
        row.put("Remarks", "");
        row.put("Qty", 0);
        row.put("Prc", 0);
        row.put("Amt", 0);
        row.put("Totqty", 0);
        row.put("Totamt", 0);
        row.put("Ftype", "");
        row.put("MType", 1);
        row.put("Cblx", "");
        row.put("PhidQuotaD", "");
        row.put("PhidQuota", "");
        row.put("PhidQuota_EXName", "");
        row.put("PhidItemdetail", "");
        row.put("ResPropertys", "");
        row.put("PcoRestQty", 0);
        row.put("PcoRestAmt", 0);
        row.put("RestQty", 0);
        row.put("RestAmt", 0);
        row.put("CntRestQty", 0 );
        row.put("CntRestAmt", 0);
        row.put("CntPayRestQty", 0);
        row.put("CntPayRestAmt", 0);
        row.put("IsFarmProduce", 0);
        row.put("PcoControlQty", 0);
        row.put("PcoControlAmt", 0);
        row.put("ControlQty", 0);
        row.put("ControlAmt", 0);
        row.put("CntControlQty", "");
        row.put("CntControlAmt", 0);
        row.put("CntPayControlQty", 0);
        row.put("CntPayControlAmt", 0);
        row.put("CostRefFlg", 0);
        row.put("CzPlanRestQty", 0);
        row.put("CzPlanRestAmt", 0);
        row.put("LossRate", 0);
        row.put("PhidCbsNew", "");
        row.put("CurrQty", 0);
        row.put("CurrChangeQty", 0);
        row.put("CurrPrc", 0);
        row.put("CurrChangePrc", 0);
        row.put("CurrAmt", 0);
        row.put("CurrChangeAmt", 0);
        row.put("CurrTotqty", 0);
        row.put("CurrChangeTotqty", 0);
        row.put("CurrTotamt", 0);
        row.put("CurrChangeTotamt", 0);
        row.put("Pms3BoqMPhid", "");
        row.put("ChangeD", 0);
        row.put("PhidCbsNew_EXName", "");
        row.put("PhidPc", "");
        row.put("PhidLevel", "");
        row.put("PhidLevel_EXName", "");
        row.put("MCname", "");
        row.put("MCode", "");
        row.put("MPhid", "");
        row.put("PhidQuota_EXCode", "");
        row.put("PhidWbs", "");
        row.put("PhidWbs_EXName", "");
        row.put("CbsQdId", "");
        row.put("BoqDIsRel", "");
        row.put("key", null);
        row.put("user_jjsjk", itemInfo.getId());
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装参数boqmtreeDataDJ 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqmtreeDataQT(BOQOtherModel itemInfo, long phid,long parentPhid) {

        String wbsCode=itemInfo.getBidNodeID();
        String wbsPhid=jdbcTemplate.queryForObject("select phid from bd_wbs  where wbs_realcode='"+wbsCode+"'",String.class);
        String wbsName=jdbcTemplate.queryForObject("select description from bd_wbs  where wbs_realcode='"+wbsCode+"'",String.class);
        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        String cntPhid=jdbcTemplate.queryForObject("select phid from pcm3_cnt_m where bill_no='"+itemInfo.getContractCode()+"'",String.class);

        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("user_cbht", cntPhid);
        row.put("PhId", phid);
        row.put("Pphid", parentPhid);
        row.put("NgInsertDt", null);
        row.put("NgUpdateDt", null);
        row.put("Creator", "");
        row.put("Creator_EXName", "");
        row.put("Editor", "");
        row.put("Editor_EXName", "");
        row.put("PhidPc", "");
        row.put("PhidPc_EXName", "");
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        row.put("Descript", "");
        row.put("PhidWbs", wbsPhid);
        row.put("PhidWbs_EXName", wbsName);
        row.put("PhidMsunit", msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
        row.put("PhidLevel", "");
        row.put("PhidLevel_EXName", "");
        row.put("MType", 3);
        row.put("IsCnt", 1);
        row.put("Expression", "");
        row.put("PhidCbs", "");
        row.put("PhidCbs_EXName", "");
        row.put("Rate", 0);
        row.put("Remarks", "");
        row.put("Qty", 0);
        row.put("Prc", 0);
        row.put("Amt", itemInfo.getTotal());
        row.put("AppStatus", "");
        row.put("Cblx", "");
        row.put("IsSummed", 0);
        row.put("Sortrow", 0);
        row.put("BoqidUniq", "");
        row.put("Version", "");
        row.put("Ftype", "");
        row.put("Parentphid", 0);
        row.put("IsRel", 0);
        row.put("PhidUnitPc", 0);
        row.put("PhidIndiPc", 0);
        row.put("Approval", 0);
        row.put("CbsQdId", "");
        row.put("Bidding", "");
        row.put("WriteSelf", "");
        row.put("Reply", "");
        row.put("ContractIncome", "");
        row.put("PlanCost", "");
        row.put("PhidVisa", "");
        row.put("PhidVisa_EXName", "");
        row.put("Color", "");
        row.put("Import", "");
        row.put("PcoRestQty", 0);
        row.put("PcoRestAmt", 0);
        row.put("PcoControlQty", 0);
        row.put("PcoControlAmt", 0);
        row.put("RestQty", 0);
        row.put("RestAmt", 0);
        row.put("ControlQty", 0);
        row.put("ControlAmt", 0);
        row.put("CntRestQty", 0);
        row.put("CntRestAmt", 0);
        row.put("CntControlQty", 0);
        row.put("CntControlAmt", 0);
        row.put("CntPayRestQty", 0);
        row.put("CntPayRestAmt", 0);
        row.put("CntPayControlQty", 0);
        row.put("CntPayControlAmt", 0);
        row.put("CzPlanRestQty", 0);
        row.put("CzPlanRestAmt", 0);
        row.put("IsChange", 0);
        row.put("SourceType", 0);
        row.put("PhidSource", 0);
        row.put("ImpInfo", 0);
        row.put("Nullify", 0);
        row.put("OriQty", 0);
        row.put("CurrQty", 0);
        row.put("CurrChangeQty", 0);
        row.put("OriPrc", 0);
        row.put("CurrPrc", 0);
        row.put("CurrChangePrc", 0);
        row.put("OriAmt", 0);
        row.put("CurrAmt", 0);
        row.put("CurrChangeAmt", 0);
        row.put("Pms3BoqMPhid", "");
        row.put("ChangeM", 0);
        row.put("ChangeM_EXName", "");
        row.put("ChgData", "");
        row.put("parentId", "root");
        row.put("checked", null);
        row.put("loading", false);
        row.put("key", null);
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装参数boqmtreeDataDJ 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqmtreeDataGF(BOQFeeModel itemInfo, long phid,long parentPhid) {

        String userId=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);

        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("user_cbht", "");
        row.put("PhId", phid);
        row.put("Pphid", parentPhid);
        row.put("NgInsertDt", null);
        row.put("NgUpdateDt", null);
        row.put("Creator", "");
        row.put("Creator_EXName", "");
        row.put("Editor", "");
        row.put("Editor_EXName", "");
        row.put("PhidPc", "");
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        row.put("Descript", "");
        row.put("PhidWbs", "");
        row.put("PhidWbs_EXName", "");
        row.put("PhidMsunit", msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
        row.put("PhidLevel", "");
        row.put("PhidLevel_EXName", "");
        row.put("MType", 4);
        row.put("IsCnt", 1);
        row.put("Expression", "1");
        row.put("PhidCbs", "");
        row.put("PhidCbs_EXName", "");
        row.put("Rate", 0);
        row.put("Remarks", "");
        row.put("Qty", 0);
        row.put("Prc", 0);
        row.put("Amt", 0);
        row.put("AppStatus", "");
        row.put("Cblx", "");
        row.put("IsSummed", 0);
        row.put("Sortrow", 0);
        row.put("BoqidUniq", "");
        row.put("Version", "");
        row.put("Ftype", "");
        row.put("Parentphid", 0);
        row.put("IsRel", 0);
        row.put("PhidUnitPc", 0);
        row.put("PhidIndiPc", 0);
        row.put("Approval", 0);
        row.put("Bidding", "");
        row.put("WriteSelf", "");
        row.put("Reply", "");
        row.put("ContractIncome", "");
        row.put("PlanCost", "");
        row.put("PhidVisa", "");
        row.put("PhidVisa_EXName", "");
        row.put("Import", "");
        row.put("PcoRestQty", 0);
        row.put("PcoRestAmt", 0);
        row.put("PcoControlQty", 0);
        row.put("PcoControlAmt", 0);
        row.put("RestQty", 0);
        row.put("RestAmt", 0);
        row.put("ControlQty", 0);
        row.put("ControlAmt", 0);
        row.put("CntRestQty", 0);
        row.put("CntRestAmt", 0);
        row.put("CntControlQty", 0);
        row.put("CntControlAmt", 0);
        row.put("CntPayRestQty", 0);
        row.put("CntPayRestAmt", 0);
        row.put("CntPayControlQty", 0);
        row.put("CntPayControlAmt", 0);
        row.put("CzPlanRestQty", 0);
        row.put("CzPlanRestAmt", 0);
        row.put("IsChange", 0);
        row.put("SourceType", 0);
        row.put("PhidSource", 0);
        row.put("Nullify", 0);
        row.put("parentId", "root");
        row.put("checked", null);
        row.put("loading", false);
        row.put("key", null);
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        newRow.put("row", row);
        return newRow;
    }

}
