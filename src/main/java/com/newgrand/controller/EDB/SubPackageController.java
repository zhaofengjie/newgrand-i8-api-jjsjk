package com.newgrand.controller.EDB;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.EDB.EDBResultModel;
import com.newgrand.domain.EDB.SubPackageApplyModel;
import com.newgrand.domain.EDB.SubPackagePlanModel;
import com.newgrand.config.IJdbcTemplate;
import com.newgrand.domain.DataInfo;
import com.newgrand.service.EDB.SubPackageApplyService;
import com.newgrand.service.EDB.SubPackagePlanChangeService;
import com.newgrand.service.EDB.SubPackagePlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 分包策划
 * @Author ChenXiangLu
 * @Date 2020/11/25 17:35
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class SubPackageController {

    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected IJdbcTemplate jdbcTemplate;
    @Autowired
    private SubPackagePlanChangeService subPackagePlanChangeService;
    @Autowired
    private SubPackagePlanService subPackagePlanService;
    @Autowired
    private SubPackageApplyService subPackageApplyService;
    ///分包计划审核标志  wf_flg=1表示审核中;
    // 不走工作流的话 chk_flg=1表示已审核


    @ApiOperation(value="接收分包计划数据", notes="接收分包计划数据", produces="application/json")
    @RequestMapping(value = "/syncSubPackagePlan",method = RequestMethod.POST)
    public EDBResultModel<String> syncSubPackagePlan(@RequestBody SubPackagePlanModel param)
    {
        log.info("接收分包计划数据"+ JSONObject.toJSONString(param));
        var result=new EDBResultModel<String>();
        try {
            DataInfo dataInfo=new DataInfo();
            String pcPhid=jdbcTemplate.queryForObject("select phid from project_table where pc_no='"+param.getCode()+"'",String.class);
//            String wfFlg="";
//            String chkFlg="";
            String wfFlg=jdbcTemplate.queryForObject("select wf_flg from pms3_subc_m where bill_type='1' and  phid_pc="+pcPhid,String.class);
            String chkFlg=jdbcTemplate.queryForObject("select chk_flg from pms3_subc_m where bill_type='1' and  phid_pc="+pcPhid,String.class);
            String jbr=jdbcTemplate.queryForObject("select phid from hr_epm_main where cno='"+param.getUserCode()+"'",String.class);
            if(pcPhid==null)
            {
                result.setData("1");
                result.setCode("400");
                result.setMessage("项目不存在:"+param.getCode());
                return result;
            }
            if(jbr==null)
            {
                result.setData("1");
                result.setCode("400");
                result.setMessage("经办人不存在:"+param.getUserCode());
                return result;
            }
            if(chkFlg==null||chkFlg.equals("")||chkFlg.equals("0"))//表示未审核
            {
                if(wfFlg==null||wfFlg.equals(""))//表示未发起工作流
                {
                    dataInfo = subPackagePlanService.saveSubPackagePlan(param);
                }
                else if(wfFlg.equals("1"))//表示已发起工作流
                {
                    dataInfo.setCode("1");
                    dataInfo.setErrorText("已发起工作流,不进行更新");
                    dataInfo.setStatus("400");
                }
            }
            else//已审核后需要进行变更
            {
                String wfFlgChange=jdbcTemplate.queryForObject("select wf_flg from pms3_subc_change_m where bill_type='1' and wf_flg='1' and  phid_pc="+pcPhid,String.class);
                String chkFlgChange=jdbcTemplate.queryForObject("select chk_flg from pms3_subc_change_m where bill_type='1' and chk_flg!='1' and  phid_pc="+pcPhid,String.class);
                if(chkFlgChange==null||chkFlgChange.equals("")||chkFlgChange.equals("0"))//表示未审核
                {
                    if(wfFlgChange==null||wfFlgChange.equals(""))//表示未发起工作流
                    {
                        dataInfo = subPackagePlanChangeService.saveSubPackagePlanChange(param);
                    }
                    else if(wfFlgChange.equals("1"))//表示已发起工作流
                    {
                        dataInfo.setCode("1");
                        dataInfo.setErrorText("已发起工作流,不进行更新");
                        dataInfo.setStatus("400");
                    }
                }
            }
            result.setData(dataInfo.getCode());
            result.setCode(dataInfo.getStatus());
            result.setMessage(dataInfo.getErrorText());
        } catch (Exception e) {
            result.setCode("1");
            result.setMessage(e.getCause().toString());
        }
        return result;
    }


    @ApiOperation(value="接收分包申请数据", notes="接收分包申请数据", produces="application/json")
    @RequestMapping(value = "/syncSubPackageApply",method = RequestMethod.POST)
    public EDBResultModel<ArrayList<DataInfo>> syncSubPackageApply(@RequestBody SubPackageApplyModel param)
    {
        log.info("接收分包申请数据"+ JSONObject.toJSONString(param));
        var result=new EDBResultModel<ArrayList<DataInfo>>();
        result.setData(new ArrayList<>());
        try {
            ArrayList<DataInfo> dataInfo=new ArrayList<DataInfo>();
            String pcPhid=jdbcTemplate.queryForObject("select phid from project_table where pc_no='"+param.getCode()+"'",String.class);
            String jbr=jdbcTemplate.queryForObject("select phid from hr_epm_main where cno='"+param.getUserCode()+"'",String.class);
            if(pcPhid==null)
            {
                result.setCode("1");
                result.setMessage("项目不存在:"+param.getCode());
                return result;
            }
            if(jbr==null)
            {
                result.setCode("1");
                result.setMessage("经办人不存在:"+param.getUserCode());
                return result;
            }

            dataInfo = subPackageApplyService.SyncSubPackageApply(param,pcPhid);

            result.setData(dataInfo);
            result.setCode("0");
            result.setMessage("接口数据接受结果看明细");
        } catch (Exception e) {
            result.setCode("1");
            result.setMessage(e.getCause().toString());
        }
        return result;
    }


}
