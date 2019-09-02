/**
 * Title: IfSysMockDAO.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.dao;


import com.gigold.pay.autotest.bo.IfSysAssertRule;

import java.util.List;
import java.util.Map;

/**
 * Title: IfSysMockDAO<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * 
 * @author xiebin
 * @date 2015年11月26日下午4:07:08
 *
 */
public interface IfSysAssertRuleDAO {

	/**
	 * 根据mockid获取断言规则
	 * @param mockId
	 * @return
     */
	List<IfSysAssertRule> getAssertRulesByMockId(int mockId);


	/**
	 * 根据规则列表更新规则
	 * @param ifSysAssertRule
	 * @return
     */
	int updateAssertRule(IfSysAssertRule ifSysAssertRule);

	/**
	 * 根据规则id删除规则
	 * @param id
	 * @return
	 */
	boolean deleteAssertRule(int id);


	/**
	 * 比较方式列表
	 * @return
     */
	List<Map> getAsType();

	/**
	 * 比较对象列表
	 * @return
     */
	List<Map> getAsClass();
}
