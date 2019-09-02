package com.gigold.pay.autotest.dao;

import java.util.List;
import java.util.Map;

import com.gigold.pay.autotest.bo.InterFaceInfo;
import com.gigold.pay.autotest.bo.TestList;

public interface InterFaceDao {
	/**
	 * 获得所有接口数
	 * 
	 * @param
	 * @return
	 */
	public int getAllIfSysCount();

	/**
	 * 根据Id获得模块的接口数
	 *
	 * @param
	 * @return
	 */
	public int getAllIfSysCountByMod(int modId);

	/**
	 * 根据Id获得接口信息
	 */
	public InterFaceInfo getInterFaceById(int id);

	/**
	 * 
	 * Title: getAllIfSys<br/>
	 * Description:获取所有的接口信息 <br/>
	 * 
	 * @author xiebin
	 * @date 2015年12月1日下午2:59:07
	 *
	 * @return
	 */
	public List<InterFaceInfo> getAllIfSys(InterFaceInfo interFaceInfo);
	/**
	 * 
	 * Title: getAllIfSys<br/>
	 * Description:获取所有的接口信息 <br/>
	 * 
	 * @author xiebin
	 * @date 2015年12月1日下午2:59:07
	 *
	 * @return
	 */
	public List<InterFaceInfo> getAllIfSysForTest();


	/**
	 * 获取单个被测接口的基本数据
	 */
	public InterFaceInfo getSingleIfForTestById(int id);

	/**
	 * 获取唯一的接口信息
	 * @return
     */
	List<InterFaceInfo> getDistinctIfMap();

	/**
	 * 查询模块下的接口统计数
	 * @return
     */
	List<Map<String,Integer>> getAllModIfCount();

	/**
	 * 获取所有能测试的接口数据
	 * @return
     */
	List<InterFaceInfo> getAllInterfaceBeTest();

	/**
	 * 根据测试清单,获取所有的可测ID清单
	 * @return
     */
	List<TestList> getIfIdCanBeTest();

	/**
	 * 更新接口信息
	 * @param interFaceInfo
	 * @return
     */
	int updateInterfaceInfo(InterFaceInfo interFaceInfo);
}
