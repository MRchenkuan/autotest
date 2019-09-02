/**
 * Title: IfStsMockRspListDto.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.rspDto;

import com.gigold.pay.autotest.bo.IfSysAssertCondition;
import com.gigold.pay.autotest.bo.IfSysAssertRule;
import com.gigold.pay.framework.core.ResponseDto;

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
public class IfSysAssertRuleRspDto extends ResponseDto {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	private List<IfSysAssertRule> assertRules;

	private IfSysAssertCondition assertCondition;

	public IfSysAssertCondition getAssertCondition() {
		return assertCondition;
	}

	public void setAssertCondition(IfSysAssertCondition assertCondition) {
		this.assertCondition = assertCondition;
	}

	public List<IfSysAssertRule> getAssertRules() {
		return assertRules;
	}

	public void setAssertRules(List<IfSysAssertRule> assertRules) {
		this.assertRules = assertRules;
	}
}
