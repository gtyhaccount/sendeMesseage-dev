package com.yh.springboot_01.pojo;

/**
 * @param smsAccount 发送短信的账号
 * @param smsPwd 发送短信密码
 * @param smsURL 发送短信服务的地址
 * @param smsContent 发送短信的内容
 * @author VKUSER017
 *
 */
public class SMSInfo {
	
      private String smsAccount;
      private String smsPwd;
      private String smsURL;
      private String smsContent;
	public String getSmsAccount() {
		return smsAccount;
	}
	public void setSmsAccount(String smsAccount) {
		this.smsAccount = smsAccount;
	}
	public String getSmsPwd() {
		return smsPwd;
	}
	public void setSmsPwd(String smsPwd) {
		this.smsPwd = smsPwd;
	}
	public String getSmsURL() {
		return smsURL;
	}
	public void setSmsURL(String smsURL) {
		this.smsURL = smsURL;
	}
	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
      
      
}
