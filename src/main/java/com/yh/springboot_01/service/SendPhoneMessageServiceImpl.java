package com.yh.springboot_01.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yh.springboot_01.dao.MessageMapper;
import com.yh.springboot_01.dao.SMSInfoMapper;
import com.yh.springboot_01.pojo.ResultCode;
import com.yh.springboot_01.pojo.ResultMsg;
import com.yh.springboot_01.pojo.ResultObject;
import com.yh.springboot_01.pojo.SMSInfo;
import com.yh.springboot_01.pojo.SMSParams;

@Service
public class SendPhoneMessageServiceImpl implements SendPhoneMessageService{
    
    @Autowired
    private MessageMapper messageMapper;
    
        /**
     * 发送短信
     * 
     * @param iCode
     *            验证码
     * @param phoneNumber
     *            手机号码
     * @param content
     *            验证内容(手机号码&验证码)
     * 
     * @return 操作结果
     */
	@Override
    public ResultObject sendSMS(SMSParams message)
    {
        ResultObject resultObject = new ResultObject();
        resultObject.setResultCode(ResultCode.FAILED);
        
        //手机号码有效性判断
        if(!isMobile(message.getPhoneNumber())){
            resultObject.setResultMsg(ResultMsg.MSG_CHECKPHONE_FAILED);
            return resultObject;
        }
        
         //验证 发送码，前台将 手机号码&验证码 的字符串发送过来
         //后台也用 手机号码&字符串，这个是调用mysql的md5方法
         //比较不相等，则说明验证失败
        String checkStr = message.getPhoneNumber() + "&" + message.getiCode();
        //调用一次数据库进行md5加密
        //下面调用数据库实际执行的操作是 select md5(Str);
        String md5Data = SMSInfoMapper.getMD5Data(checkStr);
        if(!md5Data.equals(message.getSign()))
        {
            //验证失败
            resultObject.setResultMsg(ResultMsg.MSG_CHECKCODE_FAILED);
            return resultObject;
        }
        
        //获取发送短信所需参数
        // 账号
        String smsAccount = "";
        // 密码
        String smsPwd = "";
        // 接口地址
        String smsURL = "";
        // 发送内容
        String smsContent = "";
        
        //从数据库取发送短信的账号、密码、接口地址和发送内容
        SMSInfo messageInfo = messageMapper.getSMSInfo();
        
        if(messageInfo != null)
        {
            //赋值
            smsAccount = messageInfo.getSmsAccount();
            smsPwd     = messageInfo.getSmsPwd();
            smsURL     = messageInfo.getSmsURL();
            smsContent = messageInfo.getSmsContent();
             
            //发送内容
            //如果取到的内容不为空，则用验证码代替取到的内容中的"CODE"字符串
            //取到的内容应该是: 您注册xx公司的验证码为{CODE}
            if(!smsContent.isEmpty()){
                smsContent = smsContent.replace("{CODE}", message.getiCode());
            }else{
                smsContent =  message.getiCode();
            }
                    
            //发送内容、地址不为空的情况
            if(!smsAccount.isEmpty() && !smsURL.isEmpty() && smsURL.indexOf("{ACCOUNT}") != -1)
            {
                try{
                    smsContent = URLEncoder.encode(smsContent,"UTF-8");
                }catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                //用取到的数据代替设置的
                smsURL = smsURL.replace("{ACCOUNT}", smsAccount)
                        .replace("{PWD}", smsPwd)
                        .replace("{PHONE}", message.getPhoneNumber())
                        .replace("{MESSAGE}",smsContent);
            }else
            {
                //没有取到地址时给个默认的
                smsURL = "http://api.duanxin.cm/?action=send&encode=utf8&username=70208843&password=6d85231f4343aab761c24c550fd8263d&phone="+message.getPhoneNumber()+"&content="+smsContent;
            }
            
            // 发送短息
            System.out.println(smsURL);
            String retValue = requestURL(smsURL,"UTF-8");
            if(retValue.equals("100")){
                //发送成功
                resultObject.setResultCode(ResultCode.SUCCESS);
                resultObject.setResultMsg(ResultMsg.MSG_SEND_SUCCESS);
            }else{
                resultObject.setResultMsg(ResultMsg.MSG_GETSMSINFO_FAILED);
            }
        }else
        {
            resultObject.setResultMsg(ResultMsg.MSG_GETSMSINFO_FAILED);
        }
        
        return resultObject;
    }
    
    
    /** 
     * 手机号验证 
     *  
     * @param  str 
     * @return 验证通过返回true 
     */  
    public static boolean isMobile(String str) {   
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
     // 验证手机号 
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); 
        m = p.matcher(str);
        b = m.matches();
        return b;
    }
    
    /** 
     * 网络请求
     *  
     * @param  String str 请求的网页地址
     * @param  String charSet 编码格式
     * @return String 网页返回值
     */
    private String requestURL(String URLStr, String charSet){
        // 网络的url地址 
        URL url = null;
        // 输入流
        BufferedReader in = null;
        StringBuffer sb = new StringBuffer();
        try{

            url = new URL(URLStr);
            in = new BufferedReader( new InputStreamReader(url.openStream(),charSet) );
            String str = null;
            while((str = in.readLine()) != null) {
                    sb.append(str);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        } finally{
            try{
                if(in!=null) {
                    in.close();
                }
            }catch(Exception ex) {
            }
        }
        String result = sb.toString();
        System.out.println(result);

        return result;
    }


}
	