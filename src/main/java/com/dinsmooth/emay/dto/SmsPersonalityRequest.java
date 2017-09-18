package com.dinsmooth.emay.dto;

/**
 * 批量短信发送参数
 * @author Frank
 *
 */
public class SmsPersonalityRequest extends SmsBaseRequest {

	private static final long serialVersionUID = 1L;

	private SmsIdAndMobileAndContent[] smses;

	public SmsIdAndMobileAndContent[] getSmses() {
		return smses;
	}

	public void setSmses(SmsIdAndMobileAndContent[] smses) {
		this.smses = smses;
	}
	

}
