/**
 * Title: IfSysMockService.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.service;

import com.gigold.pay.autotest.bo.IfSysAssertCondition;
import com.gigold.pay.autotest.bo.IfSysAssertRule;
import com.gigold.pay.autotest.bo.IfSysMock;
import com.gigold.pay.autotest.dao.IfSysAssertRuleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 断言规则服务
 * 用来处理所有断言规则相关的维护
 */
@Service
public class IfSysAssertRuleService {

	@Autowired
	IfSysAssertRuleDAO ifSysAssertRuleDao;


	/**
	 * 根据用例获取断言规则
	 * @param mock 用例
	 * @return
     */
	public List<IfSysAssertRule> getAssertRulesByMock(IfSysMock mock) {
		int mockId = mock.getId();
		List<IfSysAssertRule> rules = new ArrayList<>();
		try{
			rules = ifSysAssertRuleDao.getAssertRulesByMockId(mockId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return rules;
	}

	/**
	 * 根据ID删除断言规则
	 * @param arId 规则id
	 * @return
     */
	public boolean deleteAssertRule(int arId) {
		boolean flag = false;
		try{
			ifSysAssertRuleDao.deleteAssertRule(arId);
			flag = true;
		}catch (Exception e){
			e.printStackTrace();
		}
		return flag;
	}


	/**
	 * 跟新断言规则列表
	 * @param ifSysAssertRules
	 * @return
     */
	public List<IfSysAssertRule> updateAssertRules(List<IfSysAssertRule> ifSysAssertRules) {
		int count=0;
		List<IfSysAssertRule> ifSysAssertRules1 = new ArrayList<>();
		try {
			int mockid=0;
			for(IfSysAssertRule ifSysAssertRule:ifSysAssertRules){
				count = ifSysAssertRuleDao.updateAssertRule(ifSysAssertRule);
				ifSysAssertRules1.add(ifSysAssertRule);
				mockid = ifSysAssertRule.getMockId();
			}
			if(mockid>0)ifSysAssertRules1 = ifSysAssertRuleDao.getAssertRulesByMockId(mockid);
			if(ifSysAssertRules1.size()>0 && count>0){
				return ifSysAssertRules1;
			}else {
				return null;
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取所有的比较条件
	 * @return
     */
	public IfSysAssertCondition getConditions() {
		IfSysAssertCondition conditions = new IfSysAssertCondition();
		List<Map> asType = null,asClass = null;
		try{
			asType = ifSysAssertRuleDao.getAsType();
			asClass = ifSysAssertRuleDao.getAsClass();
		}catch (Exception e){
			e.printStackTrace();
		}
		conditions.setAsType(asType);
		conditions.setAsClass(asClass);

		return conditions;
	}
}
