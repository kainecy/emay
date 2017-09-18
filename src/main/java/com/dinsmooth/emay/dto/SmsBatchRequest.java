package com.dinsmooth.emay.dto;

/**
 * 批量短信发送参数
 * @author Frank
 *
 */
public class SmsBatchRequest extends SmsBaseRequest {

	private static final long serialVersionUID = 1L;

	/**
	 * 手机号与自定义SmsId
	 */
	private SmsIdAndMobile[] smses;
	
	/**
	 * 短信内容
	 */
	private String content;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public SmsIdAndMobile[] getSmses() {
		return smses;
	}

	public void setSmses(SmsIdAndMobile[] smses) {
		this.smses = smses;
	}

}
