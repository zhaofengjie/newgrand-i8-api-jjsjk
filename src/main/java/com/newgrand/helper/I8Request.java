package com.newgrand.helper;

import com.alibaba.fastjson.JSON;
import com.newgrand.domain.KillMsg;
import com.newgrand.domain.LoginRt;
import com.newgrand.domain.PostRv;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class I8Request {
    static Map<String,String> _cookieVal=new HashMap<String, String>();

    @Autowired
    private CloseableHttpClient httpClient;
    @Autowired
    private RequestConfig requestConfig;

    @Value("${i8.url}")
    private String i8url;
    @Value("${i8.user}")
    private String i8user;
    @Value("${i8.pwd}")
    private String i8pwd;
    @Value("${i8.database}")
    private String i8database;

    private void console(String str){
        System.out.println(str);
    }

    public String PostFormSync(String url, List<NameValuePair> formdata) throws IOException {
        boolean islogin=true;
        String rv="";
        if(_cookieVal==null||_cookieVal.isEmpty()||_cookieVal.size()==0){
            islogin = DoLogin();
        }
        if(islogin){//登录成功
            console("cookie中键值个数："+_cookieVal.size());
            PostRv postRv=DoPost(url,formdata);
            if(postRv.getSuccess()==false){//会话失效的情况
                islogin = DoLogin();//做一次登录
                if(islogin){
                    postRv=DoPost(url,formdata);//登录成功重新发请求
                    if(postRv.getSuccess()){
                        rv=postRv.getResp();
                    }
                }
            }
            else {
                rv=postRv.getResp();
            }
        }
        return rv;
    }

    public PostRv DoPost(String url, List<NameValuePair> formdata ) throws IOException {
        PostRv rv=new PostRv();
        rv.setSuccess(true);
        String postUrl = i8url+url;
        console("业务请求地址："+postUrl);
        HttpPost httpPost  = new HttpPost(postUrl);
        httpPost.setConfig(requestConfig);
        String cookie=GetCookie();
        console("业务请求cookie值："+cookie);
        httpPost.addHeader("Cookie",cookie);
        HttpEntity entity = new UrlEncodedFormEntity(formdata,"utf-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            String res = ResponseHandle(response);
            rv.setResp(res);
            return rv;
        }
        catch(Exception e){
            console("业务请求报错："+e.getMessage());
            httpPost.abort();
            rv.setSuccess(false);
            return rv;
        }
        finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public boolean DoLogin() throws IOException {
        String resout =  DoLoginFirst(false);
        console("第一次登录返回："+resout);
        LoginRt secrv = JSON.parseObject(resout, LoginRt.class);
        //{ success: false,msg:'{"Message":"您使用的用户名已在[::ffff:172.20.0.172]登录，是否强行登录并清除[::ffff:172.20.0.172]上的登录信息？","IpAddress":"::ffff:172.20.0.172","Devicetype":"1","UserId":"ngsecdev","SessionID":"6399a970-07a4-e978-e442-d54d477adbaa"}'}
        //{ success: false,msg:'登录失败！密码不正确！您还有4次登录机会'}
        //{ success: true,msg:''}
        // 网络错误
        if(secrv==null){
            return false;
        }
        else {
            if(secrv.getSuccess()==false){
                console("登录失败，需要踢人");
                KillMsg km = JSON.parseObject(secrv.getMsg(), KillMsg.class);
                if(km!=null){
                    boolean isKill = KillUser(km);
                    if(isKill){
                        //重新登录
                        String dologinagin = DoLoginFirst(true);
                        console("踢人成功以后再次登录返回："+dologinagin);
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    log.warn("登录接口返回："+secrv.getMsg());
                    return false;
                }
            }
            else {
                return true;
            }
        }
    }

    private String DoLoginFirst(boolean isKill) throws IOException {
        String surl = i8url+"/SUP/Login/WebLogin";
        HttpPost httpPost  = new HttpPost(surl);
        httpPost.setConfig(requestConfig);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("UserID",i8user));
        params.add(new BasicNameValuePair("UserPwd",i8pwd));
        params.add(new BasicNameValuePair("DataBase",i8database));
        params.add(new BasicNameValuePair("Language","zh-CN"));
        if(isKill){
            params.add(new BasicNameValuePair("IsOnlineCheck","1"));
        }
        HttpEntity entity = new UrlEncodedFormEntity(params,"utf-8");

        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        String result="";
        try {
            response = httpClient.execute(httpPost);
            result = ResponseHandle(response);
            SetHeader(response);
        }
        catch(Exception e){
            log.error("url : "+ surl +", msg : " + e.getMessage());
            httpPost.abort();
            result = e.getMessage();
            return result;
        }
        finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    private  boolean KillUser(KillMsg km ) throws IOException {
        String surl = i8url+"/SUP/Login/KillOnlineUser";
        HttpPost httpPost  = new HttpPost(surl);
        httpPost.setConfig(requestConfig);
        List<NameValuePair> param = new ArrayList<>();
        param.add(new BasicNameValuePair("IpAddress",km.getIpAddress()));
        param.add(new BasicNameValuePair("Devicetype", km.getDevicetype()));
        param.add(new BasicNameValuePair("UserId",km.getUserId()));
        param.add(new BasicNameValuePair("SessionID",km.getSessionID()));
        HttpEntity entity = new UrlEncodedFormEntity(param,"utf-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        String result="";
        try {
            response = httpClient.execute(httpPost);
            result = ResponseHandle(response);
            console("踢人接口返回："+result);
            return true;
        }
        catch(Exception e){
            log.warn("踢人报错"+e.getMessage());
            httpPost.abort();
            return false;
        }
        finally {
            if (response != null) {
                response.close();
            }
        }
    }

    private  void  SetHeader(CloseableHttpResponse response){
        Header[] headers = response.getHeaders("Set-Cookie");
        for(int i=0;i<headers.length;i++){
            Header h = headers[i];
            String v = h.getValue();
            //i85.2.0tokenkey=ngtokenkey%24NEWGRAND-WEB%245a9cb49f-6bee-43df-b6a8-83874b936776; expires=Tue, 20 Oct 2020 09:53:44 GMT; path=/
            int i1= v.indexOf(";");
            String kv =v.substring(0,i1);
            String[] kk = kv.split("=");
            if(kk.length==1){
                _cookieVal.put(kk[0],"");
            }
            else {
                _cookieVal.put(kk[0],kk[1]);
            }
        }
    }

    private  String GetCookie(){
        StringBuffer sb=new StringBuffer();
        for(String key : _cookieVal.keySet()){
            sb.append(key+"="+_cookieVal.get(key)+";");
        }
        return sb.toString();
    }

    private String ResponseHandle(CloseableHttpResponse response) {
        String result = "";
        // 获取响应体
        HttpEntity httpEntity = null;
        try {
            // 获取响应状态
            int statusCode = response.getStatusLine().getStatusCode();
            // 没有正常响应
            if (statusCode < HttpStatus.SC_OK || statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                result="调用产品接口失败,状态码:"+statusCode;
                throw new RuntimeException("statusCode : " + statusCode);
            }
            // 获取响应体
            httpEntity = response.getEntity();
            if (httpEntity !=null) {
                result = EntityUtils.toString(httpEntity);
            }

        } catch (Exception e) {
            log.error("HttpClientHelper reponseHandle error", e);
        } finally {
            // 如果httpEntity没有被完全消耗，那么连接无法安全重复使用，将被关闭并丢弃
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
