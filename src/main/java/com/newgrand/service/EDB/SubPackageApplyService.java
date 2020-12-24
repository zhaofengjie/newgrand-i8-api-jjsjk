package com.newgrand.service.EDB;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.domain.EDB.SubPackageApplyDetailFeeModel;
import com.newgrand.domain.EDB.SubPackageApplyDetailModel;
import com.newgrand.domain.EDB.SubPackageApplyModel;
import com.newgrand.config.IJdbcTemplate;
import com.newgrand.domain.DataInfo;
import com.newgrand.helper.DateTranslate;
import com.newgrand.helper.I8Request;
import com.newgrand.helper.SnowflakeIdWorker;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分包申请
 * @Author ChenXiangLu
 * @Date 2020/12/14 20:50
 * @Version 1.0
 */
@Service
@Transactional
public class SubPackageApplyService {

    /**
     * 模拟登录的用户
     */
    @Value("${i8.user}")
    private String i8user;
    @Autowired
    @Resource(name = "jdbcTemplateOrcle")
    protected IJdbcTemplate jdbcTemplate;

    @Autowired
    private I8Request i8Request;
    /**
     * 默认组织
     */
    @Value("${i8.orgId}")
    private String orgId;

    public ArrayList<DataInfo> SyncSubPackageApply(SubPackageApplyModel param, String pcPhid)
    {
        ArrayList<DataInfo> rvInfo = new ArrayList<>();

        //查询所有分包方式
        List<Map<String, Object>> billTypePhids=jdbcTemplate.queryForList("select phid,c_no from fg_simple_data where c_type='YCIH_fbfs'");
        //查询所有单位
        ArrayList<String> msunits=new ArrayList<>();
        msunits.add("m");
        param.getDetais().stream().map(m->m.getFeeItemInfos())
                .forEach(f->f.forEach(ff->
                {
                    if(!msunits.contains(ff.getUnit()))
                        msunits.add(ff.getUnit());
                }));
        List<Map<String, Object>> msunitPhids=jdbcTemplate.queryForList("select phid,msname from msunit  where msname in ('"+ StringUtils.join(msunits,"','")+"')");
        //判断成本科目是否存在
        ArrayList<String> cbsCodes=new ArrayList<>();
        param.getDetais().stream().map(m->m.getFeeItemInfos())
                .forEach(f->f.forEach(ff->
                {
                    ff.setCourseCode("05.05");//测试使用,测试时对方你未维护该字段,后期取消该默认值
                    if(!cbsCodes.contains(ff.getCourseCode()))
                        cbsCodes.add(ff.getCourseCode());
                }));
        List<Map<String, Object>> cbsPhids=jdbcTemplate.queryForList("select phid,cbs_code from bd_cbs  where cbs_code in ('"+ StringUtils.join(cbsCodes,"','")+"')");

        String fillPsnPhid=jdbcTemplate.queryForObject("select phid from hr_epm_main where cno='"+param.getUserCode()+"'",String.class);
        String creatorPhid=jdbcTemplate.queryForObject("select phid from fg3_user where userno='"+i8user+"'",String.class);
        for (SubPackageApplyDetailModel v:param.getDetais()) {

            DataInfo tempRes=syncDetail(v,msunitPhids,cbsPhids,pcPhid,fillPsnPhid,creatorPhid,param.getCode(),billTypePhids);
            rvInfo.add(tempRes);
        }
        return  rvInfo;
    }


    private DataInfo syncDetail(SubPackageApplyDetailModel detail,List<Map<String,Object>> unitPhids,
                                List<Map<String, Object>> cbsPhids,String pcPhid,String fillPsnPhid,String creatorPhid,String pcNo,List<Map<String,Object>> billTypePhids)
    {
        DataInfo result=new DataInfo();
        result.setCode(detail.getId());
        //判断是否已引用,已引用不进行操作
        var isUsed=jdbcTemplate.queryForObject("select u_sfyy from p_form_fbgl_fbsq_m where bill_no='"+detail.getId()+"'",String.class);
        if(isUsed!=null&&isUsed.equals("1"))
        {
            result.setStatus("1");
            result.setErrorText("单据已引用,不允许修改");
            return result;
        }
        String u_fbfs="";
        //判断承包方式是否存在
        for(Map<String, Object> b:billTypePhids)
        {
            if(b.get("C_NO").equals(detail.getFeeType()))
            {
                u_fbfs=String.valueOf(b.get("PHID"));
                break;
            }
        }
        if(u_fbfs.equals(""))
        {
            result.setStatus("1");
            result.setErrorText("分包方式不存在:"+JSONObject.toJSONString(detail.getFeeType()));
            return result;
        }

        String billDt= DateTranslate.getDateAsString(LocalDateTime.now(),"yyyy-MM-dd");
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        boolean isExistM=true;
        var phidM=jdbcTemplate.queryForObject("select phid from p_form_fbgl_fbsq_m  where bill_no='"+detail.getId()+"'",String.class);
        if(phidM==null)
        {
            phidM= String.valueOf(idWorker.nextId());
            isExistM=false;
        }
        StringBuilder sql=new StringBuilder("begin ");
        String tempM="";
        if(isExistM)
        {
            tempM=String.format("update p_form_fbgl_fbsq_m set title='%s',pc=%s,fillpsn=%s,u_fbfs=%s where phid=%s;",
                    detail.getName(),pcPhid,fillPsnPhid,u_fbfs,phidM);
        }
        else
        {
            tempM=String.format("insert into p_form_fbgl_fbsq_m" +
                            "(phid,title,bill_no,pc,ocode,fillpsn,creator,u_xmbm,u_fbfs,bill_dt,u_sfyy) " +
                            "values(%s,'%s','%s',%s,%s,%s,%s,'%s',%s,to_date('%s','yyyy-MM-dd'),'%s');",
                    phidM,detail.getName(),detail.getId(),pcPhid,orgId,fillPsnPhid,creatorPhid,pcNo,u_fbfs,billDt,2);
        }
        sql.append(tempM);
        for (SubPackageApplyDetailFeeModel v :detail.getFeeItemInfos())
        {
            String tempD;
            var isExistD=true;
            var phidD=jdbcTemplate.queryForObject("select phid from p_form_fbgl_fbsq_d where u_jjsjkid='"+v.getId()+"'",String.class);
            if(phidD==null)
            {
                phidD= String.valueOf(idWorker.nextId());
                isExistD=false;
            }
            String u_fbfyxbm=v.getCode();
            String u_fbfyxmc=v.getName();
            String u_fbfyxtz=v.getSpec();
            String u_jldw="";
            v.setUnit("t");
            for(Map<String, Object> u:unitPhids)
            {
                if(u.get("MSNAME").equals(v.getUnit())||u.get("MSNAME").equals("m"))
                {
                    u_jldw=String.valueOf(u.get("PHID"));
                    break;
                }
            }
            if(u_jldw.equals(""))
            {

                result.setStatus("1");
                result.setErrorText("单位不存在:"+JSONObject.toJSONString(v.getUnit()));
                return result;
            }
            String u_sl=v.getQuantity();
            String u_zhwsdj=v.getNoTaxRate();
            String u_qzrgfwsdj=v.getLaborNoTaxRate();
            String u_zhhsdj=v.getTaxRate();
            String u_qzrgfhsdj=v.getLaborTaxRate();
            String u_je_ws=v.getNoTaxTotal();
            String u_qzrgfje_ws=v.getLaborNoTaxTotal();
            String u_je=v.getTaxTotal();
            String u_qzrgfje=v.getLaborTaxTotal();
            String u_se=v.getSjTotal();
            String u_jlgz=v.getCalcRule();
            String u_gznr=v.getWorkScope();
            String u_clgyjpp=v.getBrand();
            String u_cbkm="";
            for(Map<String, Object> c:cbsPhids)
            {
                if(c.get("CBS_CODE").equals(v.getCourseCode()))
                {
                    u_cbkm=String.valueOf(c.get("PHID"));
                    break;
                }
            }
            if(u_cbkm.equals(""))
            {
                result.setStatus("1");
                result.setErrorText("CBS不存在:"+JSONObject.toJSONString(v.getCourseCode()));
                return result;
            }
            String u_shuilv=v.getSjRate();
            if(isExistD)
            {
                tempD=String.format("update p_form_fbgl_fbsq_d" +
                                "set u_fbfyxbm='%s',u_fbfyxmc='%s',u_fbfyxtz='%s',u_jldw=%s,u_sl=%s," +
                                "u_zhwsdj=%s,u_qzrgfwsdj=%s,u_zhhsdj=%s,u_qzrgfhsdj=%s,u_je_ws=%s,u_qzrgfje_ws=%s,u_je=%s," +
                                "u_qzrgfje=%s,u_se=%s,u_jlgz='%s',u_gznr='%s',u_clgyjpp='%s',u_cbkm=%s,u_shuilv=%s where phid=%s;",
                        u_fbfyxbm,u_fbfyxmc,u_fbfyxtz,u_jldw,u_sl,
                        u_zhwsdj,u_qzrgfwsdj,u_zhhsdj,u_qzrgfhsdj,u_je_ws,u_qzrgfje_ws,u_je,
                        u_qzrgfje,u_se,u_jlgz,u_gznr,u_clgyjpp,u_cbkm,u_shuilv,phidD);
            }
            else
            {
                tempD=String.format("insert into p_form_fbgl_fbsq_d" +
                                "(phid,m_code,u_fbfyxbm,u_fbfyxmc,u_fbfyxtz,u_jldw,u_sl," +
                                "u_zhwsdj,u_qzrgfwsdj,u_zhhsdj,u_qzrgfhsdj,u_je_ws,u_qzrgfje_ws,u_je," +
                                "u_qzrgfje,u_se,u_jlgz,u_gznr,u_clgyjpp,u_cbkm,u_shuilv   ) values" +
                                "(%s,%s,'%s','%s','%s',%s,%s," +
                                "%s,%s,%s,%s,%s,%s,%s," +
                                "%s,%s,'%s','%s','%s',%s,%s);",
                        phidD,phidM,u_fbfyxbm,u_fbfyxmc,u_fbfyxtz,u_jldw,u_sl,
                        u_zhwsdj,u_qzrgfwsdj,u_zhhsdj,u_qzrgfhsdj,u_je_ws,u_qzrgfje_ws,u_je,
                        u_qzrgfje,u_se,u_jlgz,u_gznr,u_clgyjpp,u_cbkm,u_shuilv);

            }
            sql.append(tempD);
        }
        sql.append(" end;");
        try {
            jdbcTemplate.execute(sql.toString());
            result.setStatus("0");
            result.setErrorText("数据同步成功");
        }catch (Exception e)
        {
            result.setStatus("1");
            result.setErrorText(e.getCause()+e.getMessage());
        }
        return result;

    }
}
