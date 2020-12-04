package com.newgrand.secdev.controller.EDB;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.domain.DataInfo;
import com.newgrand.secdev.domain.EDB.BOQModel;
import com.newgrand.secdev.domain.EDB.EDBResultModel;
import com.newgrand.secdev.domain.EDB.WBSLevel1Model;
import com.newgrand.secdev.service.EDB.BOQService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * BOQ
 * @Author ChenXiangLu
 * @Date 2020/11/25 16:42
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/edbApi")
@Api("经济数据库接口")
public class BOQController {

    @Autowired
    private BOQService boqService;

    @ApiOperation(value="接收收入BOQ数据", notes="接收收入BOQ数据", produces="application/json")
    @RequestMapping(value = "/syncBoqIn",method = RequestMethod.POST)
    public EDBResultModel<String> syncBoqIn(@RequestBody BOQModel param) {
        log.info("接收收入BOQ数据"+ JSONObject.toJSONString(param));
        System.out.println("接收收入BOQ数据"+ JSONObject.toJSONString(param));
        var result = new EDBResultModel<String>();
        try {
            DataInfo dataInfo = boqService.saveBoqCost(param, false);
            result.setData(dataInfo.getCode());
            result.setCode(dataInfo.getStatus());
            result.setMessage(dataInfo.getErrorText());
        } catch (Exception e)
        {
            result.setCode("1");
            result.setMessage(e.getMessage());
            result.setData(param.getCode());
        }
        return result;
    }


    @ApiOperation(value="接收成本BOQ数据", notes="接收成本BOQ数据", produces="application/json")
    @RequestMapping(value = "/syncBoqCost",method = RequestMethod.POST)
    public EDBResultModel<String> syncBoqCost(@RequestBody BOQModel param)
    {
        log.info("接收成本BOQ数据"+ JSONObject.toJSONString(param));
        System.out.println("接收成本BOQ数据"+ JSONObject.toJSONString(param));
        var result=new EDBResultModel<String>();
        try {
        DataInfo dataInfo = boqService.saveBoqCost(param, true);
        result.setData(dataInfo.getCode());
        result.setCode(dataInfo.getStatus());
        result.setMessage(dataInfo.getErrorText());
        } catch (Exception e)
        {
            result.setCode("1");
            result.setMessage(e.getMessage());
            result.setData(param.getCode());
        }
        return result;
    }

}
