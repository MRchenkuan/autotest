/**
 * Title: IfSysMockService.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.service;

import com.gigold.pay.autotest.annotation.IfSysMockHistoryAnnotation;
import com.gigold.pay.autotest.bo.IfSysMock;
import com.gigold.pay.autotest.bo.TestStatus;
import com.gigold.pay.autotest.dao.IfSysMockDAO;
import com.gigold.pay.framework.core.Domain;
import com.gigold.pay.framework.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试状态服务
 */
@Service
public class IfSysTestStatuService extends AbstractService {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Autowired
	IfSysMockDAO ifSysMockDao;

	public TestStatus getTestStatus(){
		TestStatus testStatus = new TestStatus();
		try {
			testStatus = ifSysMockDao.getTestStatus();
		}catch (Exception e){
			e.printStackTrace();
		}

		return testStatus;
	}

	public int updateTestStatus(TestStatus testStatus){
		int count = 0;
		try {
			count = ifSysMockDao.updateTestStatus(testStatus);
		}catch (Exception e){
			e.printStackTrace();
		}
		return count;
	}
}
