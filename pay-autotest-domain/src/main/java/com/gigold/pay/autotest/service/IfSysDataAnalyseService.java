/**
 * Title: IfSysAutoTestService.java<br/>
 * Description: <br/>
 * Copyright: Copyright (c) 2015<br/>
 * Company: gigold<br/>
 *
 */
package com.gigold.pay.autotest.service;

import com.gigold.pay.autotest.bo.*;
import com.gigold.pay.autotest.dao.IfSysMockHistoryDAO;
import com.gigold.pay.autotest.dao.InterFaceDao;

import com.gigold.pay.framework.core.Domain;
import com.gigold.pay.framework.service.AbstractService;
import com.gigold.pay.framework.util.common.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 *	数据分析服务
 */
@Service
public class IfSysDataAnalyseService extends AbstractService {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	@Autowired
	IfSysMockHistoryService ifSysMockHistoryService;
	@Autowired
	InterFaceDao interFaceDao;
	@Autowired
	IfSysMockHistoryDAO ifSysMockHistoryDAO;
	@Autowired
	InterFaceService interFaceService;
	@Autowired
	InterFaceSysService interFaceSysService;

	/**
	 * 获取最近N批测试结果
	 * @return 返回结果对象
	 */
	public Map<String, Object> getAnalyse(int jnrCount) {
		return getAnalyse(jnrCount,0);
	}

	/**
	 * 获取最近N批测试结果
	 * @return 返回结果对象
	 */
	public Map<String, Object> getAnalyse(int jnrCount,int ifid) {
		// 构建结果分析
		List<IfSysMockHistory> recentRst;
		if(ifid<=0){
			// 所有结果
			recentRst = ifSysMockHistoryService.getNewestReslutOf(jnrCount);
		}else{
			// 指定接口的结果
			recentRst = ifSysMockHistoryService.getNewestReslutOf(jnrCount,ifid);
		}

		if(recentRst==null||recentRst.size()<=0){
			System.out.println("查询最近的mocks查询结果为空");
			return null;
		}
		Map< String,List<IfSysMockHistory> > mailBuffers = new HashMap<>();
		for(IfSysMockHistory history:recentRst){
			String JNR = history.getJrn();

			// 为每个接收者包装信件
			if(mailBuffers.containsKey(JNR)&&mailBuffers.get(JNR).size()!=0){
				mailBuffers.get(JNR).add(history);
			}else{
				List<IfSysMockHistory> histories = new ArrayList<>();
				histories.add(history);
				mailBuffers.put(JNR,histories);
			}
		}

		// 重新格式化结果数据
		Iterator<Map.Entry<String, List<IfSysMockHistory>>> entries = mailBuffers.entrySet().iterator();
		Comparator<String> comparator = new Comparator<String>(){
			public int compare(String o1, String o2)
			{
				try {
					int n1 = Integer.parseInt(o1);
					int n2 = Integer.parseInt(o2);
					if (n1 == n2)
						return 0;
					else
						return n1-n2;
				}catch (Exception e){
					debug("排序失败");
					e.printStackTrace();
					return 0;
				}

			}};
		Map<String, Map<String, Map<String, Object>>> initedDataSet = new TreeMap<>();
		ArrayList<String> HeadIFID = new ArrayList<>();

		Map<String,String> IfIDNameMap = new TreeMap<>(comparator);// id-名字映射
		Map<String,String> IfIDDsnrMap = new TreeMap<>(comparator); // id-设计者映射
		Map<String,HashMap<String,Object>> IfIDmodlMap = new TreeMap<>(comparator); // id-系统模块映射
		float _passRate = 0;

		// 获取唯一的接口信息列表
		List<InterFaceInfo> distinctIfList = interFaceService.getDistinctIfMap();

//		// 获取忽略清单
//		List<Integer> testIgnoreLists = interFaceService.getIfIdCanNotBeTest();

		// 计算通过率 - 格式化数据
		List<IfSysMockHistory> lastRst = ifSysMockHistoryService.getNewestReslutOf(1);//最近一批数据

		// 初始化各模块数据
		List<InterFaceSysTem> sysInfos = interFaceSysService.getAllSysInfo();
		Map<Integer,String> sysIdNameMap = new HashMap<>();//模块ID-名字
		for(InterFaceSysTem sysInfo : sysInfos){
			sysIdNameMap.put(sysInfo.getId(),sysInfo.getSysName());
		}

		// 接口id-接口信息映射
		Map<Integer,InterFaceInfo> distinctIfMap = new HashMap<>();
		for(InterFaceInfo distinctIf : distinctIfList){
//			// 忽略列表
//			if(testIgnoreLists.contains(distinctIf.getId()))continue;

			// 收集各个模块的接口信息
			String modId= String.valueOf(distinctIf.getIfSysId());
			String ifId = String.valueOf(distinctIf.getId());

			List<String> sys_iflist = new ArrayList<>();
			if(!IfIDmodlMap.containsKey(modId)){
				IfIDmodlMap.put(modId, new HashMap<String, Object>());
				IfIDmodlMap.get(modId).put("sys_iflist", new ArrayList<String>());// 后续收集
				IfIDmodlMap.get(modId).put("sys_name", sysIdNameMap.get(Integer.parseInt(modId)));
				IfIDmodlMap.get(modId).put("sys_ifCount", 0);
				IfIDmodlMap.get(modId).put("sys_caseCount", 0);
				IfIDmodlMap.get(modId).put("sys_mockPassRate", 0);
				IfIDmodlMap.get(modId).put("sys_CCcoverage", 0);
				IfIDmodlMap.get(modId).put("sys_IFcoverage", 0);
			}

			if(IfIDmodlMap.get(modId).get("sys_iflist")!=null){
				sys_iflist = (List<String>)IfIDmodlMap.get(modId).get("sys_iflist");
			}

			if(!sys_iflist.contains(ifId))sys_iflist.add(String.valueOf(ifId));
			Collections.sort(sys_iflist, comparator);

			// 收集接口ID与接口详情的对应关系
			distinctIfMap.put(distinctIf.getId(),distinctIf);

			// 收集接口表头的数据
			HeadIFID.add(String.valueOf(distinctIf.getId()));
		}

		// 去重 - HeadIFID 去重/排序
		for (String aHeadIFID : HeadIFID) {
			String _ifId = String.valueOf(aHeadIFID);
			IfIDNameMap.put(_ifId, _ifId);
		}

		while (entries.hasNext()) {
			Map.Entry<String, List<IfSysMockHistory>> entry = entries.next();
			// 每一批的批号
			String JNR = entry.getKey();
			// 每一批的所有数据
			List<IfSysMockHistory> histMocks = entry.getValue();//[{if1,test1,info1},{if1,test2,info2}]

			// 遍历所有数据,并分装到eachIfset
			Map<String,Map<String,Object>> eachIfSet = new HashMap<>();// {if1 : {null,null,[] },if2 : { }}
			for(IfSysMockHistory eachHisMock:histMocks){

				//{if1,test1,info1}
				//判断eachIfSet是否已经有该接口的数据,若没有,则新建
				String ifId = String.valueOf(eachHisMock.getIfId());

				if(!eachIfSet.containsKey(ifId)){
					eachIfSet.put(ifId,new HashMap<String,Object>());
					eachIfSet.get(ifId).put("ifPassRate", 0f); //后面计算
					eachIfSet.get(ifId).put("ifTestData",new ArrayList<Object>());
				}
				Map<String,Object> eachIfHisDate = eachIfSet.get(ifId);

				// 当前接口的所有原始结果数据存放点
				List<Integer> ifTestData = (List<Integer>) eachIfHisDate.get("ifTestData");

				// 简化mock结果,只存结果,mock都不需要
				ifTestData.add(Integer.parseInt(eachHisMock.getTestResult()));

				// 实时计算当前接口通过率
				float rstSiz = ifTestData.size();//当前单接口集合大小
				if(rstSiz!=0){

					float nowRst = 0;
					try {
						nowRst = StringUtil.isEmpty(eachHisMock.getTestResult())?0:(StringUtil.equals("1",eachHisMock.getTestResult())?1:0);
					}catch (Exception e){
						System.out.println("************** TestResult 为空 ********* 是否有改了数据库  ***:"+eachHisMock.getTestResult());
					}
					float preRst = (float) (eachIfSet.get(ifId).get("ifPassRate"));
					float _rate = ((rstSiz-1)*preRst+nowRst)/rstSiz;
					eachIfHisDate.put("ifPassRate",(float)(Math.round(_rate*100))/100); //实时计算
				}else{
					eachIfHisDate.put("ifPassRate","没有测试数据,无法计算");
				}
			}

			// 将没有数据的接口信息一并组入
			Map<String,Map<String,Object>> _eachIfSet = new HashMap<>();// {if1 : {null,null,[] },if2 : { }}
			for(String ifId : HeadIFID){
				Map<String,Object> ifset = new HashMap<>();
				ifset.put("ifPassRate", 0f); //后面计算
				ifset.put("ifTestData",new ArrayList<Object>());
				_eachIfSet.put(ifId,ifset);
			}
			//拼装
			_eachIfSet.putAll(eachIfSet);

			// 根据取单个还是取多个,输出对应的结果
			if(ifid<=0){
				initedDataSet.put(JNR,_eachIfSet);
			}else{
				Map<String,Map<String,Object>> ___eachIfSet = new HashMap<>();
				___eachIfSet.put(String.valueOf(ifid),_eachIfSet.get(String.valueOf(ifid)));
				initedDataSet.put(JNR,___eachIfSet);
			}
		}
		// 格式化结束

		// 去重 - 替换接口名
		for (String key : IfIDNameMap.keySet()) {
			InterFaceInfo ifinfo = distinctIfMap.get(Integer.parseInt(key));
			if (ifinfo != null) {
				IfIDNameMap.put(key, ifinfo.getIfName());
				IfIDDsnrMap.put(key, ifinfo.getDsname());
			}
		}
		// 去重 - 结束

		// JNR集合
		Set<String> OrderedHeadJNRSet = initedDataSet.keySet();

		// 用例总数
		float mockCount = lastRst.size();

		// 计算当次覆盖率
		float CCprob,CCtot,IFtst=0,IFtot;
		CCprob=0;
		CCtot = lastRst.size();
		for(IfSysMockHistory eachRst:lastRst){
			if(!(eachRst.getTestResult()==null||eachRst.getTestResult().equals("1")||eachRst.getTestResult().equals("0")))
				CCprob++;
			_passRate += ((eachRst.getTestResult()!=null)&&eachRst.getTestResult().equals("1")?1:0);
		}
		// 计算通过率 - 按mock算
		float mockPassRate = 100*_passRate/mockCount;
		String lastJNR = lastRst.get(0).getJrn();
		for(String ifId:initedDataSet.get(lastJNR).keySet()){
			List testData = (List) initedDataSet.get(lastJNR).get(ifId).get("ifTestData");
			if(testData.size()>0){
				IFtst++;
			}
		}
		IFtot = interFaceDao.getAllIfSysCount();

		// 计算当次覆盖率
		float CCcoverage = ( 1 - CCprob/CCtot );
		float IFcoverage = IFtot>0?( IFtst/IFtot ):0;

		// 获取模块下的接口统计数
		List<Map<String,Integer>> modIfCountList = interFaceDao.getAllModIfCount();
		if(modIfCountList==null||modIfCountList.size()<=0){
			modIfCountList = new ArrayList<>();
		}
		Map<Integer,Integer> modIfCountMap = new HashMap<>();
		for(Map<String,Integer> modIfCount:modIfCountList){
			modIfCountMap.put(modIfCount.get("id"),modIfCount.get("count"));
		}

		// 计算各模测试指标
		for(String modID: IfIDmodlMap.keySet()){
			HashMap<String,Object> modeDetail = IfIDmodlMap.get(modID);
			modeDetail.put("sys_ifCount",0);// 接口数计算


			// 用例数计算
			int _mockCt = 0;
			float mod_CCprob=0;//
			float mod__passRate = 0;
			for(IfSysMockHistory alastRst:lastRst){
				if(alastRst.getIfSysId()==Integer.parseInt(modID)){
					// 若出现当前模块的接口,该模块则通过接口数+1
					if(!(alastRst.getTestResult()==null||alastRst.getTestResult().equals("1")||alastRst.getTestResult().equals("0")))
						mod_CCprob++;// 问题记录自增
					// 通过率累计
					mod__passRate += ((alastRst.getTestResult()!=null)&&alastRst.getTestResult().equals("1")?1:0);
					// 用例数自增
					_mockCt++;
					// 接口数自增
				}
			}

			// 计算接口通过数
			List modIfList = (List) IfIDmodlMap.get(modID).get("sys_iflist");
			for(Object modIfId:modIfList){
				try {
					if(ifid<=0){// 当获取批量数据时,才需要求模块通过率
						List ifTestData = (List) initedDataSet.get(lastJNR).get(String.valueOf(modIfId)).get("ifTestData");
						if(ifTestData.size()>0){
							modeDetail.put("sys_ifCount",(int)modeDetail.get("sys_ifCount")+1);
						}
					}
				}catch (Exception e){
					debug("计算接口通过数异常");
				}
			}

			// 计算用例通过率
			float mod_mockPassRate = 100*mod__passRate/_mockCt;
			modeDetail.put("sys_caseCount",_mockCt);

			// 接口覆盖率计算
			int mod_IFtst= (int) IfIDmodlMap.get(modID).get("sys_ifCount");
			float mod_IFtot= Float.parseFloat(String.valueOf(modIfCountMap.get(Integer.parseInt(modID))));
			float mod_IFcoverage = ( mod_IFtst/mod_IFtot );
			modeDetail.put("sys_IFcoverage",(float)(Math.round(mod_IFcoverage*10000))/100);

			// 计算用例覆盖率
			float mod_CCtot=_mockCt;
			float mod_CCcoverage = ( 1 - mod_CCprob/mod_CCtot );
			modeDetail.put("sys_CCcoverage",(float)(Math.round(mod_CCcoverage*10000))/100);
			modeDetail.put("sys_mockPassRate",(float)(Math.round(mod_mockPassRate*100))/100);
		}


		// 存储结果
		Map<String,Object> model = new HashMap<>();
		model.put("initedDataSet", initedDataSet);// 所有数据
		model.put("IfIDNameMap", IfIDNameMap);// 表列头
		model.put("IfIDDsnrMap", IfIDDsnrMap);// 设计者映射
		model.put("IfIDmodlMap", IfIDmodlMap);// 模块接口映射
		model.put("OrderedHeadJNRSet", OrderedHeadJNRSet);//表行头
		// 最近一条JNR
		model.put("lastJNR", lastJNR);
		// 指标数据
		model.put("ifCount", IFtst);
		model.put("caseCount", Math.round(mockCount));
		model.put("jnrCount", jnrCount);
		model.put("mockPassRate", (float)(Math.round(mockPassRate*100))/100);//保留两位
		model.put("CCcoverage", (float)(Math.round(CCcoverage*10000))/100);//保留两位
		model.put("IFcoverage", (float)(Math.round(IFcoverage*10000))/100);//保留两位
		return model;
	}

	/**
	 * 根据接口列表获取测试数据
	 * @param ifIdList 接口id列表
	 * @param jnrCount 批数
     * @return 返回测试数据
     */
	public TestListResults getTestResulteByIfList(int[] ifIdList, int jnrCount){
		TestListResults results = new TestListResults();

		// list 为空时返回空
		if(ifIdList==null || ifIdList.length<=0) return results;

		String ifIdListStr = joinString(ifIdList,",");
		// 得到所需接口的历史
		List<IfSysMockHistory> hisList = ifSysMockHistoryDAO.getHistoryByIfIdList(ifIdListStr,jnrCount);

		// 计算各项指标
		calTest(ifIdList,results,hisList);

		return results;
	}

	/**
	 * 计算各项指标
	 * @param ifIdList 测试清单中的id列表
	 * @param results 出参,格式化的结果
	 * @param hisList 历史记录
	 */
	private void calTest(int[] ifIdList, TestListResults results, List<IfSysMockHistory> hisList) {
		int ifCount,mockCount,ifPassCount,ifFailedCount
				, mockPassCount=0
				, mockFaildCount=0;
		float ifPassRate,mockPassRate;
		List<String> jnrList = new ArrayList<>();
		List<Map> interfaceInfo,moduleInfo,userInfo;
		List<List<List<String>>> dates;

		String ifIdListStr = joinString(ifIdList,",");
		ifCount = ifIdList.length;
		mockCount = ifSysMockHistoryDAO.getMockCountByIfIdList(ifIdListStr);
		interfaceInfo = ifSysMockHistoryDAO.getInterfaceInfoByIdList(ifIdListStr);
		moduleInfo = ifSysMockHistoryDAO.getModuleInfoByIdList(ifIdListStr);
		userInfo = ifSysMockHistoryDAO.getUserInfoByIdList(ifIdListStr);
		List<Integer> ifFaildList = new ArrayList<>();


		// 获取最新的批次号,可以优化
		String lastJnr = hisList.get(hisList.size()-1).getJrn();

		// 计算用例总数,失败用例总数,失败接口总数
		for(IfSysMockHistory his : hisList) {
			// jnr列表
			if(!jnrList.contains(his.getJrn())){
				jnrList.add(his.getJrn());
			}

			if(StringUtil.equals(lastJnr,his.getJrn())){
				// 用例通过统计
				if("1".equals(his.getTestResult())){
					mockPassCount++;
				}

				// 用例失败统计
				if("0".equals(his.getTestResult())){
					mockFaildCount++;
				}

				// 接口失败列表
				if(!"1".equals(his.getTestResult()) && !ifFaildList.contains(his.getIfId())){
					ifFaildList.add(his.getIfId());
				}
			}
		}

		// 计算各项通过率
		ifFailedCount = ifFaildList.size();
		ifPassCount = interfaceInfo.size() - ifFaildList.size();// 实际接口数 - 失败接口数
		ifPassRate = (float)ifPassCount/(float)ifCount;
		mockPassRate = (float)mockPassCount/(float)mockCount;


		// 格式化data
		Collections.sort(jnrList);
		dates = assembleData(jnrList,ifIdList,hisList);

		// 计算各模块的指标
		moduleInfo = calModulesResulte(moduleInfo,interfaceInfo,dates.get(dates.size()-1),ifIdList);

		// 保存结果
		results.setJnrList(jnrList);
		results.setIfIdList(ifIdList);
		results.setMockCount(mockCount);
		results.setMockPassCount(mockPassCount);
		results.setMockFaildCount(mockFaildCount);
		results.setIfPassCount(ifPassCount);
		results.setIfFaildCount(ifFailedCount);
		results.setIfCount(ifCount);
		results.setIfPassRate(ifPassRate);
		results.setMockPassRate(mockPassRate);
		results.setDates(dates);
		results.setInterfaceInfo(interfaceInfo);
		results.setModuleInfo(moduleInfo);
		results.setUserInfo(userInfo);
	}

	/**
	 * 计算各模块指标
	 * @param moduleInfo 模块的基本信息
	 * @param dates 数据清单
	 * @param interfaceInfo  接口信息
     * @return 返回模块基本信息
     */
	private List<Map> calModulesResulte(List<Map> moduleInfo, List<Map> interfaceInfo, List<List<String>> dates, int[] ifIdArray) {
		// 缓存接口信息,减小复杂度
		List<Map> _interfaceInfo = new ArrayList<>();
		_interfaceInfo.addAll(interfaceInfo);

		List<Map> _moduleInfo = new ArrayList<>();
		List<Integer> ifIdList = new ArrayList<>();
		// array 转 list
		for (int ifId : ifIdArray) {
			ifIdList.add(ifId);
		}

		for (Map mdInfo : moduleInfo) {
			int moduleId = (int) mdInfo.get("id");
			List<Map> nowModIfs = new ArrayList<>();
			List<Map> __interfaceInfo = new ArrayList<>();
			__interfaceInfo.addAll(_interfaceInfo);
			// 每个模块的接口
			for (Map ifInfo : __interfaceInfo) {
				if ((int) ifInfo.get("ifSysId") == moduleId) {
					nowModIfs.add(ifInfo);
					_interfaceInfo.remove(ifInfo);// 减小计算复杂度
				}
			}

			mdInfo = calSingleModsResulte(mdInfo,dates,ifIdList,nowModIfs);
			_moduleInfo.add(mdInfo);
		}

		return _moduleInfo;
	}

	/**
	 * 计算单模块各项指标
	 *
	 * @param mdInfo 原始模块数据
	 * @param dates 最近一批测试结果
	 * @param ifIdList 接口id列表
	 * @param nowModIfs 当前模块下所有的接口
     * @return 返回
     */
	private Map calSingleModsResulte(Map<String,Object> mdInfo, List<List<String>> dates, List<Integer> ifIdList, List<Map> nowModIfs) {
		List<String> totolCaseRst = new ArrayList<>();
		int mockPassCount = 0;
		int mockFailedCount = 0;
		int ifPassCount = 0;
		int ifCount= nowModIfs.size();
		int mockCount = 0;
		// 用例合并
		for (Map eachIfInfo : nowModIfs) {
			int ifId = (int) eachIfInfo.get("id");
			// 当前结果的序号
			int rstIndex = ifIdList.indexOf(ifId);
			List<String> rst = dates.get(rstIndex);
			// 接口是否全对
			if(rst!=null && rst.size()>0 && sumArr(rst) == rst.size() ){
				ifPassCount++;
			}
			if(rst!=null)
			totolCaseRst.addAll(rst);
		}

		// 用例总数
		mockCount = totolCaseRst.size();

		// 计算mock通过数
		for(String eachCaseRst : totolCaseRst){
			switch (eachCaseRst){
				case "1":
					mockPassCount++;
					break;
				case "0":
					mockFailedCount++;
					break;
				default:break;
			}
		}

		// 计算总体指标
		float mockPassRate = (float)mockPassCount / (float)totolCaseRst.size();
		float mockFailedRate = (float)mockFailedCount / (float)totolCaseRst.size();
		float ifPassRate = (float)ifPassCount / nowModIfs.size();
		
		mdInfo.put("ifCount",ifCount);
		mdInfo.put("mockCount",mockCount);
		mdInfo.put("mockPassRate",mockPassRate);
		mdInfo.put("mockFailedRate",mockFailedRate);
		mdInfo.put("ifPassRate",ifPassRate);

		return mdInfo;
	}

	/**
	 * 历史数据转三维数组
	 * @param jnrList X轴
	 * @param ifIdList Y轴
	 * @param hisList Z轴
     * @return 三维数组
     */
	private List<List<List<String>>> assembleData(List<String> jnrList, int[] ifIdList, List<IfSysMockHistory> hisList) {
		Map<String,Map<Integer,List<String>>> mapRst = new HashMap<>();
		List<List<List<String>>> reRst = new ArrayList<>();
		// 根据横纵坐标分存到map
		for(IfSysMockHistory his : hisList){

			String nowJnr = his.getJrn();
			int nowIfId = his.getIfId();
			String nowRst = his.getTestResult();

			if(!mapRst.containsKey(his.getJrn())){
				mapRst.put(nowJnr,new HashMap<Integer, List<String>>());
			}
			Map<Integer,List<String>> nowJnrData = mapRst.get(nowJnr);

			if(!nowJnrData.containsKey(nowIfId)){
				nowJnrData.put(nowIfId,new ArrayList<String>());
			}
			nowJnrData.get(nowIfId).add(nowRst);
		}

		// 将map转为三维list
		for(String jnr : jnrList){
			List<List<String>> ofJnr = new ArrayList<>();
			for(int ifId : ifIdList){
				List<String> eachIf = mapRst.get(jnr).get(ifId);
				ofJnr.add(eachIf);
			}
			reRst.add(ofJnr);
		}

		return reRst;
	}


	/**
	 * 格式化数组到字符串方法
	 * @param ifIdList id列表
	 * @param s 分隔符
     * @return 返回
     */
	private String joinString(int[] ifIdList, String s) {
		String str = "";
		if(ifIdList.length<=0) return str;
		for(int ifId : ifIdList){
			str += ifId+s;
		}
		str = str.replaceAll(",*$","");
		return str;
	}


	/**
	 * 数组求和方法
	 * @param arr 数组
	 * @return 数组的和
     */
	private float sumArr(List<String> arr){
		float sum = 0;
		for(String _val : arr){
			int val = Integer.parseInt(_val);
			try{
				sum += val;
			}catch (Exception e){
				e.printStackTrace();
				sum += -1;
			}
		}

		return sum;
	}

}
