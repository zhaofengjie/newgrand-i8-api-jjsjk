package com.newgrand.secdev.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.domain.*;
import com.newgrand.secdev.helper.I8Request;
import com.newgrand.secdev.helper.SnowflakeIdWorker;
import com.newgrand.secdev.service.MdmService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mdmApi")
@Api("主数据API接口")
public class MdmController {
    @Autowired
    private MdmService mdmService;

    @ApiOperation(value="项目信息推送", notes="项目信息推送", produces="application/json")
    @RequestMapping(value = "/projectInfo",method = RequestMethod.POST)
    public @ResponseBody JSONObject ProjectInfo(@RequestBody String project)
    {
        return getMdmJsonObject(project,ApiType.ProjectInfo);
    }

    private JSONObject getMdmJsonObject(String reqInfo,ApiType type) {
        JSONObject rv=new JSONObject();
        JSONObject esb=new JSONObject();
        esb.put("CODE","S");
        esb.put("DESC","接收成功");
        try {
            JSONObject projectInfo = JSON.parseObject(reqInfo);
            JSONObject data=new JSONObject();
            JSONObject datainfos = new JSONObject();
            datainfos.put("UUID",projectInfo.getJSONObject("DATA").getString("UUID"));
            JSONArray datainfo=new JSONArray();
            JSONArray dataInfosReq = projectInfo.getJSONObject("DATA").getJSONArray("DATAINFOS");
            for(int i=0;i<dataInfosReq.size();i++){
                JSONObject pro = dataInfosReq.getJSONObject(i);
                DataInfo saveRv = null;
                switch (type){
                    case ProjectInfo:
                        saveRv = mdmService.SaveProject(pro);
                        break;
                    case EnterpriseInfo:
                        saveRv = mdmService.SaveEnterprise(pro);
                        break;
                    default:
                        break;
                }
                datainfo.add(saveRv);
            }
            datainfos.put("DATAINFO",datainfo);
            data.put("DATAINFOS",datainfos);
            esb.put("DATA",data);
            rv.put("ESB",esb);
        }
        catch (Exception e){
            esb.put("CODE","E");
            esb.put("DESC",e.getMessage());
            rv.put("ESB",esb);
            return rv;
        }
        return rv;
    }

    @ApiOperation(value="客商信息推送", notes="客商信息推送", produces="application/json")
    @RequestMapping(value = "/enterpriseInfo",method = RequestMethod.POST)
    public @ResponseBody JSONObject EnterpriseInfo(@RequestBody String enterprise)
    {
        return getMdmJsonObject(enterprise,ApiType.EnterpriseInfo);
    }

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public  @ResponseBody WebRv Test() throws IOException {
        WebRv rv=new WebRv();
        rv.setSuccess(true);
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        rv.setData(df.format(day));
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        long id= idWorker.nextId();
        rv.setMsg(Long.toString(id));
        rv.setMsg("202010302");
        return  rv;
    }

    @RequestMapping(value = "/ping",method = RequestMethod.POST)
    public @ResponseBody String ping(@RequestBody String ipAddress) throws Exception {
        try {
            Socket socket = new Socket();

            JSONObject ipJo = JSON.parseObject(ipAddress);
            socket.connect(new InetSocketAddress(ipJo.getString("ip"), ipJo.getInteger("port")));


            //int  timeOut =  3000 ;  //超时应该在3钞以上
            //boolean status = InetAddress.getByName(ipJo.getString("ip")).isReachable(timeOut);
            // 当返回值是true时，说明host是可用的，false则不可。
            return "false";
        }
        catch (Exception ex)
        {
            return ex.getMessage() +":"+ ex.getStackTrace();
        }

    }
}
