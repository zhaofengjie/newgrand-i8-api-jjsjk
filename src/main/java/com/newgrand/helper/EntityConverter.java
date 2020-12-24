package com.newgrand.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EntityConverter {
    /*
     * 功能描述: 设置前端form表单的字段值
     * @Param: [baseData, map]
     * @Return: java.lang.String
     * @Author: xienb
     * @Date: 2020/10/21
     */
    public String SetField(String baseData, HashMap<String,Object> map){
        JSONObject root = JSON.parseObject(baseData);
        JSONObject form = root.getJSONObject("form").getJSONObject("newRow");
        for (String key : map.keySet()) {
            form.put(key,map.get(key));
        }
        return root.toJSONString();
    }

    public HashMap<String,Object> SetFieldMap(String baseData, HashMap<String,Object> map){
        HashMap<String,Object> root = JSON.parseObject(baseData, HashMap.class);
        for (String key : map.keySet()) {
            root.put(key,map.get(key));
        }
        return root;
    }
}
