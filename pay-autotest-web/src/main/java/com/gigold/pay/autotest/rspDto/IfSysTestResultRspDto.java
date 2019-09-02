/**
 * Title: IfSysMockRspDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.rspDto;

import com.gigold.pay.autotest.bo.TestListResults;
import com.gigold.pay.framework.core.ResponseDto;

/**
 * Title: IfSysMockRspDto<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年12月2日下午1:36:56
 *
 */
public class IfSysTestResultRspDto extends ResponseDto {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private TestListResults testListResults;

	public TestListResults getTestListResults() {
		return testListResults;
	}

	public void setTestListResults(TestListResults testListResults) {
		this.testListResults = testListResults;
	}
}
