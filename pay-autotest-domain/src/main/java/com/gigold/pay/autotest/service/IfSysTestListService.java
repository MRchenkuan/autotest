/**
 * Title: IfSysMockService.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.service;


import java.util.List;
import java.util.Map;

import com.gigold.pay.autotest.bo.TestList;
import com.gigold.pay.autotest.dao.IfSysTestListDAO;
import com.gigold.pay.framework.util.common.StringUtil;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gigold.pay.autotest.bo.InterFaceInfo;
import com.gigold.pay.autotest.dao.InterFaceDao;
import com.gigold.pay.framework.bootstrap.SystemPropertyConfigure;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


import java.util.*;

/**
 *	测试清单服务
 */
@Service
public class IfSysTestListService {



	@Autowired
	IfSysTestListDAO ifSysTestListDAO;
	@Autowired
	InterFaceDao interFaceDao;
	@Autowired
	InterFaceService interFaceService;


	/**
	 * 根据id获取测试清单
	 * @param listId
	 * @return
     */
	public TestList getTestListById(int listId){
		TestList testList=null;
		try {
			testList = ifSysTestListDAO.getTestListById(listId);
		}catch (Exception e){
			e.printStackTrace();
		}
		return testList;
	}

	/**
	 * 根据分页,获取所有测试列表中所有的接口
	 * @param curPageNum
	 * @return
	 */
	public PageInfo<InterFaceInfo> getIfsOfAllTestList(int curPageNum) {

		List<InterFaceInfo> list = new Page<>();
		PageInfo<InterFaceInfo> pageInfo = null;
		PageHelper.startPage(curPageNum, Integer.parseInt(SystemPropertyConfigure.getProperty("sys.pageSize")));

		try {
			// 测试清单列表
			List<TestList> testLists = ifSysTestListDAO.getAllTestList();

			// 所有可测接口列表
			List<InterFaceInfo> canBeTestInfo = interFaceService.getAllInterfaceBeTest();
			List<Integer> canBeTestIds = new ArrayList<>();
			for(InterFaceInfo interFaceInfo : canBeTestInfo){
				canBeTestIds.add(interFaceInfo.getId());
			}

			// 求所有测试清单的忽略交集
			List<Integer> canNotTestIds = getInterSections(testLists);

			// 去掉忽略ID
			canBeTestIds.removeAll(canNotTestIds);


			// 初始化接口对象列表
			for(Integer ifId : canBeTestIds){
				InterFaceInfo interFaceInfo = interFaceDao.getSingleIfForTestById(ifId);
				if(interFaceInfo!=null)
				list.add(interFaceInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		}
		if(list!=null){
			pageInfo = new PageInfo<InterFaceInfo>(list);
		}
		return pageInfo;
	}

	/**
	 * 求测试清单的忽略交集
	 * @param testLists
	 * @return
     */
	public static List<Integer> getInterSections(List<TestList> testLists) {
		if(testLists.size()<=0)return new ArrayList<>();

		List<String> rst = Arrays.asList(testLists.get(0).getExcludeIdList().split("[,，]"));

		for(TestList testList:testLists ){
			String excludeIds = testList.getExcludeIdList();
			String[] ifIdArray = {};
			if(StringUtil.isNotEmpty(excludeIds))
				ifIdArray = excludeIds.split("[,，]");
			List<String>ifIdList = Arrays.asList(ifIdArray);
			rst.retainAll(ifIdList);
		}
		List<Integer> rstI = new ArrayList<>();
		for(String ifId : rst){
			int iifId = Integer.parseInt(ifId);
			rstI.add(iifId);
		}

		return rstI;
	}

	/**
	 * 根据测试清单ID获取可测ID列表
	 * @param listId
	 * @return
     */
	public String[] getIfIdArraryByListId(int listId) {
		List<InterFaceInfo> inteFaceInfoList = interFaceDao.getAllInterfaceBeTest();
		List<String> allList = new ArrayList<>();
		for(InterFaceInfo info:inteFaceInfoList){
			allList.add(String.valueOf(info.getId()));
		}

		TestList testList = getTestListById(listId);
		String ignoreStr = testList.getExcludeIdList();
		String[] ignoreArray = {};
		if(StringUtil.isNotEmpty(ignoreStr) ){
			ignoreArray = ignoreStr.split("[,，]");
		}
		List<String> ignoreList = Arrays.asList(ignoreArray);


		// 差集
		allList.removeAll(ignoreList);
		int size = allList.size();
		return allList.toArray(new String[size]);
	}

}
