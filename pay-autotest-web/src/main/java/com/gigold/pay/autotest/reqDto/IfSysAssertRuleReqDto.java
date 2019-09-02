/**
 * Title: IfStsMockRspListDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.reqDto;

import com.gigold.pay.autotest.bo.IfSysAssertRule;
import com.gigold.pay.framework.core.RequestDto;
import com.gigold.pay.framework.core.exception.OtherExceptionCollect;

import java.util.List;

/**
 * Title: IfStsMockRspListDto<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年12月2日下午3:30:57
 *
 */
public class IfSysAssertRuleReqDto extends RequestDto {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private List<IfSysAssertRule> assertRules;

	public List<IfSysAssertRule> getAssertRules() {
		return assertRules;
	}

	public void setAssertRules(List<IfSysAssertRule> assertRules) {
		this.assertRules = assertRules;
	}

	@Override
	public boolean validate() throws OtherExceptionCollect {
		return true;
	}
}
