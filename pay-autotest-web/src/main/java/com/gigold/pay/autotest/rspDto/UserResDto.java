package com.gigold.pay.autotest.rspDto;

import com.gigold.pay.autotest.bo.UserInfo;
import com.gigold.pay.framework.core.ResponseDto;

public class UserResDto extends ResponseDto {
 
	private UserInfo userInfo;

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

}
