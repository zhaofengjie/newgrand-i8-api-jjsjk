package com.newgrand.secdev.service.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.config.IJdbcTemplate;
import com.newgrand.secdev.domain.DataInfo;
import com.newgrand.secdev.domain.EDB.*;
import com.newgrand.secdev.helper.DateTranslate;
import com.newgrand.secdev.helper.EntityConverter;
import com.newgrand.secdev.helper.I8Request;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BOQ 清单变更保存
 * @Author ChenXiangLu
 * @Date 2020/11/28 17:28
 * @Version 1.0
 */

@Slf4j
@Service
public class BOQChangeService {
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
    protected IJdbcTemplate jdbcTemplate;

    @Autowired
    private I8Request i8Request;

    private String billStr="{\"PhidPc\":\"\",\"PhidCblx\":\"\",\"ChangeType\":1,\"BefortAmt\":0,\"CurrentAmt\":0,\"AfterAmt\":0,\"BillCode\":\"\",\"Description\":\"\",\"BillDt\":\"2020-12-21 11:05:48\",\"Creator_EXName\":\"\",\"ChangeNo\":\"\",\"Remarks\":\"\",\"Version\":\"0\",\"IsWbs\":\"2\",\"ChkFlg\":\"\",\"IsLock\":\"\",\"AppStatus\":\"\",\"BillNo\":\"\",\"Title\":\"11\",\"AsrFlg\":\"\",\"WfFlg\":\"\",\"DaFlg\":\"\",\"ChkDt\":\"\",\"PhidOcode\":\"\",\"LevelCode\":\"0\",\"PrintCount\":0,\"BillType\":\"\",\"UserType\":\"\",\"PhId\":\"\",\"NgInsertDt\":\"2020-12-21 11:05:48\",\"NgUpdateDt\":\"2020-12-21 11:06:47\",\"Creator\":\"\",\"Editor\":\"\",\"CurOrgId\":\"\",\"PhidChkpsn\":\"0\",\"PhidSchemeId\":\"0\",\"ChangeBill\":\"1\",\"Pms3BoqBillPhid\":\"\",\"PhidTask\":\"\",\"key\":\"\"}";
    private String mStr="{\"id\":\"\",\"PhId\":\"\",\"Pphid\":\"\",\"NgInsertDt\":\"2020-12-21 11:05:48\",\"NgUpdateDt\":\"2020-12-21 11:05:48\",\"Creator\":\"\",\"Creator_EXName\":\"\",\"Editor\":\"\",\"Editor_EXName\":\"\",\"PhidPc\":\"\",\"PhidPc_EXName\":\"\",\"Code\":\"\",\"Cname\":\"\",\"Descript\":\"\",\"PhidWbs\":\"\",\"PhidWbs_EXName\":\"\",\"PhidMsunit\":\"\",\"PhidMsunit_EXName\":\"\",\"PhidLevel\":\"0\",\"PhidLevel_EXName\":\"\",\"MType\":1,\"IsCnt\":0,\"Expression\":\"\",\"PhidCbs\":\"0\",\"PhidCbs_EXName\":\"\",\"Rate\":0,\"Remarks\":\"\",\"Qty\":0,\"Prc\":0,\"Amt\":0,\"AppStatus\":\"\",\"Cblx\":\"\",\"IsSummed\":\"0\",\"Sortrow\":1,\"BoqidUniq\":\"\",\"Version\":\"\",\"Ftype\":\"*\",\"Parentphid\":\"0\",\"IsRel\":0,\"PhidUnitPc\":\"0\",\"PhidIndiPc\":\"0\",\"Approval\":\"0\",\"CbsQdId\":\"\",\"Bidding\":\"\",\"WriteSelf\":\"\",\"Reply\":\"\",\"ContractIncome\":\"0\",\"PlanCost\":\"0\",\"PhidVisa\":\"0\",\"PhidVisa_EXName\":\"\",\"Color\":\"0\",\"Import\":\"0\",\"PcoRestQty\":0,\"PcoRestAmt\":0,\"PcoControlQty\":0,\"PcoControlAmt\":0,\"RestQty\":0,\"RestAmt\":0,\"ControlQty\":0,\"ControlAmt\":0,\"CntRestQty\":0,\"CntRestAmt\":0,\"CntControlQty\":0,\"CntControlAmt\":0,\"CntPayRestQty\":0,\"CntPayRestAmt\":0,\"CntPayControlQty\":0,\"CntPayControlAmt\":0,\"IsColor\":\"\",\"CzPlanRestQty\":0,\"CzPlanRestAmt\":0,\"SourceType\":0,\"PhidSource\":\"0\",\"ImpInfo\":\"\",\"OriQty\":0,\"CurrQty\":0,\"CurrChangeQty\":0,\"OriPrc\":0,\"CurrPrc\":0,\"CurrChangePrc\":0,\"OriAmt\":0,\"CurrAmt\":0,\"CurrChangeAmt\":0,\"Pms3BoqMPhid\":\"0\",\"ChangeM\":1,\"ChangeM_EXName\":\"\",\"ChgData\":\"\",\"BoqmType\":2,\"OutExpress\":\"\",\"OutExpressRate\":\"\",\"ChangeTypeName\":\"\",\"user_cbht_name\":\"\",\"user_djhs\":0,\"user_hjhs\":0,\"user_sjdj\":0,\"user_sjhj\":0,\"user_cbht\":\"\",\"user_jjsjk\":\"\",\"parentId\":\"root\",\"checked\":null,\"loading\":false,\"key\":\"\"}";
    private String gridStr="{\"PhId\":\"0\",\"Pphid\":\"\",\"Ppphid\":\"0\",\"SType\":0,\"IsCost\":\"0\",\"PhidCbs\":\"0\",\"PhidCbs_EXName\":\"\",\"PhidItemid\":\"\",\"PhidItemid_EXName\":\"\",\"PhidRestype\":\"0\",\"PhidResbs\":\"\",\"PhidResbs_EXName\":\"\",\"Code\":\"\",\"Cname\":\"\",\"RcjType\":\"\",\"Spec\":\"\",\"PhidMsunit\":\"\",\"PhidMsunit_EXName\":\"\",\"ResAlias\":\"\",\"Note\":\"\",\"Remarks\":\"\",\"Qty\":0,\"Prc\":0,\"Amt\":0,\"Totqty\":0,\"Totamt\":0,\"Ftype\":\"*\",\"MType\":1,\"Cblx\":\"\",\"PhidQuotaD\":\"0\",\"PhidQuota\":\"0\",\"PhidQuota_EXName\":\"\",\"PhidItemdetail\":\"\",\"ResPropertys\":\"\",\"PcoRestQty\":0,\"PcoRestAmt\":0,\"RestQty\":0,\"RestAmt\":0,\"CntRestQty\":0,\"CntRestAmt\":0,\"CntPayRestQty\":0,\"CntPayRestAmt\":0,\"IsFarmProduce\":0,\"PcoControlQty\":0,\"PcoControlAmt\":0,\"ControlQty\":0,\"ControlAmt\":0,\"CntControlQty\":0,\"CntControlAmt\":0,\"CntPayControlQty\":0,\"CntPayControlAmt\":0,\"CostRefFlg\":0,\"CzPlanRestQty\":0,\"CzPlanRestAmt\":0,\"LossRate\":0,\"PhidCbsNew\":\"0\",\"CurrQty\":0,\"CurrChangeQty\":0,\"CurrPrc\":0,\"CurrChangePrc\":0,\"CurrAmt\":0,\"CurrChangeAmt\":0,\"CurrTotqty\":0,\"CurrChangeTotqty\":0,\"CurrTotamt\":0,\"CurrChangeTotamt\":0,\"Pms3BoqMPhid\":\"0\",\"ChangeD\":2,\"PhidCbsNew_EXName\":\"\",\"BeforeLossRate\":0,\"OriPrc\":0,\"OriQty\":0,\"OriAmt\":0,\"OriTotqty\":0,\"OriTotamt\":0,\"OutSpecName\":\"\",\"OutMsunitName\":\"\",\"PhidPc\":\"0\",\"PhidLevel\":\"0\",\"PhidLevel_EXName\":\"\",\"MCname\":\"\",\"MCode\":\"\",\"MPhid\":\"0\",\"PhidQuota_EXCode\":\"\",\"PhidWbs\":\"0\",\"PhidWbs_EXName\":\"\",\"CbsQdId\":\"0\",\"BoqDIsRel\":\"0\",\"MDescript\":\"\",\"user_djhs\":0,\"user_hjhs\":0,\"user_sjdj\":0,\"user_sjhj\":0,\"user_jjsjk\":\"\",\"user_sl\":0,\"key\":null}";

    /**
     * 同步BOQ
     * @param param
     * @param isCost true表示是成本,需要添加表体明细
     * @return
     */
    public DataInfo saveBoqCostChg(BOQModel param,boolean isCost)
    {
        //region 排除未变更的数据  清单项变更就是变更,不需要考虑到费用项级别
        //分部分项
        param.setBQItemInfos(param.getBQItemInfos().stream().filter(f->!f.getStatus().equals("0"))
                .collect(Collectors.toCollection(ArrayList::new)));
        //单价措施费
        param.setMeasureItemInfos(param.getMeasureItemInfos().stream().filter(f->!f.getStatus().equals("0"))
                .collect(Collectors.toCollection(ArrayList::new)));
        //其他
        param.setOtherItemInfos(param.getOtherItemInfos().stream().filter(f->!f.getStatus().equals("0"))
                .collect(Collectors.toCollection(ArrayList::new)));
        //因为税费没有清单项,税费默认都是更改
        //endregion

        String yuflName=isCost?"成本清单变更":"收入清单变更";
        String ysfl=isCost?boqysflCost:boqysflIn;
        DataInfo rvInfo = new DataInfo();
        //判断项目是否存在
        String pcPhid=jdbcTemplate.queryForObject("select phid from project_table where pc_no='"+param.getCode()+"'",String.class);
        if(pcPhid==null)
        {
            rvInfo.setCode("1");
            rvInfo.setStatus("400");
            rvInfo.setErrorText("项目不存在:"+param.getCode());
            return rvInfo;
        }
        var oldPhid=jdbcTemplate.queryForObject("select phid from pms3_boq_bill where change_bill !=1 and phid_pc=" + pcPhid + " and phid_cblx="+ysfl, Long.class);

        //判断是不是已审核或者走工作流(收入清单不走工作流) change_bill=1表示变更
        //变更如果是已审核直接新增,此处不需要做判断
        var wfflg = jdbcTemplate.queryForObject("select wf_flg from pms3_boq_bill where wf_flg=1 and change_bill =1 and phid_pc=" + pcPhid + " and phid_cblx="+ysfl, String.class);
        if(wfflg!=null&&wfflg.equals("1"))//在走工作流
        {
            rvInfo.setErrorText(yuflName+"单据已经申请工作流,不进行同步");
            rvInfo.setStatus("400");
            rvInfo.setCode(param.getCode());
            return rvInfo;
        }

        //判断WBS是否存在
        List<String> wbsCodes=param.getBQItemInfos().stream().filter(f->f!=null).map(m->m.getPId()).collect(Collectors.toList());
        wbsCodes.addAll(param.getBQItemInfos().stream().filter(f->f!=null).map(m->m.getBidNodeID()).collect(Collectors.toList()));
        wbsCodes.addAll(param.getMeasureItemInfos().stream().filter(f->f!=null).map(m->m.getPId()).collect(Collectors.toList()));
        wbsCodes.addAll(param.getMeasureItemInfos().stream().filter(f->f!=null).map(m->m.getBidNodeID()).collect(Collectors.toList()));
        wbsCodes.addAll(param.getOtherItemInfos().stream().filter(f->f!=null).map(m->m.getBidNodeID()).collect(Collectors.toList()));
        wbsCodes=wbsCodes.stream().distinct().filter(f->f!=null&&!f.equals("")).collect(Collectors.toList());
        List<Map<String, Object>> wbsPhids=jdbcTemplate.queryForList("select phid,wbs_realcode,description from bd_wbs  where pcid='"+pcPhid+"' and  wbs_realcode in ('"+ StringUtils.join(wbsCodes,"','")+"')");
        if(wbsCodes.size()!=wbsPhids.size())
        {
            for(Map<String, Object> v:wbsPhids)
            {
                if(wbsCodes.contains(v.get("WBS_REALCODE")))
                {
                    wbsCodes.remove(v.get("WBS_REALCODE"));
                }
            }
            rvInfo.setCode("1");
            rvInfo.setStatus("400");
            rvInfo.setErrorText("WBS不存在:"+JSONObject.toJSONString(wbsCodes));
            return rvInfo;
        }
        //判断CBS是否存在
        ArrayList<String> cbsCodes=new ArrayList<>();
        cbsCodes.addAll(param.getFeeItemInfos().stream().map(m->m.getCourseCode()).collect(Collectors.toList()));
        param.getBQItemInfos().stream().map(m->m.getFeeItemInfos())
                .forEach(f->f.forEach(ff->
                {
                    if(!cbsCodes.contains(ff.getCourseCode()))
                    cbsCodes.add(ff.getCourseCode());
                }));
        param.getMeasureItemInfos().stream().map(m->m.getFeeItemInfos())
                .forEach(f->f.forEach(ff->
                {
                    if(!cbsCodes.contains(ff.getCourseCode()))
                        cbsCodes.add(ff.getCourseCode());
                }));
        List<Map<String, Object>> cbsPhids=jdbcTemplate.queryForList("select phid,cbs_code,cbs_name from bd_cbs  where pcid="+pcPhid+" and cbs_code in ('"+ StringUtils.join(cbsCodes,"','")+"')");
        if(cbsCodes.size()!=cbsPhids.size())
        {
            for(Map<String, Object> v:cbsPhids)
            {
                if(cbsCodes.contains(v.get("CBS_CODE")))
                {
                    cbsCodes.remove(v.get("CBS_CODE"));
                }
            }
            rvInfo.setCode("1");
            rvInfo.setStatus("400");
            rvInfo.setErrorText("CBS不存在:"+JSONObject.toJSONString(cbsCodes));
            return rvInfo;
        }

        //region 判断单位是否存在
        ArrayList<String> msunits=new ArrayList<>();
        msunits.addAll(param.getBQItemInfos().stream().map(m->String.valueOf(m.getUnit())).distinct().collect(Collectors.toList()));
        msunits.addAll(param.getMeasureItemInfos().stream().map(m->String.valueOf(m.getUnit())).distinct().collect(Collectors.toList()));
        msunits.addAll(param.getOtherItemInfos().stream().map(m->String.valueOf(m.getUnit())).distinct().collect(Collectors.toList()));
        msunits.addAll(param.getFeeItemInfos().stream().map(m->String.valueOf(m.getUnit())).distinct().collect(Collectors.toList()));
        param.getBQItemInfos().stream().map(m->m.getFeeItemInfos())
            .forEach(f->f.forEach(ff->
            {
                ff.setUnit("m");//测试使用,测试时对方你未维护该字段,后期取消该默认值
                if(!msunits.contains(ff.getUnit()))
                    msunits.add(ff.getUnit());
            }));
        param.getMeasureItemInfos().stream().map(m->m.getFeeItemInfos())
                .forEach(f->f.forEach(ff->
                {
                    ff.setUnit("m");//测试使用,测试时对方你未维护该字段,后期取消该默认值
                    if(!msunits.contains(ff.getUnit()))
                        msunits.add(ff.getUnit());
                }));
        List<Map<String, Object>> msunitPhids=jdbcTemplate.queryForList("select phid,msname from msunit  where msname in ('"+ StringUtils.join(msunits,"','")+"')");
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
        //endregion

        //region 物料分类查询 如果不存在,费用项的资源编码名称取费用项编码名称
        ArrayList<String> resBsCodes=new ArrayList<>();
        resBsCodes.addAll(param.getFeeItemInfos().stream().map(m->m.getMaterialCode()).distinct().collect(Collectors.toList()));
        param.getBQItemInfos().stream().map(m->m.getFeeItemInfos())
                .forEach(f->f.forEach(ff->
                {
                    if(!resBsCodes.contains(ff.getMaterialCode()))
                        resBsCodes.add(ff.getMaterialCode());
                }));
        param.getMeasureItemInfos().stream().map(m->m.getFeeItemInfos())
                .forEach(f->f.forEach(ff->
                {
                    if(!resBsCodes.contains(ff.getMaterialCode()))
                        resBsCodes.add(ff.getMaterialCode());
                }));
        List<Map<String, Object>> resBsPhids=jdbcTemplate.queryForList("select phid,code,name from res_bs  where pcid="+pcPhid+" and code in ('"+ StringUtils.join(resBsCodes,"','")+"')");


        //endregion
        
        List<NameValuePair> urlParameters = new ArrayList<>();
        Map<String, ArrayList<Map<String, Object>>> allData=paramBoqCostChg(param,isCost,ysfl,pcPhid,wbsPhids,cbsPhids,
                msunitPhids,oldPhid,resBsPhids);
        ArrayList<Map<String, Object>> mstformDatas=allData.get("MstformData");
        ArrayList<Map<String, Object>> boqmtreeDataFBs=allData.get("boqmtreeDataFB");
        ArrayList<Map<String, Object>> boqdgridDataFBs=allData.get("boqdgridDataFB");
        ArrayList<Map<String, Object>> boqmtreeDataDJs=allData.get("boqmtreeDataDJ");
        ArrayList<Map<String, Object>> boqdgridDataDJs=allData.get("boqdgridDataDJ");
        ArrayList<Map<String, Object>> boqmtreeDataQTs=allData.get("boqmtreeDataQT");
        ArrayList<Map<String, Object>> boqdgridDataQTs=allData.get("boqdgridDataQT");
//        ArrayList<Map<String, Object>> boqmtreeDataGFs=allData.get("boqmtreeDataGF");
        StringBuilder mStr=new StringBuilder();
        StringBuilder fbStr=new StringBuilder();
        StringBuilder fbGridStr=new StringBuilder();
        StringBuilder djStr=new StringBuilder();
        StringBuilder djGridStr=new StringBuilder();
        StringBuilder qtStr=new StringBuilder();
        StringBuilder qtGridStr=new StringBuilder();
//        StringBuilder gfStr=new StringBuilder();

        ///region 表头拼接
        mStr.append("{\"form\":{\"key\":\"PhId\"");
        if(mstformDatas.get(0).get("key")==null||mstformDatas.get(0).get("key").equals("")) {
            mStr.append(",\"newRow\":" + JSONObject.toJSONString(mstformDatas.get(0)));
        }
        else {
            mStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(mstformDatas.get(0)));
        }
        mStr.append("}}");
        ///endregion

        ///region 分部分项工程量清单拼接
        fbStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> fbNew= (ArrayList<Map<String, Object>>) boqmtreeDataFBs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")==null||((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> fbModify= (ArrayList<Map<String, Object>>) boqmtreeDataFBs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")!=null&&!((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
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
        ArrayList<Map<String, Object>> fbGridNew= (ArrayList<Map<String, Object>>) boqdgridDataFBs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")==null||((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> fbGridModify= (ArrayList<Map<String, Object>>) boqdgridDataFBs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")!=null&&!((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
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
        ArrayList<Map<String, Object>> djNew= (ArrayList<Map<String, Object>>) boqmtreeDataDJs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")==null||((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> djModify= (ArrayList<Map<String, Object>>) boqmtreeDataDJs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")!=null&&!((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
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
        ArrayList<Map<String, Object>> djGridNew= (ArrayList<Map<String, Object>>) boqdgridDataDJs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")==null||((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> djGridModify= (ArrayList<Map<String, Object>>) boqdgridDataDJs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")!=null&&!((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
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
        ArrayList<Map<String, Object>> qtNew= (ArrayList<Map<String, Object>>) boqmtreeDataQTs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")==null||((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> qtModify= (ArrayList<Map<String, Object>>) boqmtreeDataQTs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")!=null&&!((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
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

        ///region 其他费用明细拼接(存储对方的规费与税金)
        qtGridStr.append("{\"table\":{\"key\":\"PhId\"");
        ArrayList<Map<String, Object>> qtGridNew= (ArrayList<Map<String, Object>>) boqdgridDataQTs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")==null||((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
        ArrayList<Map<String, Object>> qtGridModify= (ArrayList<Map<String, Object>>) boqdgridDataQTs.stream().filter(f->((Map<String,Object>)f.get("row")).get("key")!=null&&!((Map<String,Object>)f.get("row")).get("key").equals("")).collect(Collectors.toList());
        if(qtGridNew.size()>0)
        {
            qtGridStr.append(",\"newRow\":" + JSONObject.toJSONString(qtGridNew));
        }
        if(qtGridModify.size()>0)
        {
            qtGridStr.append(",\"modifiedRow\":" + JSONObject.toJSONString(qtGridModify));
        }
        qtGridStr.append("},\"isChanged\":true}");
        ///endregion


        urlParameters.add(new BasicNameValuePair("mstformData", mStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataFB", fbStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataDJ", djStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataQT", qtStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataGF", "{\"table\":{\"key\":\"PhId\"}}"));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataSP", "{\"table\":{\"key\":\"PhId\"}}"));
        urlParameters.add(new BasicNameValuePair("boqmtreeDataZJ", "{\"table\":{\"key\":\"PhId\"}}"));
        urlParameters.add(new BasicNameValuePair("boqqtgridDataFB", "{\"table\":{\"key\":\"PhId\"}}"));
        urlParameters.add(new BasicNameValuePair("boqqtgridDataDJ", "{\"table\":{\"key\":\"PhId\"}}"));
        urlParameters.add(new BasicNameValuePair("boqdgridDataFB", fbGridStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqdgridDataDJ", djGridStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqdgridDataQT", qtGridStr.toString()));
        urlParameters.add(new BasicNameValuePair("boqmrelwbs", "[]"));
        urlParameters.add(new BasicNameValuePair("oldphid", oldPhid.toString()));
        urlParameters.add(new BasicNameValuePair("boqbill","1"));
        urlParameters.add(new BasicNameValuePair("isContinue", "false"));
        urlParameters.add(new BasicNameValuePair("attchmentGuid", "0"));
        try {
                log.info("BOQ参数:"+JSONObject.toJSONString(urlParameters));
                String i8rv = i8Request.PostFormSync("/PMS/PCO/BOQEQ/BoqBill/SaveChg", urlParameters);
                JSONObject i8rvJson = JSON.parseObject(i8rv);
                if (i8rvJson != null && i8rvJson.getString("Status").toLowerCase().equals("success")) {
                    rvInfo.setStatus("0");
                    rvInfo.setErrorText("记录保存成功");
                    rvInfo.setCode(i8rvJson.getJSONArray("KeyCodes").get(0).toString());
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
    public Map<String, ArrayList<Map<String, Object>>> paramBoqCostChg(BOQModel param,boolean isCost,String ysfl,
                                                                    String pcPhid,List<Map<String, Object>> wbsPhids,
                                                                    List<Map<String, Object>> cbsPhids,
                                                                    List<Map<String, Object>> msunitPhids,
                                                                    Long oldPhid,List<Map<String, Object>> resBsPhids)
    {
        Map<String, ArrayList< Map<String, Object>>> result=new HashMap<>();

        Long minPhid=-1L;
        Long phid = -1L;//用于新增初始化本级暂存id
        Long level2Phid = 0L;//初始化本级暂存id
        Long level2ParentPhid = 0L;//初始化末级暂存id
        Long level3Phid = 0L;//初始化本级暂存id
        Long level3ParentPhid = 0L;//初始化末级暂存id

        phid = jdbcTemplate.queryForObject("select phid from pms3_boq_bill where change_bill =1 and wf_flg!=1 and chk_flg !=1 and phid_pc=" + pcPhid + " and phid_cblx="+ysfl, Long.class);
        if(phid==null)
            phid = minPhid;

        //region  查询计算表头数据 清单的经济数据库id  用于查询变更前总价
        var QDjjsjkids=new ArrayList<String>();
        QDjjsjkids.addAll(param.getBQItemInfos().stream().map(m->m.getId()).collect(Collectors.toList()));
        QDjjsjkids.addAll(param.getMeasureItemInfos().stream().map(m->m.getId()).collect(Collectors.toList()));
        QDjjsjkids.addAll(param.getOtherItemInfos().stream().map(m->m.getId()).collect(Collectors.toList()));
        QDjjsjkids.add("xzd_9999");
        //变更前值
        var befortAmt=jdbcTemplate.queryForObject("select sum(curramt) from pms3_boq_m where pphid="+oldPhid+" and phid in (" +
                "select distinct phid from pms3_boq_m where change_bill!=1 and  user_jjsjk in ('"+StringUtils.join(QDjjsjkids.toArray(),"','")+"')\n" +
                "union \n" +
                "select distinct pms3_boq_m_phid from pms3_boq_m where change_bill=1 and  user_jjsjk in ('"+StringUtils.join(QDjjsjkids.toArray(),"','")+"')", Double.class);
        //变更后值
        double afterAmt= 0;
        afterAmt+=param.getBQItemInfos().stream().mapToDouble(f -> Double.parseDouble(f.getTotal())).sum();
        afterAmt+=param.getMeasureItemInfos().stream().mapToDouble(f -> Double.parseDouble(f.getTotal())).sum();
        afterAmt+=param.getOtherItemInfos().stream().mapToDouble(f -> Double.parseDouble(f.getTotal())).sum();
        //变更值
        double currentAmt=afterAmt-befortAmt;

        //endregion

        //region 查询清单信息

        RowMapper<BOQChangeMModel> rowMapper=new BeanPropertyRowMapper(BOQChangeMModel.class);
        List<BOQChangeMModel> oldValuesM=jdbcTemplate.query("select distinct user_jjsjk jjsjk,ori_qty oriQty,\n" +
                "curr_qty currqty,ori_prc oriprc,curr_prc currprc,ori_amt oriamt,curr_amt curramt,phid oldphid\n" +
                "from pms3_boq_m where user_jjsjk in ('"+StringUtils.join(QDjjsjkids.toArray(),"','")+"')\n" +
                "or phid in (\n" +
                "select distinct pms3_boq_m_phid from pms3_boq_m where change_m=1 and  user_jjsjk \n" +
                "in ('"+StringUtils.join(QDjjsjkids.toArray(),"','")+"'))",rowMapper);

        //endregion

        //region 查询费用项信息
        var Feejjsjkids=new ArrayList<String>();
        Feejjsjkids.addAll(param.getFeeItemInfos().stream().map(m->m.getId()).collect(Collectors.toList()));
        param.getBQItemInfos().stream().map(m->m.getFeeItemInfos())
                .forEach(f->f.forEach(ff->
                {
                    ff.setUnit("m");//测试使用,测试时对方你未维护该字段,后期取消该默认值
                    if(!Feejjsjkids.contains(ff.getId()))
                        Feejjsjkids.add(ff.getId());
                }));
        param.getMeasureItemInfos().stream().map(m->m.getFeeItemInfos())
                .forEach(f->f.forEach(ff->
                {
                    ff.setUnit("m");//测试使用,测试时对方你未维护该字段,后期取消该默认值
                    if(!Feejjsjkids.contains(ff.getId()))
                        Feejjsjkids.add(ff.getId());
                }));

        List<BOQChangeDModel> oldValuesD=jdbcTemplate.query("select distinct user_jjsjk jjsjk,qty oriQty,\n" +
                "amt oriPrc,totamt oriamt,totqty oriTotQty,totamt oriTotAmt,phid oldphid\n" +
                "from pms3_boq_d where user_jjsjk in ('"+StringUtils.join(Feejjsjkids.toArray(),"','")+"')\n" +
                "or phid in (\n" +
                "select distinct pms3_boq_m_phid from pms3_boq_d where change_d=1 and  user_jjsjk " +
                "in ('"+StringUtils.join(Feejjsjkids.toArray(),"','")+"'))",new BeanPropertyRowMapper(BOQChangeDModel.class));
        //endregion

        ArrayList<Map<String, Object>> MstformData=new ArrayList<>();
        MstformData.add(paramMstformDataChg(pcPhid,phid,ysfl,param.getUrl(),param.getUserCode(),oldPhid,befortAmt,currentAmt,afterAmt));

        ArrayList<Map<String, Object>> boqmtreeDataFBs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqdgridDataFBs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqmtreeDataDJs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqdgridDataDJs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqmtreeDataQTs=new ArrayList<>();
        ArrayList<Map<String, Object>> boqdgridDataQTs=new ArrayList<>();

        level2ParentPhid=phid;
        for(BOQBQModel v :param.getBQItemInfos()) {
            minPhid--;
            level2Phid=jdbcTemplate.queryForObject("select phid from pms3_boq_m where pphid="+level2ParentPhid+"  and user_jjsjk='"+v.getId()+"'", Long.class);
            if(level2Phid==null)
                level2Phid=minPhid;;
            Map<String, Object> temp=new HashMap<>();
            var tempOldValueM=oldValuesM.stream().filter(f->f.getJjsjk().equals(v.getId())).collect(Collectors.toList());
            BOQChangeMModel oldValueM=tempOldValueM==null||tempOldValueM.size()==0? null:tempOldValueM.get(0);
            temp=boqmtreeDataFBChg(v,level2Phid,level2ParentPhid,wbsPhids,msunitPhids,oldValueM,isCost);
            boqmtreeDataFBs.add(temp);
            level3ParentPhid=level2Phid;
            if(isCost) {
                for (BOQBQFeeModel f : v.getFeeItemInfos()) {
                    minPhid --;
                    level3Phid = jdbcTemplate.queryForObject("select phid from pms3_boq_d where pphid="+level3ParentPhid+"  and user_jjsjk='" + f.getId() + "'", Long.class);
                    if (level3Phid==null)
                        level3Phid=minPhid;
                    Map<String, Object> tempFee = new HashMap<>();
                    var tempOldValueD=oldValuesD.stream().filter(d->d.getJjsjk().equals(f.getId())).collect(Collectors.toList());
                    BOQChangeDModel oldValueD=tempOldValueD==null||tempOldValueD.size()==0? null:tempOldValueD.get(0);
                    tempFee = boqdgridDataFBChg(f, level3Phid, level3ParentPhid,cbsPhids,msunitPhids,oldValueD,
                            v.getQuantity(),resBsPhids);
                    boqdgridDataFBs.add(tempFee);
                }
            }
        }
        for(BOQMeasureModel v :param.getMeasureItemInfos()) {
            minPhid--;
            level2Phid=jdbcTemplate.queryForObject("select phid from pms3_boq_m where pphid="+level2ParentPhid+"  and user_jjsjk='"+v.getId()+"'", Long.class);
            if(level2Phid==null)
                level2Phid=minPhid;
            Map<String, Object> temp=new HashMap<>();
            var tempOldValueM=oldValuesM.stream().filter(f->f.getJjsjk().equals(v.getId())).collect(Collectors.toList());
            BOQChangeMModel oldValueM=tempOldValueM==null||tempOldValueM.size()==0? null:tempOldValueM.get(0);
            temp=boqmtreeDataDJChg(v,level2Phid,level2ParentPhid,wbsPhids,msunitPhids,oldValueM,isCost);
            boqmtreeDataDJs.add(temp);
            level3ParentPhid=level2Phid;
            if(isCost) {
                for (BOQMeasureFeeModel f : v.getFeeItemInfos()) {
                    minPhid--;
                    level3Phid = jdbcTemplate.queryForObject("select phid from pms3_boq_d where pphid="+level3ParentPhid+"  and user_jjsjk='" + f.getId() + "'", Long.class);
                    if (level3Phid==null)
                        level3Phid = minPhid;
                    Map<String, Object> tempFee = new HashMap<>();
                    var tempOldValueD=oldValuesD.stream().filter(d->d.getJjsjk().equals(f.getId())).collect(Collectors.toList());
                    BOQChangeDModel oldValueD=tempOldValueD==null||tempOldValueD.size()==0? null:tempOldValueD.get(0);
                    tempFee = boqdgridDataDJChg(f, level3Phid, level3ParentPhid, cbsPhids,msunitPhids,oldValueD,
                            v.getQuantity(),resBsPhids);
                    boqdgridDataDJs.add(tempFee);
                }
            }
        }
        for(BOQOtherModel v :param.getOtherItemInfos()) {
            minPhid--;
            level2Phid=jdbcTemplate.queryForObject("select phid from pms3_boq_m where pphid="+level2ParentPhid+"  and user_jjsjk='"+v.getId()+"'", Long.class);
            if(level2Phid==null)
                level2Phid=minPhid;
            Map<String, Object> temp=new HashMap<>();
            var tempOldValueM=oldValuesM.stream().filter(f->f.getJjsjk().equals(v.getId())).collect(Collectors.toList());
            BOQChangeMModel oldValueM=tempOldValueM==null||tempOldValueM.size()==0? null:tempOldValueM.get(0);
            temp=boqmtreeDataQTChg(v,level2Phid,level2ParentPhid,wbsPhids,msunitPhids,oldValueM);
            boqmtreeDataQTs.add(temp);
        }
        if(isCost) {//如果是成本清单,需要将规费税金同步到其他费用里面,并且在清单项中添加一个固定的清单项
            String total=String.valueOf(param.getFeeItemInfos().stream().mapToDouble(m->Double.parseDouble(m.getTaxTotal())).sum());
            String tolQty=String.valueOf(param.getFeeItemInfos().stream().mapToDouble(m->Double.parseDouble(m.getQuantity())).sum());
            BOQOtherModel tempOther=new BOQOtherModel();
            tempOther.setCode("9999");
            tempOther.setBidNodeID("");
            tempOther.setContractCode("");
            tempOther.setId("xzd_9999");
            tempOther.setName("其他费用项");
            tempOther.setUnit("m");
            tempOther.setTotal(total);
            tempOther.setStatus("");
            minPhid--;
            level2Phid=jdbcTemplate.queryForObject("select phid from pms3_boq_m where pphid="+level2ParentPhid+"  and phid_pc=" + pcPhid + " and code='"+tempOther.getCode()+"'", Long.class);
            if(level2Phid==null)
                level2Phid=minPhid;
            Map<String, Object> temp=new HashMap<>();
            var tempOldValueM=oldValuesM.stream().filter(f->f.getJjsjk().equals("xzd_9999")).collect(Collectors.toList());
            BOQChangeMModel oldValueM=tempOldValueM==null||tempOldValueM.size()==0? null:tempOldValueM.get(0);
            temp=boqmtreeDataQTChg(tempOther,level2Phid,level2ParentPhid,wbsPhids,msunitPhids,oldValueM);
            boqmtreeDataQTs.add(temp);
            level3ParentPhid=level2Phid;
            for (BOQFeeModel f : param.getFeeItemInfos()) {
                minPhid--;
                level3Phid = jdbcTemplate.queryForObject("select phid from pms3_boq_d where pphid="+level3ParentPhid+"  and user_jjsjk='" + f.getId() + "'", Long.class);
                if (level3Phid==null)
                    level3Phid = minPhid;
                Map<String, Object> tempFee = new HashMap<>();
                var tempOldValueD=oldValuesD.stream().filter(d->d.getJjsjk().equals(f.getId())).collect(Collectors.toList());
                BOQChangeDModel oldValueD=tempOldValueD==null||tempOldValueD.size()==0? null:tempOldValueD.get(0);
                tempFee = boqdgridDataQTChg(f, level3Phid, level3ParentPhid, cbsPhids,msunitPhids,oldValueD,tolQty,resBsPhids);
                boqdgridDataQTs.add(tempFee);
            }
        }

        result.put("MstformData", MstformData);
        result.put("boqmtreeDataFB",boqmtreeDataFBs);
        result.put("boqdgridDataFB",boqdgridDataFBs);
        result.put("boqmtreeDataDJ",boqmtreeDataDJs);
        result.put("boqdgridDataDJ",boqdgridDataDJs);
        result.put("boqmtreeDataQT",boqmtreeDataQTs);
        result.put("boqdgridDataQT",boqdgridDataQTs);
        return result;
    }

    /**
     * 封装 表头 参数MstformData 是否是新增判断key是否有值
     * @param pcPhid 项目phid
     * @return
     */
    public Map<String, Object> paramMstformDataChg(String pcPhid,Long phid,String ysfl,String url,String userCode,
                                                Long oldPhid,Double befortAmt,Double currentAmt,Double AfterAmt) {
        String org=jdbcTemplate.queryForObject("select cat_phid from project_table where phid="+pcPhid,String.class);
        String zdrId=jdbcTemplate.queryForObject("select phid from hr_epm_main where cno='"+userCode+"'",String.class);

        String insertDt=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        HashMap<String, Object> row = new HashMap<>();
        row.put("BefortAmt",  befortAmt);
        row.put("CurrentAmt", currentAmt );
        row.put("AfterAmt", AfterAmt );
        row.put("PhidPc", pcPhid);
        row.put("PhidCblx", ysfl);
        row.put("PhidOcode", org);
        row.put("NgInsertDt", insertDt);
        row.put("NgUpdateDt", insertDt);
        row.put("Creator", zdrId);
        row.put("Editor", zdrId);
        row.put("CurOrgId", org);
        row.put("Pms3BoqBillPhid", oldPhid);
        row.put("PhidTask", "");
        row.put("key", "");
        if(phid>0)
        {
            row.put("key", phid);
        }
        row=new EntityConverter().SetFieldMap(billStr,row);
        return row;
    }

    /**
     * 封装 分部分项 参数boqmtreeDataFB 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqmtreeDataFBChg(BOQBQModel itemInfo, Long phid,Long parentPhid,
                                              List<Map<String, Object>> wbsPhids,List<Map<String, Object>> msunitPhids,
                                              BOQChangeMModel changeM,boolean isCost) {

        if(changeM==null)
        {
            changeM=new BOQChangeMModel();
            changeM.setOriQty((double) 0);
            changeM.setCurrQty((double) 0);
            changeM.setOriPrc((double) 0);
            changeM.setCurrPrc((double) 0);
            changeM.setOriAmt((double) 0);
            changeM.setCurrAmt((double) 0);
            changeM.setOldPhid("0");
        }
        String wbsCode=itemInfo.getPId()==null||itemInfo.getPId().equals("")?itemInfo.getBidNodeID(): itemInfo.getPId();
        String wbsPhid="";
        String wbsName="";
        for(Map<String, Object> v : wbsPhids)
        {
            if(String.valueOf(v.get("WBS_REALCODE")).equals(wbsCode))
            {
                wbsPhid= String.valueOf(v.get("PHID"));
                wbsName= String.valueOf(v.get("DESCRIPTION"));
                break;
            }
        }
        String msunitPhid="";
        for(Map<String, Object> v : msunitPhids)
        {
            if(String.valueOf(v.get("MSNAME")).equals(wbsCode))
            {
                msunitPhid= String.valueOf(v.get("PHID"));
                break;
            }
        }

        String cntPhid=jdbcTemplate.queryForObject("select phid from pcm3_cnt_m where cnt_type='5' and bill_no='"+itemInfo.getContractCode()+"'",String.class);
        String insertDt=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        Map<String, Object> newRow = new HashMap<>();
        HashMap<String, Object> row = new HashMap<>();
        row.put("id", phid);
        row.put("PhId", phid);
        row.put("Pphid", parentPhid);
        row.put("NgInsertDt", insertDt);
        row.put("NgUpdateDt", insertDt);
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        row.put("Descript", itemInfo.getSpec());
        row.put("PhidWbs", wbsPhid);
        row.put("PhidWbs_EXName", wbsName);
        row.put("PhidMsunit", msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
        row.put("MType", 1);

        row.put("Rate", 0);
        row.put("Qty", itemInfo.getQuantity());
        row.put("Prc", itemInfo.getRate());
        row.put("Amt", isCost?Double.parseDouble(itemInfo.getQuantity())*Double.parseDouble(itemInfo.getCostNoRate()):itemInfo.getTotal());
        row.put("OriQty", changeM.getOriQty());//原始工程量
        row.put("CurrQty", changeM.getCurrQty());//当前工程量
        row.put("CurrChangeQty", Double.parseDouble(itemInfo.getQuantity())-changeM.getCurrQty());//本次变更工程量
        row.put("OriPrc", changeM.getOriPrc());//
        row.put("CurrPrc", changeM.getCurrPrc());//
        row.put("CurrChangePrc", Double.parseDouble(itemInfo.getRate())-changeM.getCurrPrc());//
        row.put("OriAmt", changeM.getOriAmt());//
        row.put("CurrAmt", changeM.getCurrAmt());//
        row.put("CurrChangeAmt", Double.parseDouble(itemInfo.getTotal())-changeM.getCurrAmt());//

        row.put("checked", "");

        row.put("Pms3BoqMPhid", changeM.getOldPhid());
        row.put("key", "");
        row.put("user_jjsjk", itemInfo.getId());
        row.put("user_djhs", itemInfo.getCostRate());
        row.put("user_hjhs", Double.parseDouble(itemInfo.getCostRate())*Double.parseDouble(itemInfo.getQuantity()));
        row.put("user_sjdj", Double.parseDouble(itemInfo.getSjTotal())/Double.parseDouble(itemInfo.getQuantity()));
        row.put("user_sjhj", itemInfo.getSjTotal());
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
            row.put("id", phid);
        }
        row=new EntityConverter().SetFieldMap(mStr, row);
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装 分部分项明细 参数boqdgridDataFB 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqdgridDataFBChg(BOQBQFeeModel itemInfo, Long phid,Long parentPhid,
                                              List<Map<String, Object>> wbsPhids,List<Map<String, Object>> msunitPhids,
                                              BOQChangeDModel changD,String mQty,List<Map<String, Object>> resBsPhids) {

        if(changD==null)
        {
            changD=new BOQChangeDModel();
            changD.setJjsjk("");
            changD.setOldPhid("");
            changD.setOriAmt((double) 0);
            changD.setOriPrc((double) 0);
            changD.setOriQty((double) 0);
            changD.setOriTotAmt((double) 0);
            changD.setOriTotQty((double) 0);
        }
        itemInfo.setUnit("m");
        String cbsPhid="";
        String cbsName="";
        for(Map<String, Object> v : wbsPhids)
        {
            if(String.valueOf(v.get("CBS_CODE")).equals(itemInfo.getCourseCode()))
            {
                cbsPhid= String.valueOf(v.get("PHID"));
                cbsName= String.valueOf(v.get("CBS_NAME"));
                break;
            }
        }
        String msunitPhid="";
        for(Map<String, Object> v : msunitPhids)
        {
            if(String.valueOf(v.get("MSNAME")).equals(itemInfo.getUnit()))
            {
                msunitPhid= String.valueOf(v.get("PHID"));
                break;
            }
        }
        Map<String, Object> newRow = new HashMap<>();
        HashMap<String, Object> row = new HashMap<>();
        row.put("PhId", "");
        row.put("Pphid", parentPhid);
        row.put("PhidCbs", cbsPhid);
        row.put("PhidCbs_EXName", cbsName);
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        //FeeType：1-劳务分包 2-材料费 3-周转材料费 4-机械费 8-专业分包
        //新中大   2			7		8			6			3
        String fType=itemInfo.getFeeType();//分包方式
        fType=fType.equals("1")?"2":fType.equals("2")?"7":fType.equals("3")?
                "8":fType.equals("4")?"6":fType.equals("8")?"3":"*";
        row.put("Ftype", fType);
        row.put("Spec", itemInfo.getSpec());
        row.put("PhidMsunit",msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());

        row.put("Amt", Double.parseDouble(itemInfo.getQuantity())/Double.parseDouble(mQty)*Double.parseDouble(itemInfo.getNoTaxRate()));
        row.put("Qty",Double.parseDouble(itemInfo.getQuantity())/Double.parseDouble(mQty));//单耗量=费用项总量/清单总量
        row.put("Prc", itemInfo.getNoTaxRate());
        row.put("OriPrc",changD.getOriPrc());
        row.put("OriQty",changD.getOriQty());
        row.put("OriAmt",changD.getOriAmt());
        row.put("OriTotqty",changD.getOriTotQty());
        row.put("OriTotamt",changD.getOriTotAmt());
        row.put("Pms3BoqMPhid",changD.getOldPhid());

        row.put("MType", 1);

        row.put("key", "");
        row.put("user_sl", Double.parseDouble(itemInfo.getZzsl())*0.01);
        row.put("user_jjsjk", itemInfo.getId());
        row.put("user_zhdjjs", itemInfo.getTaxRate());
        row.put("user_djhs", Double.parseDouble(itemInfo.getQuantity())/Double.parseDouble(mQty)*Double.parseDouble(itemInfo.getNoTaxRate()));
        row.put("user_hjhs", itemInfo.getTaxTotal());
        row.put("user_sjdj", itemInfo.getSjRate());
        row.put("user_sjhj", itemInfo.getSjTotal());
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        row=new EntityConverter().SetFieldMap(gridStr, row);
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装 单价措施费 参数boqmtreeDataDJ 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqmtreeDataDJChg(BOQMeasureModel itemInfo, Long phid,Long parentPhid,
                                              List<Map<String, Object>> wbsPhids,List<Map<String, Object>> msunitPhids,
                                              BOQChangeMModel changeM,boolean isCost) {
        if(changeM==null)
        {
            changeM=new BOQChangeMModel();
            changeM.setOriQty((double) 0);
            changeM.setCurrQty((double) 0);
            changeM.setOriPrc((double) 0);
            changeM.setCurrPrc((double) 0);
            changeM.setOriAmt((double) 0);
            changeM.setCurrAmt((double) 0);
            changeM.setOldPhid("0");
        }
        String wbsCode=itemInfo.getPId()==null||itemInfo.getPId().equals("")?itemInfo.getBidNodeID(): itemInfo.getPId();
//        String wbsPhid=jdbcTemplate.queryForObject("select phid from bd_wbs  where pcid='"+pcPhid+"' and wbs_realcode='"+wbsCode+"'",String.class);
//        String wbsName=jdbcTemplate.queryForObject("select description from bd_wbs  where pcid='"+pcPhid+"' and wbs_realcode='"+wbsCode+"'",String.class);
//        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        String wbsPhid="";
        String wbsName="";
        for(Map<String, Object> v : wbsPhids)
        {
            if(String.valueOf(v.get("WBS_REALCODE")).equals(wbsCode))
            {
                wbsPhid= String.valueOf(v.get("PHID"));
                wbsName= String.valueOf(v.get("DESCRIPTION"));
                break;
            }
        }
        String msunitPhid="";
        for(Map<String, Object> v : msunitPhids)
        {
            if(String.valueOf(v.get("MSNAME")).equals(wbsCode))
            {
                msunitPhid= String.valueOf(v.get("PHID"));
                break;
            }
        }
        String cntPhid=jdbcTemplate.queryForObject("select phid from pcm3_cnt_m where cnt_type='5' and bill_no='"+itemInfo.getContractCode()+"'",String.class);
        cntPhid=cntPhid==null?"":cntPhid;
        String insertDt=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        Map<String, Object> newRow = new HashMap<>();
        HashMap<String, Object> row = new HashMap<>();
        row.put("id", phid);
        row.put("PhId", phid);

        row.put("Pphid", parentPhid);
        row.put("NgInsertDt", insertDt);
        row.put("NgUpdateDt", insertDt);
//        row.put("Creator", "");
//        row.put("Creator_EXName", "");
//        row.put("Editor", "");
//        row.put("Editor_EXName", "");
//        row.put("PhidPc", "");
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        row.put("Descript", itemInfo.getSpec());
        row.put("PhidWbs", wbsPhid);
        row.put("PhidWbs_EXName", wbsName);
        row.put("PhidMsunit", msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());
//        row.put("PhidLevel", "");
//        row.put("PhidLevel_EXName", "");
        row.put("MType", 2);
//        row.put("IsCnt", 1);
//        row.put("Expression", "");
//        row.put("PhidCbs", "");
//        row.put("PhidCbs_EXName", "");
        row.put("Rate", 0);
//        row.put("Remarks", "");
        row.put("Qty", itemInfo.getQuantity());
        row.put("Prc", itemInfo.getRate());
        row.put("Amt", isCost?Double.parseDouble(itemInfo.getQuantity())*Double.parseDouble(itemInfo.getCostNoRate()):itemInfo.getTotal());
        row.put("OriQty", changeM.getOriQty());//原始工程量
        row.put("CurrQty", changeM.getCurrQty());//当前工程量
        row.put("CurrChangeQty", Double.parseDouble(itemInfo.getQuantity())-changeM.getCurrQty());//本次变更工程量
        row.put("OriPrc", changeM.getOriPrc());//
        row.put("CurrPrc", changeM.getCurrPrc());//
        row.put("CurrChangePrc", Double.parseDouble(itemInfo.getRate())-changeM.getCurrPrc());//
        row.put("OriAmt", changeM.getOriAmt());//
        row.put("CurrAmt", changeM.getCurrAmt());//
        row.put("CurrChangeAmt", Double.parseDouble(itemInfo.getTotal())-changeM.getCurrAmt());//

        row.put("checked", "");

        row.put("Pms3BoqMPhid", changeM.getOldPhid());
        row.put("key", "");
        row.put("user_jjsjk", itemInfo.getId());
        row.put("user_djhs", itemInfo.getCostRate());
        row.put("user_hjhs", Double.parseDouble(itemInfo.getCostRate())*Double.parseDouble(itemInfo.getQuantity()));
        row.put("user_sjdj", Double.parseDouble(itemInfo.getSjTotal())/Double.parseDouble(itemInfo.getQuantity()));
        row.put("user_sjhj", itemInfo.getSjTotal());
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
            row.put("id", phid);
        }
        row=new EntityConverter().SetFieldMap(mStr, row);
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装 单价措施费明细  参数boqdgridDataDJ 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqdgridDataDJChg(BOQMeasureFeeModel itemInfo, Long phid,Long parentPhid,
                                              List<Map<String, Object>> wbsPhids,List<Map<String, Object>> msunitPhids,
                                              BOQChangeDModel changD,String mQty,List<Map<String, Object>> resBsPhids) {
        if(changD==null)
        {
            changD=new BOQChangeDModel();
            changD.setJjsjk("");
            changD.setOldPhid("");
            changD.setOriAmt((double) 0);
            changD.setOriPrc((double) 0);
            changD.setOriQty((double) 0);
            changD.setOriTotAmt((double) 0);
            changD.setOriTotQty((double) 0);
        }
        itemInfo.setUnit("m");
//        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
//        String cbsPhid=jdbcTemplate.queryForObject("select phid from bd_cbs where cbs_code='"+itemInfo.getCourseCode()+"' and pcid="+pcPhid,String.class);
//        String cbsName=jdbcTemplate.queryForObject("select cbs_name from bd_cbs where cbs_code='"+itemInfo.getCourseCode()+"' and pcid="+pcPhid,String.class);
        String cbsPhid="";
        String cbsName="";
        for(Map<String, Object> v : wbsPhids)
        {
            if(String.valueOf(v.get("CBS_CODE")).equals(itemInfo.getCourseCode()))
            {
                cbsPhid= String.valueOf(v.get("PHID"));
                cbsName= String.valueOf(v.get("CBS_NAME"));
                break;
            }
        }
        String msunitPhid="";
        for(Map<String, Object> v : msunitPhids)
        {
            if(String.valueOf(v.get("MSNAME")).equals(itemInfo.getUnit()))
            {
                msunitPhid= String.valueOf(v.get("PHID"));
                break;
            }
        }
        Map<String, Object> newRow = new HashMap<>();
        HashMap<String, Object> row = new HashMap<>();
        row.put("PhId", "");
        row.put("Pphid", parentPhid);
//        row.put("Ppphid", "");
//        row.put("SType", "");
        row.put("IsCost", "1");
        row.put("PhidCbs", cbsPhid);
        row.put("PhidCbs_EXName", cbsName);
//        row.put("PhidItemid", "");
//        row.put("PhidItemid_EXName", "");
//        row.put("PhidRestype", "");
//        row.put("PhidResbs", "");
//        row.put("PhidResbs_EXName", "");
        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        //FeeType：1-劳务分包 2-材料费 3-周转材料费 4-机械费 8-专业分包
        //新中大   2			7		8			6			3
        String fType=itemInfo.getFeeType();//分包方式
        fType=fType.equals("1")?"2":fType.equals("2")?"7":fType.equals("3")?
                "8":fType.equals("4")?"6":fType.equals("8")?"3":"*";
        row.put("Ftype", fType);
        row.put("Spec", itemInfo.getSpec());
        row.put("PhidMsunit",msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());

        row.put("Amt", Double.parseDouble(itemInfo.getQuantity())/Double.parseDouble(mQty)*Double.parseDouble(itemInfo.getNoTaxRate()));
        row.put("Qty",Double.parseDouble(itemInfo.getQuantity())/Double.parseDouble(mQty));//单耗量=费用项总量/清单总量
        row.put("Prc", itemInfo.getNoTaxRate());
        row.put("OriPrc",changD.getOriPrc());
        row.put("OriQty",changD.getOriQty());
        row.put("OriAmt",changD.getOriAmt());
        row.put("OriTotqty",changD.getOriTotQty());
        row.put("OriTotamt",changD.getOriTotAmt());
        row.put("Pms3BoqMPhid",changD.getOldPhid());
        row.put("MType", 2);
//        row.put("Cblx", "");
//        row.put("PhidQuotaD", "");
//        row.put("PhidQuota", "");
//        row.put("PhidQuota_EXName", "");
//        row.put("PhidItemdetail", "");
//        row.put("ResPropertys", "");
//        row.put("PcoRestQty", 0);
//        row.put("PcoRestAmt", 0);
//        row.put("RestQty", 0);
//        row.put("RestAmt", 0);
//        row.put("CntRestQty", 0 );
//        row.put("CntRestAmt", 0);
//        row.put("CntPayRestQty", 0);
//        row.put("CntPayRestAmt", 0);
//        row.put("IsFarmProduce", 0);
//        row.put("PcoControlQty", 0);
//        row.put("PcoControlAmt", 0);
//        row.put("ControlQty", 0);
//        row.put("ControlAmt", 0);
//        row.put("CntControlQty", "");
//        row.put("CntControlAmt", 0);
//        row.put("CntPayControlQty", 0);
//        row.put("CntPayControlAmt", 0);
//        row.put("CostRefFlg", 0);
//        row.put("CzPlanRestQty", 0);
//        row.put("CzPlanRestAmt", 0);
//        row.put("LossRate", 0);
//        row.put("PhidCbsNew", "");
//        row.put("CurrQty", 0);
//        row.put("CurrChangeQty", 0);
//        row.put("CurrPrc", 0);
//        row.put("CurrChangePrc", 0);
//        row.put("CurrAmt", 0);
//        row.put("CurrChangeAmt", 0);
//        row.put("CurrTotqty", 0);
//        row.put("CurrChangeTotqty", 0);
//        row.put("CurrTotamt", 0);
//        row.put("CurrChangeTotamt", 0);
//        row.put("Pms3BoqMPhid", "");
//        row.put("ChangeD", 0);
//        row.put("PhidCbsNew_EXName", "");
//        row.put("PhidPc", "");
//        row.put("PhidLevel", "");
//        row.put("PhidLevel_EXName", "");
//        row.put("MCname", "");
//        row.put("MCode", "");
//        row.put("MPhid", "");
//        row.put("PhidQuota_EXCode", "");
//        row.put("PhidWbs", "");
//        row.put("PhidWbs_EXName", "");
//        row.put("CbsQdId", "");
//        row.put("BoqDIsRel", "");
        row.put("key", "");
        row.put("user_sl", Double.parseDouble(itemInfo.getZzsl())*0.01);
        row.put("user_jjsjk", itemInfo.getId());
        row.put("user_zhdjjs", itemInfo.getTaxRate());
        row.put("user_djhs", Double.parseDouble(itemInfo.getQuantity())/Double.parseDouble(mQty)*Double.parseDouble(itemInfo.getTaxRate()));
        row.put("user_hjhs", itemInfo.getTaxTotal());
        row.put("user_sjdj", itemInfo.getSjRate());
        row.put("user_sjhj", itemInfo.getSjTotal());
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        row=new EntityConverter().SetFieldMap(gridStr, row);
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装 其他项清单 参数boqmtreeDataDJ 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqmtreeDataQTChg(BOQOtherModel itemInfo, Long phid,Long parentPhid,
                                              List<Map<String, Object>> wbsPhids,List<Map<String, Object>> msunitPhids,
                                              BOQChangeMModel changeM) {
        if(changeM==null)
        {
            changeM=new BOQChangeMModel();
            changeM.setOriQty((double) 0);
            changeM.setCurrQty((double) 0);
            changeM.setOriPrc((double) 0);
            changeM.setCurrPrc((double) 0);
            changeM.setOriAmt((double) 0);
            changeM.setCurrAmt((double) 0);
            changeM.setOldPhid("0");
        }
        String wbsCode=itemInfo.getBidNodeID();
//        String wbsPhid=jdbcTemplate.queryForObject("select phid from bd_wbs  where pcid='"+pcPhid+"' and wbs_realcode='"+wbsCode+"'",String.class);
//        String wbsName=jdbcTemplate.queryForObject("select description from bd_wbs  where pcid='"+pcPhid+"' and wbs_realcode='"+wbsCode+"'",String.class);
//        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
        String wbsPhid="";
        String wbsName="";
        for(Map<String, Object> v : wbsPhids)
        {
            if(String.valueOf(v.get("WBS_REALCODE")).equals(wbsCode))
            {
                wbsPhid= String.valueOf(v.get("PHID"));
                wbsName= String.valueOf(v.get("DESCRIPTION"));
                break;
            }
        }
        String msunitPhid="";
        for(Map<String, Object> v : msunitPhids)
        {
            if(String.valueOf(v.get("MSNAME")).equals(wbsCode))
            {
                msunitPhid= String.valueOf(v.get("PHID"));
                break;
            }
        }
        String cntPhid=jdbcTemplate.queryForObject("select phid from pcm3_cnt_m where cnt_type='5' and bill_no='"+itemInfo.getContractCode()+"'",String.class);
        cntPhid=cntPhid==null?"":cntPhid;
        String insertDt=DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd HH:mm:ss");
        Map<String, Object> newRow = new HashMap<>();
        HashMap<String, Object> row = new HashMap<>();
        row.put("id", phid);
        row.put("PhId", phid);
        row.put("Pphid", parentPhid);
        row.put("NgInsertDt", insertDt);
        row.put("NgUpdateDt", insertDt);

        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());

        row.put("PhidWbs", wbsPhid);
        row.put("PhidWbs_EXName", wbsName);
        row.put("PhidMsunit", msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());

        row.put("MType", 3);

        row.put("Rate", 0);
        row.put("Qty", 1);
        row.put("Prc", itemInfo.getTotal());
        row.put("Amt", itemInfo.getTotal());

        row.put("OriQty", 1);
        row.put("OriQty", changeM.getOriQty());//原始工程量
        row.put("CurrQty", changeM.getCurrQty());//当前工程量
        row.put("CurrChangeQty", 0);//本次变更工程量
        row.put("OriPrc", changeM.getOriPrc());//
        row.put("CurrPrc", changeM.getCurrPrc());//
        row.put("CurrChangePrc", 0);//
        row.put("OriAmt", changeM.getOriAmt());//
        row.put("CurrAmt", changeM.getCurrAmt());//
        row.put("CurrChangeAmt", Double.parseDouble(itemInfo.getTotal())-changeM.getCurrAmt());//

        row.put("checked", "");

        row.put("Pms3BoqMPhid", changeM.getOldPhid());
        row.put("key", "");
        row.put("user_jjsjk", itemInfo.getId());
        row.put("user_djhs", itemInfo.getTotal());
        row.put("user_hjhs", itemInfo.getTotal());
        row.put("user_sjdj", itemInfo.getTotal());
        row.put("user_sjhj", itemInfo.getTotal());
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
            row.put("id", phid);
        }
        row=new EntityConverter().SetFieldMap(mStr, row);
        newRow.put("row", row);
        return newRow;
    }

    /**
     * 封装 其他项清单明细 参数boqdgridDataDJ 是否是新增判断key是否有值
     * @return
     */
    public Map<String, Object> boqdgridDataQTChg(BOQFeeModel itemInfo, Long phid,Long parentPhid,
                                              List<Map<String, Object>> wbsPhids,List<Map<String, Object>> msunitPhids,
                                              BOQChangeDModel changD,String mQty,List<Map<String, Object>> resBsPhids) {
        if(changD==null)
        {
            changD=new BOQChangeDModel();
            changD.setJjsjk("");
            changD.setOldPhid("");
            changD.setOriAmt((double) 0);
            changD.setOriPrc((double) 0);
            changD.setOriQty((double) 0);
            changD.setOriTotAmt((double) 0);
            changD.setOriTotQty((double) 0);
        }
        itemInfo.setUnit("m");
//        String msunitPhid=jdbcTemplate.queryForObject("select phid from msunit where msname='"+itemInfo.getUnit()+"'",String.class);
//        String cbsPhid=jdbcTemplate.queryForObject("select phid from bd_cbs where cbs_code='"+itemInfo.getCourseCode()+"' and pcid="+pcPhid,String.class);
//        String cbsName=jdbcTemplate.queryForObject("select cbs_name from bd_cbs where cbs_code='"+itemInfo.getCourseCode()+"' and pcid="+pcPhid,String.class);
        String cbsPhid="";
        String cbsName="";
        for(Map<String, Object> v : wbsPhids)
        {
            if(String.valueOf(v.get("CBS_CODE")).equals(itemInfo.getCourseCode()))
            {
                cbsPhid= String.valueOf(v.get("PHID"));
                cbsName= String.valueOf(v.get("CBS_NAME"));
                break;
            }
        }
        String msunitPhid="";
        for(Map<String, Object> v : msunitPhids)
        {
            if(String.valueOf(v.get("MSNAME")).equals(itemInfo.getUnit()))
            {
                msunitPhid= String.valueOf(v.get("PHID"));
                break;
            }
        }
        Map<String, Object> newRow = new HashMap<>();
        HashMap<String, Object> row = new HashMap<>();
        row.put("PhId", "");
        row.put("Pphid", parentPhid);

        row.put("IsCost", "1");
        row.put("PhidCbs", cbsPhid);
        row.put("PhidCbs_EXName", cbsName);

        row.put("Code", itemInfo.getCode());
        row.put("Cname", itemInfo.getName());
        //FeeType：1-劳务分包 2-材料费 3-周转材料费 4-机械费 8-专业分包
        //新中大   2			7		8			6			3
        String fType=itemInfo.getFeeType();//分包方式
        fType=fType.equals("1")?"2":fType.equals("2")?"7":fType.equals("3")?
                "8":fType.equals("4")?"6":fType.equals("8")?"3":"*";
        row.put("Ftype", fType);
        row.put("Spec", itemInfo.getSpec());
        row.put("PhidMsunit",msunitPhid);
        row.put("PhidMsunit_EXName", itemInfo.getUnit());

        row.put("Amt", Double.parseDouble(itemInfo.getQuantity())/Double.parseDouble(mQty)*Double.parseDouble(itemInfo.getNoTaxRate()));
        row.put("Prc", itemInfo.getNoTaxRate());

        row.put("Qty",Double.parseDouble(itemInfo.getQuantity())/Double.parseDouble(mQty));//单耗量=费用项总量/清单总量
        row.put("OriPrc",changD.getOriPrc());
        row.put("OriQty",changD.getOriQty());
        row.put("OriAmt",changD.getOriAmt());
        row.put("OriTotqty",changD.getOriTotQty());
        row.put("OriTotamt",changD.getOriTotAmt());
        row.put("Pms3BoqMPhid",changD.getOldPhid());
        row.put("MType", 3);
        row.put("key", "");
        row.put("user_sl", Double.parseDouble(itemInfo.getZzsl())*0.01);
        row.put("user_jjsjk", itemInfo.getId());
        row.put("user_zhdjjs", itemInfo.getTaxRate());
        row.put("user_djhs", Double.parseDouble(itemInfo.getQuantity())/Double.parseDouble(mQty)*Double.parseDouble(itemInfo.getTaxRate()));
        row.put("user_hjhs", itemInfo.getTaxTotal());
        row.put("user_sjdj", itemInfo.getSjRate());
        row.put("user_sjhj", itemInfo.getSjTotal());
        if(phid>0)
        {
            row.put("key", phid);
            row.put("PhId", phid);
        }
        row=new EntityConverter().SetFieldMap(gridStr, row);
        newRow.put("row", row);
        return newRow;
    }


}
