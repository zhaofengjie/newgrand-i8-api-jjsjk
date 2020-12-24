package com.newgrand.secdev.service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.domain.DataInfo;
import com.newgrand.secdev.domain.WebRv;
import com.newgrand.secdev.helper.EntityConverter;
import com.newgrand.secdev.helper.I8Request;
import com.newgrand.secdev.helper.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class MdmService extends ServiceBase {
    @Autowired
    private I8Request i8Request;
    @Autowired
    private EntityConverter entityConverter;

    private static final String dmMainBase  = "{\"form\":{\"key\":\"PhId\",\"newRow\":{\"PcNo\":\"085450\",\"ProjectName\":\"测试\",\"Ab\":\"\",\"Szfx\":\"1\",\"PhIdType\":\"505201016000002\",\"Stat\":\"sts\",\"RiskFlag\":\"\",\"StartDate\":\"2020-10-19\",\"EndDate\":\"2020-10-22\",\"FactStartDt\":\"\",\"FactEndDt\":\"\",\"CatPhId\":\"105200927000002\",\"ProjectManager\":\"\",\"LimitTime\":4,\"FactTime\":0,\"ApproxContractFc\":0,\"PhIdFiOcode\":\"\",\"ProjectOrg\":\"\",\"ManageMode\":\"\",\"PhIdCompany\":\"\",\"PhIdSgOrg\":\"\",\"VirtualFlg\":\"4\",\"GroupShare\":\"0\",\"PhidBuildType\":\"\",\"Descript\":\"\",\"PhIdScheme\":\"414201015000003\",\"NgRecordVer\":\"\",\"user_xmjl\":\"\",\"Pc\":\"\",\"DutyOfficer\":\"\",\"ChkFlg\":\"0\",\"PhIdApp\":\"\",\"WfFlg\":\"0\",\"DaFlg\":\"0\",\"BillFlg\":\"1\",\"PhIdOri\":0,\"PhId\":\"0\",\"IsSynchronous\":0,\"Guid\":\"\",\"ImpInfo\":\"\",\"PhidSourceMId\":\"\",\"ItemResource\":\"\",\"Py\":\"CS\",\"PointId\":\"\",\"Creator\":\"\",\"PhidTask\":\"\",\"OrgByProject\":\"\",\"AsrFlg\":\"\",\"CountryId\":\"1\",\"ProvinceId\":0,\"CityId\":0,\"RegionId\":0,\"ProjectAddress\":\"\",\"Longitude\":0,\"Latitude\":0,\"JobTax\":\"\",\"JobPhone\":\"\",\"BuildingArea\":0,\"Msunit\":\"\",\"IsQuality\":\"\",\"IsSafety\":\"\",\"ProjectParentId\":\"\",\"CntNo\":\"\",\"ContractType\":\"\",\"ContractorDate\":\"\",\"CurrType\":\"1\",\"ExchRate\":1,\"CntAmtFc\":0,\"CntAmt\":0,\"IsClass\":\"\",\"PcClass\":\"\",\"BuildIsclass\":\"\",\"BuildClass\":\"\",\"ConRecorde\":\"\",\"WorkRecorde\":\"\",\"Deadline\":\"\",\"GSituation\":\"\",\"ImposeType\":\"2\",\"ImposeArea\":\"1\",\"PreImposeRate\":0,\"PermitDtB\":\"\",\"PhIdRatepayOcode\":\"\",\"PhIdTaxOcode\":\"\",\"DecOrg\":\"\",\"PhIdTaxCenter\":\"\",\"Bank\":\"\",\"Bankaccount\":\"\",\"IsWjz\":\"\",\"InvType\":\"\",\"TaxRate\":0.03,\"LbProjseries\":\"\",\"Tbmodel\":\"\",\"IsPcCostkz\":\"1\",\"UseDefaultPrc\":\"\",\"key\":\"0\"}}}";
    private static  final String dmCorrect="{\"table\":{\"key\":\"PhId\",\"newRow\":[{\"row\":{\"PhId\":\"\",\"Code\":\"01\",\"Pc\":\"\",\"TypeCode\":1,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"11\",\"PhidBsYs_EXName\":\"合同收入\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"02\",\"Pc\":\"\",\"TypeCode\":2,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"12\",\"PhidBsYs_EXName\":\"目标成本\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"03\",\"Pc\":\"\",\"TypeCode\":3,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"13\",\"PhidBsYs_EXName\":\"责任成本\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"04\",\"Pc\":\"\",\"TypeCode\":4,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"14\",\"PhidBsYs_EXName\":\"计划成本\",\"PhidBsYs_EXName_Flag\":\"1\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"05\",\"Pc\":\"\",\"TypeCode\":5,\"IsYsbreak\":\"1\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"413201019000001\",\"PhidBsYs_EXName\":\"X分解\",\"PhidBsYs_EXName_Flag\":\"\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"06\",\"Pc\":\"\",\"TypeCode\":6,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"413201019000002\",\"PhidBsYs_EXName\":\"成本预算\",\"PhidBsYs_EXName_Flag\":\"\",\"key\":null}},{\"row\":{\"PhId\":\"\",\"Code\":\"07\",\"Pc\":\"\",\"TypeCode\":7,\"IsYsbreak\":\"\",\"NgRecordVer\":\"\",\"PcId\":\"\",\"PhidBsYs\":\"413201019000003\",\"PhidBsYs_EXName\":\"收入预算\",\"PhidBsYs_EXName_Flag\":\"\",\"key\":null}}]},\"isChanged\":true}";

    /*
     * 功能描述: 保存项目信息
     * @Param: [pro]
     * @Return: java.lang.String
     * @Author: xienb
     * @Date: 2020/10/21
     */
    public DataInfo SaveProject(JSONObject pro) throws IOException {
        DataInfo rvInfo =new DataInfo();
        rvInfo.setUuid(pro.getString("UUID"));
        rvInfo.setCode(pro.getString("DESC1"));
        rvInfo.setVersion("1");
        try {
            String PcNo = pro.getString("DESC1");
            Object[] params = new Object[] { PcNo };
            Integer count = jdbcTemplate.queryForObject("select count(*) from project_table where pc_no=:PcNo",params, Integer.class);
            if(count.equals(0)){
                //新增
                List<NameValuePair> urlParameters=new ArrayList<>();
                urlParameters.add(new BasicNameValuePair("dmEmployee", "{\"table\":{\"key\":\"PhId\"}}"));
                urlParameters.add(new BasicNameValuePair("dmCompany", "{\"table\":{\"key\":\"PhId\"}}"));
                urlParameters.add(new BasicNameValuePair("dmWareHouse", "{\"table\":{\"key\":\"PhId\"}}"));
                urlParameters.add(new BasicNameValuePair("dmDeliverName", "{\"table\":{\"key\":\"PhId\"}}"));
                urlParameters.add(new BasicNameValuePair("dmPcmap", "{\"table\":{\"key\":\"PhId\"}}"));
                urlParameters.add(new BasicNameValuePair("bustype", "ProjectTable"));
                urlParameters.add(new BasicNameValuePair("isContinue", "false"));
                urlParameters.add(new BasicNameValuePair("attchmentGuid", "0"));
                urlParameters.add(new BasicNameValuePair("dmCorrect", dmCorrect));
                HashMap<String,Object> map=new HashMap<String,Object>();
                map.put("PcNo",pro.getString("DESC1"));//项目编码
                map.put("ProjectName",pro.getString("DESC3"));//项目名称
                String DESC13=pro.getString("DESC13");//项目类型
                String PhIdType = GetPhidByCode("wbs_type","type_no",DESC13);
                if(!StringUtils.isEmpty(PhIdType)) map.put("PhIdType",PhIdType);
                String DESC66 = pro.getString("DESC66");//项目状态,后续沟通后调整
                map.put("Stat",GetXmzt(DESC66));
                map.put("StartDate",pro.getString("DESC77"));//计划开工日期
                map.put("EndDate",pro.getString("DESC78"));//计划竣工日期
                String DESC69 = pro.getString("DESC69");//管理组织
                String CatPhId = GetPhidByCode("fg_orglist","ocode",DESC69);
                if(!StringUtils.isEmpty(CatPhId))map.put("CatPhId",CatPhId);
                urlParameters.add(new BasicNameValuePair("dmMain", entityConverter.SetField(dmMainBase,map)));
                String i8rv = i8Request.PostFormSync("/PMS/PC/ProjectTable/save",urlParameters);
                JSONObject i8rvJson = JSON.parseObject(i8rv);
                //
                if(i8rvJson!=null&&i8rvJson.getString("Status").toLowerCase().equals("success")){
                    rvInfo.setStatus("0");
                    rvInfo.setErrorText("新增记录成功");
                }
                else {
                    rvInfo.setStatus("1");
                    rvInfo.setErrorText(i8rv);
                }
            }
            else {
                //更新
                String pc_no = PcNo;
                String project_name = pro.getString("DESC3");
                String DESC13=pro.getString("DESC13");//项目类型
                String phid_type = GetPhidByCode("wbs_type","type_no",DESC13);
                String DESC66 = pro.getString("DESC66");//项目状态,后续沟通后调整
                String stat = GetXmzt(DESC66);
                String start_date = pro.getString("DESC77");
                String end_date = pro.getString("DESC78");
                String DESC69 = pro.getString("DESC69");//管理组织
                String cat_phid = GetPhidByCode("fg_orglist","ocode",DESC69);
                int rows = jdbcTemplate.update("update project_table set project_name=:project_name,phid_type=:phid_type,stat=:stat,start_date=:start_date,end_date=:end_date,cat_phid=:cat_phid where pc_no=:pc_no"
                        ,new Object[]{pc_no,project_name,phid_type,stat,start_date,end_date,cat_phid});
                if(rows>0){
                    rvInfo.setStatus("0");
                    rvInfo.setErrorText("更新记录成功");
                }
                else {
                    rvInfo.setStatus("1");
                    rvInfo.setErrorText("更新记录失败");
                }
            }
            return rvInfo;
        }
        catch (Exception ex){
            rvInfo.setStatus("1");
            rvInfo.setErrorText(ex.getMessage());
            return rvInfo;
        }
    }

    /*
     * 功能描述: 保存客商信息
     * @Param: [pro]
     * @Return: java.lang.String
     * @Author: xienb
     * @Date: 2020/10/21
     */
    public DataInfo SaveEnterprise(JSONObject pro) {
        DataInfo rvInfo =new DataInfo();
        rvInfo.setUuid(pro.getString("UUID"));
        rvInfo.setCode(pro.getString("DESC1"));
        rvInfo.setVersion("1");
        try {
            String compno = pro.getString("DESC1");
            Object[] params = new Object[] { compno };
            Integer count = jdbcTemplate.queryForObject("select count(*) from fg3_enterprise where compno=:compno",params, Integer.class);
            //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
            Date now = new Date();
            if(count.equals(0)){
                String sqlInsert="insert into fg3_enterprise (" +
                        "phid," +
                        "fromtype," +
                        "fromorg_id," +
                        "compno," +
                        "compname," +
                        "simpname," +
                        "oldname," +
                        "person_flg," +
                        "address," +
                        "enternature_id," +
                        "unisocialcredit," +
                        "taxno," +
                        "taxaddress," +
                        "taxtelephone," +
                        "accstop," +
                        "relorg_id," +
                        "nation_id," +
                        "ng_insert_dt," +
                        "ng_update_dt," +
                        "ng_record_ver," +
                        "ng_sv_search_key," +
                        "ng_sd_search_key) " +
                        " values(" +
                        ":phid," +
                        ":fromtype," +
                        ":fromorg_id," +
                        ":compno," +
                        ":compname," +
                        ":simpname," +
                        ":oldname," +
                        ":person_flg," +
                        ":address," +
                        ":enternature_id," +
                        ":unisocialcredit," +
                        ":taxno," +
                        ":taxaddress," +
                        ":taxtelephone," +
                        ":accstop," +
                        ":relorg_id," +
                        ":nation_id," +
                        ":ng_insert_dt," +
                        ":ng_update_dt," +
                        ":ng_record_ver," +
                        ":ng_sv_search_key," +
                        ":ng_sd_search_key" +
                        ")";
                SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
                long phid = idWorker.nextId();
                String fromtype="org";
                String fromorg_id="105200927000002";//目前串固定值，云南建投，后续调整
                String compname=pro.getString("DESC2");
                String simpname="";
                String oldname="";
                String person_flg="4";
                String address=pro.getString("DESC14");
                String enternature_id="0";
                String unisocialcredit=pro.getString("DESC6");
                String taxno="";
                String taxaddress="";
                String taxtelephone="";
                String accstop="0";
                String relorg_id="0";
                String nation_id="0";
                Date ng_insert_dt=now;
                Date ng_update_dt=now;
                String ng_record_ver="1";
                String ng_sv_search_key="10";
                String ng_sd_search_key="105200927000002";
                Object[] paramsInsert = new Object[] { phid,
                        fromtype,
                        fromorg_id,
                        compno,
                        compname,
                        simpname,
                        oldname,
                        person_flg,
                        address,
                        enternature_id,
                        unisocialcredit,
                        taxno,
                        taxaddress,
                        taxtelephone,
                        accstop,
                        relorg_id,
                        nation_id,
                        ng_insert_dt,
                        ng_update_dt,
                        ng_record_ver,
                        ng_sv_search_key,
                        ng_sd_search_key };
                int rows = jdbcTemplate.update(sqlInsert,paramsInsert);
                if(rows>0){
                    rvInfo.setStatus("0");
                    rvInfo.setErrorText("新增记录成功");
                }
                else {
                    rvInfo.setStatus("1");
                    rvInfo.setErrorText("新增记录失败");
                }
            }
            else {
                String sqlUpdate="update fg3_enterprise set " +
                        " compname=:compname," +
                        " address=:address," +
                        " unisocialcredit=:unisocialcredit" +
                        " where compno=:compno ";
                String compname=pro.getString("DESC2");
                String address=pro.getString("DESC14");
                String unisocialcredit=pro.getString("DESC6");
                Object[] paramsUpdate = new Object[] {
                        compname,address,unisocialcredit,compno
                };
                int rows = jdbcTemplate.update(sqlUpdate,paramsUpdate);
                if(rows>0){
                    rvInfo.setStatus("0");
                    rvInfo.setErrorText("更新记录成功");
                }
                else {
                    rvInfo.setStatus("1");
                    rvInfo.setErrorText("更新记录失败");
                }
            }
            return rvInfo;
        }
        catch (Exception ex){
            rvInfo.setStatus("1");
            rvInfo.setErrorText(ex.getMessage());
            return rvInfo;
        }
    }

    /*
     * 功能描述: 将主数据项目状态转换成i8的项目状态
     * @Param: [zt]
     * @Return: java.lang.String
     * @Author: xienb
     * @Date: 2020/10/21
     */
    private  String GetXmzt(String zt){
        String rv="sts";
        if(zt.equals("在建工程")){

        }
        else if(zt.equals("竣工未结算")){

        }
        else if(zt.equals("竣工已结算")){

        }
        else if(zt.equals("签订合同")){

        }
        return rv;
    }
}
