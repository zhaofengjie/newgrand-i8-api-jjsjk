package com.newgrand.controller.EDB;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.EDB.*;
import com.newgrand.config.IJdbcTemplate;
import com.newgrand.domain.DataInfo;
import com.newgrand.domain.EDB.*;
import com.newgrand.helper.I8Request;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WBS数据接收
 *
 * @Author ChenXiangLu
 * @Date 2020/11/25 15:49
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class WBSController {

    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected IJdbcTemplate jdbcTemplate;

    @Autowired
    private I8Request i8Request;

    ///需要确认是否存在重读推送数据的问题
    ///重复推送的话 子父集状态是什么样的
    //对方是不是每次新增状态都是2,是否存在对方新增后没推送然后修改了,然后状态不是新增状态
    @ApiOperation(value = "接收WBS数据", notes = "接收WBS数据", produces = "application/json")
    @RequestMapping(value = "/syncWbs", method = RequestMethod.POST)
    public EDBResultModel<String> syncWbs(@RequestBody WBSModel param) {
        log.info("接收WBS数据"+ JSONObject.toJSONString(param));
        System.out.println("接收WBS数据"+ JSONObject.toJSONString(param));
        var result = new EDBResultModel<String>();
        result.setCode("0");
        result.setMessage("WBS接收成功");
        var data=new ArrayList<EDBResultModel<String>>();
        try {
            var pcId = jdbcTemplate.queryForObject("select phid from project_table where bill_no='" + param.getCode() + "'", String.class);
            if(pcId==null)
            {
                result.setCode("1");
                result.setMessage("项目不存在:"+param.getCode());
                return result;
            }
            var level1List = param.getProjectDXInfos();
            for (WBSLevel1Model level1 : level1List) {
                DataInfo dataInfo = paramLevel(level1, pcId);
                if (dataInfo.getStatus().equals("1")) {
                    result.setCode("1");
                    result.setMessage(dataInfo.getErrorText());
                }
            }
        } catch (Exception e) {
            result.setCode("1");
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 编辑层级关系  注意层级间的phid值
     * @param model
     * @param pcId
     * @return
     */
    public DataInfo paramLevel(WBSLevel1Model model, String pcId){
        DataInfo rvInfo = new DataInfo();
        Long minId=-1L;//最小值,用于新增
        Long phid = -1L;//用于新增初始化本级暂存id
//        Long newPhid = -1L;//用于确定新增和修改的最终数据
        Long level2Phid = 0L;//初始化本级暂存id
//        Long newLevel2Phid = 0L;//用于确定新增和修改的最终数据
        Long level2ParentPhid = 0L;//初始化末级暂存id
        Long level3Phid = 0L;//初始化本级暂存id
        Long level3ParentPhid = 0L;//初始化末级暂存id
        ArrayList<Map<String, Object>> allRow = new ArrayList<>();
        var level2List= model.getProjectDWInfos();
        var isLeaf=level2List==null||level2List.size()==0?true:false;

        phid =jdbcTemplate.queryForObject("select phid  from bd_wbs where wbs_realcode='"+model.getId()+"' and  pcid=" + pcId, Long.class);
        if(phid==null)
            phid=minId;
        Map<String, Object> newRow1 = paramProcess(model.getId(), model.getName(), pcId, phid, 0L, "1", isLeaf);
        allRow.add(newRow1);

        level2ParentPhid=phid;//二级时需要把一级的phid赋值给父级
        for (WBSLevel2Model level2:level2List)
        {
            var level3List= level2.getBqItemFBInfos();
            if(level3List==null)
                level3List=level2.getMeasureFBInfos();
            else
                level3List.addAll(level2.getMeasureFBInfos());
            isLeaf=level3List==null||level3List.size()==0?true:false;

            level2Phid = jdbcTemplate.queryForObject("select phid  from bd_wbs where wbs_realcode='" + level2.getId() + "' and pcid=" + pcId, Long.class);
            if(level2Phid==null) {
                minId--;
                level2Phid = minId;
            }
            Map<String, Object> newRow2 = paramProcess(level2.getId(),level2.getName(),pcId,level2Phid,level2ParentPhid,"2",isLeaf);
            allRow.add(newRow2);

            level3ParentPhid=level2Phid;//二级时需要把一级的phid赋值给父级
            for (WBSLevel3Model level3:level3List)
            {
                level3Phid = jdbcTemplate.queryForObject("select phid  from bd_wbs where wbs_realcode='" + level3.getId() + "' and pcid=" + pcId, Long.class);
                if(level3Phid==null)
                {
                    minId--;
                    level3Phid=minId;
                }
                isLeaf=true;
                Map<String, Object> newRow3 = paramProcess(level3.getId(), level3.getName(), pcId, level3Phid, level3ParentPhid, "3", isLeaf);
                allRow.add(newRow3);
            }
        }
//        ((Map<String,Object>)allRow.get(0).get("row")).put("isFirst", true);//第一个
//        ((Map<String,Object>)allRow.get(allRow.size() - 1)).put("isLast", true);//倒数第一个
        for(Map<String, Object> v :allRow)
        {
            var s=((Map<String, Object>)v.get("row")).get("PhId")==null;
            var ss=((Map<String, Object>)v.get("row")).get("PhId").toString().equals("");
            var ssss=Long.parseLong(((Map<String, Object>)v.get("row")).get("PhId").toString())<0;
        }
        ArrayList<Map<String, Object>> newRow= (ArrayList<Map<String, Object>>) allRow.stream().filter(f->((Map<String, Object>)f.get("row")).get("PhId")==null||((Map<String, Object>)f.get("row")).get("PhId").toString().equals("")|| Long.parseLong(((Map<String, Object>)f.get("row")).get("PhId").toString())<0).collect(Collectors.toList());
        ArrayList<Map<String, Object>> modifyRow= (ArrayList<Map<String, Object>>) allRow.stream().filter(f->((Map<String, Object>)f.get("row")).get("PhId")!=null&&!((Map<String, Object>)f.get("row")).get("PhId").toString().equals("")&& Long.parseLong(((Map<String, Object>)f.get("row")).get("PhId").toString())>0).collect(Collectors.toList());
        StringBuilder saveData=new StringBuilder();
        saveData.append("{\"table\":{\"key\":\"phid\"");
        if(newRow.size()>0)
        {
            saveData.append(",\"newRow\":"+JSONObject.toJSONString(newRow));
        }
        if(modifyRow.size()>0)
        {
            saveData.append(",\"modifiedRow\":"+JSONObject.toJSONString(modifyRow));
        }
        saveData.append("},\"isChanged\":true}");
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("savedata", saveData.toString()));
        urlParameters.add(new BasicNameValuePair("wbscbsdata", "{\"table\":{\"key\":\"PhId\"}}"));
        try {
            if(allRow.size()==0)
            {
                rvInfo.setStatus("0");
                rvInfo.setErrorText("无新增数据");
            }
            else {
                String i8rv = i8Request.PostFormSync("/PMS/BasicData/WBS/WbsSave", urlParameters);
                JSONObject i8rvJson = JSON.parseObject(i8rv);
                if (i8rvJson != null && i8rvJson.getString("Status").toLowerCase().equals("success")) {
                    rvInfo.setStatus("0");
                    rvInfo.setErrorText("新增记录成功");
                } else {
                    rvInfo.setStatus("1");
                    rvInfo.setErrorText(i8rv);
                }
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
     * 封装参数
     * @param wbsCode wbs編碼
     * @param wbsName wbs名稱
     * @param pcId 項目phid
     * @param phid 本级phid
     * @param parentPhid 父级phid
     * @param depth 深度
     * @param leaf 是否是叶子节点
     * @return
     */
    public Map<String, Object> paramProcess(String wbsCode,String wbsName, String pcId,Long phid,Long parentPhid,
                                            String depth,boolean leaf) {
        Map<String, Object> newRow = new HashMap<>();
        Map<String, Object> row = new HashMap<>();
        row.put("OrgPrc", 0);
        row.put("WbsRealcode", wbsCode);
        row.put("WbsName", wbsName);
        row.put("PcId", pcId);
        row.put("PhId", phid);
        row.put("ParentPhId", parentPhid);
        row.put("depth", depth);
        if (depth.equals("1")) {
            row.put("parentId", "root");
        }
        row.put("leaf", false);
        row.put("PhIdProduct", "");
        row.put("PhIdProduct_EXName", "");
        row.put("GclTotal", 0);
        row.put("Prc", 0);
        row.put("GclPrc", 0);
        row.put("Msunit", "");
        row.put("BeginDate", null);
        row.put("EndDate", null);
        row.put("ActualBeginDate", null);
        row.put("ActualEndDate", null);
        row.put("Forbid", "");
        row.put("WbsHelpName", "");
        row.put("FgPclass", "");
        row.put("Dm", "");
        row.put("Remarks", "");
        row.put("WbsHelp", "");
        row.put("MsunitName", "");
        row.put("FgPclassCode", "");
        row.put("FgPclassName", "");
        row.put("text", "");
        row.put("WbsParent", "");
        row.put("RelWbsCode", "");
        row.put("WbsPcParent", "");
        row.put("WbsParentName", "");
        row.put("PhidWbsTask", "0");
        row.put("PhidWbsTask_EXName", "");
        row.put("IsLock", 0);
        row.put("Profit", 1);
        row.put("Cost", 1);
        row.put("Schedule", 1);
        row.put("NgPhIdBP_Name", "");
        row.put("GbCode", "");
        row.put("GbCodeName", "");
        row.put("NgShareDataSearchKey", "");
        row.put("NgSuperiorVisibleSearchKey", "");
        row.put("NgOrgIdOriginal", "");
        row.put("NgPhIdOriginal", "");
        row.put("NgPhIdUIScheme", "");
        row.put("NgPhIdOrg", "");
        row.put("NgPhIdBP", "");
        row.put("NgPhIdCU", "");
        row.put("NgShareSign", 0);
        row.put("NgPhIdOrg_Name", "");
        row.put("NgBpPhids", "");
        row.put("NgBpNames", "");
        row.put("NgRecordVer", 0);
        row.put("index", 0);
        row.put("expanded", true);
        row.put("expandable", true);
        row.put("checked", null);
        row.put("cls", "");
        row.put("iconCls", "");
        row.put("icon", "");
        row.put("root", false);
        row.put("isLast", false);
        row.put("isFirst", false);
        row.put("allowDrop", true);
        row.put("allowDrag", true);
        row.put("loaded", false);
        row.put("loading", false);
        row.put("href", "");
        row.put("hrefTarget", "");
        row.put("qtip", "");
        row.put("qtitle", "");
        row.put("qshowDelay", 0);
        row.put("children", null);
        row.put("key", "");
        if(phid>0)
        {
            row.put("key", phid);
            var ngRecordVer=jdbcTemplate.queryForObject("select ng_record_ver  from bd_wbs where wbs_realcode='" + wbsCode + "' and pcid=" + pcId, String.class);
            row.put("NgRecordVer", ngRecordVer);
        }
        newRow.put("row", row);
        return newRow;
    }
}
