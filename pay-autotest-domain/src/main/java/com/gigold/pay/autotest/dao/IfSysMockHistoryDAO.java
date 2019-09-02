/**
 * Title: IfSysMockHistoryDAO.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.dao;

import com.gigold.pay.autotest.bo.IfSysMockHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Title: IfSysMockHistoryDAO<br/>
 * Description: <br/>
 * Company: gigold<br/>
 * @author xiebin
 * @date 2015年12月16日下午1:20:48
 *
 */
public interface IfSysMockHistoryDAO {
	/**
	 * 
	 * Title: addIfSysMockHistory<br/>
	 * Description: 添加测试历史数据记录<br/>
	 * @author xiebin
	 * @date 2015年12月16日下午1:33:17
	 *
	 * @param ifSysMockHistory
	 * @return
	 */
   	public int addIfSysMockHistory(IfSysMockHistory ifSysMockHistory);

	/**
	 * 根据批数查询历史测试记录
	 * @param limit 批数
	 * @return 返回列表
     */
	public List<IfSysMockHistory> getNewestReslutOf(int limit);

	/**
	 * 据批数和接口查询历史测试记录
	 * @param limit 批数
	 * @return 返回列表
     */
	public List<IfSysMockHistory> getNewestReslutByIfIdOf(@Param("limit") int limit,@Param("ifid") int ifid);
	/**
	 * 
	 * Title: getmockhistoryByJrnAndMockId<br/>
	 * Description: 根据批次号和mockID获取用例历史记录<br/>
	 * @author xiebin
	 * @date 2016年1月5日下午12:35:00
	 *
	 * @param ifSysMockHistory
	 * @return
	 */
	public List<IfSysMockHistory> getmockhistoryByJrnAndMockId(IfSysMockHistory ifSysMockHistory);


	/**
	 * 根据接口ID获取结果
	 * @param ifIdList
	 * @param jnrCount
     * @return
     */
	List<IfSysMockHistory> getHistoryByIfIdList(@Param("ifIdList") String ifIdList, @Param("jnrCount") int jnrCount);

	/**
	 * 根据接口ID列表获取mock总数
	 * @param ifIdListStr
	 * @return
     */
	int getMockCountByIfIdList(@Param("ifIdListStr") String ifIdListStr);

	/**
	 * 根据接口ID列表获取接口名字
	 * @param ifIdListStr
	 * @return
     */
	List<Map> getInterfaceInfoByIdList(@Param("ifIdListStr") String ifIdListStr);

	/**
	 * 根据接口ID列表获取系统信息
	 * @param ifIdListStr
	 * @return
     */
	List<Map> getModuleInfoByIdList(@Param("ifIdListStr") String ifIdListStr);

	/**
	 * 根据接口ID列表获取用户信息
	 * @param ifIdListStr
	 * @return
     */
	List<Map> getUserInfoByIdList(@Param("ifIdListStr") String ifIdListStr);
}
