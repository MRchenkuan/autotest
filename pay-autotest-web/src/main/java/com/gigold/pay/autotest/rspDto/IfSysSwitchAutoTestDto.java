/**
 * Title: IfStsMockRspListDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.rspDto;

import com.gigold.pay.autotest.bo.InterFaceInfo;
import com.gigold.pay.framework.core.ResponseDto;

/**
 * Title: IfStsMockRspListDto<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年12月2日下午3:30:57
 *
 */
public class IfSysSwitchAutoTestDto extends ResponseDto {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private String isAutoTest;

	public String getIsAutoTest() {
		return isAutoTest;
	}

	public void setIsAutoTest(String isAutoTest) {
		this.isAutoTest = isAutoTest;
	}
}
