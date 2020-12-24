package com.newgrand.domain.EDB;

import lombok.Getter;
import lombok.Setter;

/**
 * 经济数据库_返回结果定义
 * @Author ChenXiangLu
 * @Date 2020/11/25 15:13
 * @Version 1.0
 */
@Getter
@Setter
public class EDBResultModel<T> {

    public EDBResultModel() {

    }
    public EDBResultModel(String code,String message,String devMessage,T data)
    {
        this.code=code;
        this.message=message;
        this.devMessage=devMessage;
        this.data=data;
    }
    /**
     * （成功为0 失败为1）
     */
    private String code;
    /**
     * 消息
     */
    private String message;
    /**
     * 备用消息
     */
    private String devMessage;
    /**
     * 具体返回对象
     */
    private T data;

}
