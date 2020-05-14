package com.yh.springboot_01.service;

import com.yh.springboot_01.pojo.ResultObject;
import com.yh.springboot_01.pojo.SMSParams;

public interface SendPhoneMessageService {
	
	 public ResultObject sendSMS(SMSParams message);
	 
	
}
