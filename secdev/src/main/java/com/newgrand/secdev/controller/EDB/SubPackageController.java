package com.newgrand.secdev.controller.EDB;

import com.alibaba.fastjson.JSONObject;
import com.newgrand.secdev.domain.EDB.EDBResultModel;
import com.newgrand.secdev.domain.EDB.SubPackageModel;
import com.newgrand.secdev.domain.EDB.SubPackagePlanModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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


    @ApiOperation(value="接收分包计划数据", notes="接收分包计划数据", produces="application/json")
    @RequestMapping(value = "/syncSubPackagePlan",method = RequestMethod.POST)
    public EDBResultModel<ArrayList<EDBResultModel>> syncSubPackagePlan(@RequestBody SubPackagePlanModel param)
    {
        log.info("接收分包计划数据"+ JSONObject.toJSONString(param));
        var result=new EDBResultModel<ArrayList<EDBResultModel>>();
        try {
        } catch (Exception e) {
            result.setCode("1");
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @ApiOperation(value="接收分包策划数据", notes="接收分包策划数据", produces="application/json")
    @RequestMapping(value = "/syncSubPackage",method = RequestMethod.POST)
    public EDBResultModel<ArrayList<EDBResultModel>> syncSubPackage(@RequestBody SubPackageModel param)
    {
        log.info("接收分包策划数据"+ JSONObject.toJSONString(param));
        System.out.println("接收分包策划数据"+ JSONObject.toJSONString(param));
        var result=new EDBResultModel<ArrayList<EDBResultModel>>();
        try {

        }catch (Exception e) {
            result.setCode("1");
            result.setMessage(e.getMessage());
        }
        return result;
    }

}
